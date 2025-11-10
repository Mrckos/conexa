package com.conexa.swapi.auth.service;

import com.conexa.swapi.auth.dto.AuthRequest;
import com.conexa.swapi.auth.dto.AuthResponse;
import com.conexa.swapi.auth.dto.RegisterRequest;
import com.conexa.swapi.auth.entity.User;
import com.conexa.swapi.auth.repository.UserRepository;
import com.conexa.swapi.shared.exception.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // register
    @Test
    void register_ok_generaToken() throws BaseException {
        RegisterRequest req = new RegisterRequest("luke", "force");

        when(repository.existsByUsername("luke")).thenReturn(false);
        when(encoder.encode("force")).thenReturn("hashForce");
        when(jwtService.generateToken("luke")).thenReturn("tokenX");

        AuthResponse resp = authService.register(req);

        Assertions.assertEquals("tokenX", resp.getToken());
        verify(repository).save(any(User.class));
        verify(jwtService).generateToken("luke");
    }

    @Test
    void register_userExiste() {
        RegisterRequest req = new RegisterRequest("luke", "force");

        when(repository.existsByUsername("luke")).thenReturn(true);

        BaseException ex = Assertions.assertThrows(
                BaseException.class,
                () -> authService.register(req)
        );

        Assertions.assertEquals(1, ex.getCode());
        Assertions.assertEquals("El usuario ya existe", ex.getMessage());
        verify(repository, never()).save(any());
    }

    // login()
    @Test
    void login_ok_generaToken() throws BaseException {
        AuthRequest req = new AuthRequest("luke", "force");

        User user = User.builder()
                .username("luke")
                .password("hashForce")
                .build();

        when(repository.findByUsername("luke")).thenReturn(Optional.of(user));
        when(encoder.matches("force", "hashForce")).thenReturn(true);
        when(jwtService.generateToken("luke")).thenReturn("tokenABC");

        AuthResponse resp = authService.login(req);

        Assertions.assertEquals("tokenABC", resp.getToken());
        verify(jwtService).generateToken("luke");
    }

    @Test
    void login_userNotFound() {
        AuthRequest req = new AuthRequest("luke", "force");

        when(repository.findByUsername("luke")).thenReturn(Optional.empty());

        BaseException ex = Assertions.assertThrows(
                BaseException.class,
                () -> authService.login(req)
        );

        Assertions.assertEquals(2, ex.getCode());
        Assertions.assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void login_wrongPassword() {
        AuthRequest req = new AuthRequest("luke", "wrong");

        User user = User.builder()
                .username("luke")
                .password("hashForce")
                .build();

        when(repository.findByUsername("luke")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "hashForce")).thenReturn(false);

        BaseException ex = Assertions.assertThrows(
                BaseException.class,
                () -> authService.login(req)
        );

        Assertions.assertEquals(3, ex.getCode());
        Assertions.assertEquals("Password incorrecto", ex.getMessage());
    }
}
