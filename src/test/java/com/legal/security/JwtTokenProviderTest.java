package com.legal.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long expiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", expiration);
    }

    @Test
    @DisplayName("Should generate valid JWT token from email")
    void shouldGenerateValidToken() {
        String token = jwtTokenProvider.generateTokenFromEmail("test@example.com", 1L, "Ayush Test");
        assertNotNull(token);
        assertTrue(token.length() > 20);

        boolean isValid = jwtTokenProvider.validateToken(token);
        assertTrue(isValid);

        String email = jwtTokenProvider.getEmailFromToken(token);
        assertEquals("test@example.com", email);
    }

    @Test
    @DisplayName("Should reject invalid or malformed JWT token")
    void shouldRejectMalformedToken() {
        String malformedToken = "eyJhbGciOiJIUzI1NiJ9.invalidpayload.signature";
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);
        assertFalse(isValid);
    }
}
