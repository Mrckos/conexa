package com.conexa.swapi.auth.controller;

import com.conexa.swapi.auth.dto.AuthRequest;
import com.conexa.swapi.auth.dto.RegisterRequest;
import com.conexa.swapi.auth.service.AuthService;
import com.conexa.swapi.shared.dto.BaseResponse;
import com.conexa.swapi.shared.exception.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Endpoints de autenticación")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @Operation(summary = "Registrar usuario nuevo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Usuario ya existe")
    })
    @PostMapping("/register")
    public BaseResponse register(@RequestBody RegisterRequest req) throws BaseException {
        return service.register(req);
    }

    @Operation(summary = "Login de usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public BaseResponse login(@RequestBody AuthRequest req) throws BaseException {
        return service.login(req);
    }
}
