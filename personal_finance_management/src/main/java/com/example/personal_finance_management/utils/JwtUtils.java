package com.example.personal_finance_management.utils;

import com.example.personal_finance_management.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public JwtUtils(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public String extractEmail(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return tokenProvider.getUsernameFromJWT(token);
        }
        throw new IllegalArgumentException("Invalid token format");
    }
}