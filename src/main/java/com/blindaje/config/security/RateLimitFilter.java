package com.blindaje.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitFilter extends OncePerRequestFilter {

    // Máximo de intentos por IP en la ventana de tiempo
    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60000; // 1 minuto

    // Mapa: IP → [contador, timestamp inicio ventana]
    private final ConcurrentHashMap<String, long[]> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Solo aplicar rate limit al endpoint de login
        if (!request.getRequestURI().equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        requestCounts.compute(ip, (key, value) -> {
            if (value == null || now - value[1] > WINDOW_MS) {
                // Nueva ventana
                return new long[]{1, now};
            }
            value[0]++;
            return value;
        });

        long[] data = requestCounts.get(ip);
        if (data[0] > MAX_REQUESTS) {
            response.setStatus(429);
            response.getWriter().write("Demasiados intentos. Espera 1 minuto.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}