package com.blindaje.modules.user.api;

import com.blindaje.config.security.JwtTokenProvider;
import com.blindaje.modules.user.domain.Role;
import com.blindaje.modules.user.domain.User;
import com.blindaje.modules.user.dto.UserResponse;
import com.blindaje.modules.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setup(@Valid @RequestBody SetupRequest request) {
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> listarUsuarios(HttpServletRequest request) {
        try {
            String token = extraerToken(request);
            String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
            
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
    @PreAuthorize("hasRole('ADMIN')")
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

    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "El nombre completo no puede estar vacío")
    private String fullName;

    @NotBlank(message = "El rol no puede estar vacío")
    private String role;

    @NotBlank(message = "El tenantId no puede estar vacío")
    private String tenantId;

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getTenantId() { return tenantId; }
}
}