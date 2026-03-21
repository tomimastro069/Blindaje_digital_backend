package com.blindaje.modules.user.service;

import com.blindaje.modules.user.domain.Role;
import com.blindaje.modules.user.domain.User;
import com.blindaje.modules.user.domain.UserStatus;
import com.blindaje.modules.user.dto.UserResponse;
import com.blindaje.modules.user.repository.UserRepository;
import com.blindaje.modules.user.dto.UserResponse;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User crearUsuario(String username, String password, String email,
                              String fullName, Role role, String tenantId) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario ya existe: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // BCrypt
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setTenantId(tenantId);

        return userRepository.save(user);
    }
    public List<User> listarUsuariosPorTenant(String tenantId) {
    return userRepository.findByTenantId(tenantId);
    }

    public UserResponse eliminarUsuario(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        UserResponse response = new UserResponse(
            user.getId(), user.getUsername(), user.getEmail(),
            user.getFullName(), user.getRole(), user.getStatus(), user.getTenantId()
        );

        userRepository.deleteById(id);
     return response;
    }
        

}