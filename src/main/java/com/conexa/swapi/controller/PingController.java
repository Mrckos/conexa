package com.conexa.swapi.controller;

import com.conexa.swapi.shared.dto.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/ping")
    public BaseResponse ping() {
        return new BaseResponse();
    }
}
