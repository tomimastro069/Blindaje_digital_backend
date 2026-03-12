package com.blindaje.config.security;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    // TODO: Implement JWT token generation, validation, and parsing

    public String generateToken(String username) {
        return null;
    }

    public boolean validateToken(String token) {
        return false;
    }

    public String getUsernameFromToken(String token) {
        return null;
    }
}
