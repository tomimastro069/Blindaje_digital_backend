package com.blindaje.modules.user.service;

import com.blindaje.modules.user.domain.Role;
import com.blindaje.modules.user.domain.User;
import com.blindaje.modules.user.domain.UserStatus;
import com.blindaje.modules.user.repository.UserRepository;
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
}