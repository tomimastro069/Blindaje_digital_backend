package com.blindaje.modules.auth.service;

import com.blindaje.config.security.JwtTokenProvider;
import com.blindaje.modules.auth.dto.LoginRequest;
import com.blindaje.modules.auth.dto.LoginResponse;
import com.blindaje.modules.user.domain.User;
import com.blindaje.modules.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtTokenProvider.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getId(),
                user.getTenantId()
        );

        return new LoginResponse(token, null);
    }
}