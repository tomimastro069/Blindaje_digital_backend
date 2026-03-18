package com.blindaje.modules.user.api;

import com.blindaje.modules.user.domain.Role;
import com.blindaje.modules.user.domain.User;
import com.blindaje.modules.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint temporal para crear el primer usuario admin.
     * Una vez que tengas el primer admin creado, securizá este endpoint
     * agregando .requestMatchers("/api/users/setup").hasRole("ADMIN")
     * en el SecurityConfig.
     */
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

    // DTO interno
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