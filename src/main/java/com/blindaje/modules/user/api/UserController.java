package com.blindaje.modules.user.api;

import com.blindaje.config.security.JwtTokenProvider;
import com.blindaje.modules.user.domain.Role;
import com.blindaje.modules.user.domain.User;
import com.blindaje.modules.user.dto.UserResponse;
import com.blindaje.modules.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/setup")
    public ResponseEntity<?> setup(@RequestBody SetupRequest request) {
        User user = userService.crearUsuario(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFullName(),
                Role.valueOf(request.getRole()),
                request.getTenantId()
        );
        return ResponseEntity.ok("Usuario creado con id: " + user.getId());
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listarUsuarios(HttpServletRequest request) {
        try {
            System.out.println(">>> Llegó al controller listarUsuarios");
            String token = extraerToken(request);
            System.out.println(">>> Token extraido: " + (token != null ? "ok" : "null"));
            String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
            System.out.println(">>> tenantId: " + tenantId);
            List<UserResponse> usuarios = userService.listarUsuariosPorTenant(tenantId)
                    .stream()
                    .map(u -> new UserResponse(
                            u.getId(), u.getUsername(), u.getEmail(),
                            u.getFullName(), u.getRole(), u.getStatus(), u.getTenantId()
                    ))
                    .toList();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            System.out.println(">>> ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        UserResponse usuario = userService.eliminarUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Token no encontrado");
    }

    static class SetupRequest {
        private String username;
        private String password;
        private String email;
        private String fullName;
        private String role;
        private String tenantId;

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public String getTenantId() { return tenantId; }
    }
}