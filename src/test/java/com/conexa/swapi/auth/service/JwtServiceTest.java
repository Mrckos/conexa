package com.conexa.swapi.auth.service;

import com.conexa.swapi.shared.exception.BaseException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET",
                "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "EXPIRATION",
                3600000L);
    }

    @Test
    void generateToken_ok() {
        String token = jwtService.generateToken("luke");
        Assertions.assertNotNull(token);
    }

    @Test
    void getUsernameFromToken_ok() {
        String token = jwtService.generateToken("luke");
        String username = jwtService.getUsernameFromToken(token);
        Assertions.assertEquals("luke", username);
    }

    @Test
    void getUsernameFromToken_tokenInvalido() {
        Assertions.assertThrows(
                Exception.class,
                () -> jwtService.getUsernameFromToken("token.invalido")
        );
    }
}
