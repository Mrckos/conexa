package com.conexa.swapi.auth.controller;

import com.conexa.swapi.auth.dto.AuthRequest;
import com.conexa.swapi.auth.dto.RegisterRequest;
import com.conexa.swapi.auth.service.AuthService;
import com.conexa.swapi.shared.dto.BaseResponse;
import com.conexa.swapi.shared.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public BaseResponse register(@RequestBody RegisterRequest req) throws BaseException {
        return service.register(req);
    }

    @PostMapping("/login")
    public BaseResponse login(@RequestBody AuthRequest req) throws BaseException {
        return service.login(req);
    }
}
