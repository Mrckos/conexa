package com.conexa.swapi.auth.service;

import com.conexa.swapi.auth.dto.AuthRequest;
import com.conexa.swapi.auth.dto.AuthResponse;
import com.conexa.swapi.auth.dto.RegisterRequest;
import com.conexa.swapi.auth.entity.User;
import com.conexa.swapi.auth.repository.UserRepository;
import com.conexa.swapi.shared.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) throws BaseException {
        if (repository.existsByUsername(req.getUsername()))
            throw new BaseException(1, "El usuario ya existe");

        User user = User.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                .build();

        repository.save(user);

        return new AuthResponse(jwtService.generateToken(user.getUsername()));
    }

    public AuthResponse login(AuthRequest req) throws BaseException {
        User user = repository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BaseException(2, "Usuario no encontrado"));

        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new BaseException(3, "Password incorrecto");

        return new AuthResponse(jwtService.generateToken(user.getUsername()));
    }
}
