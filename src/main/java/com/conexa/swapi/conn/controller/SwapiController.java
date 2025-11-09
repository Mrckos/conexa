package com.conexa.swapi.conn.controller;

import com.conexa.swapi.conn.service.SwapiService;
import com.conexa.swapi.shared.dto.BaseResponse;
import com.conexa.swapi.shared.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SwapiController {

    private final SwapiService service;
    private static final Set<String> ALLOWED = Set.of("people", "films", "starships", "vehicles");


    @GetMapping("/{resource}")
    public BaseResponse list(
            @PathVariable String resource,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String id
    ) throws BaseException {

        if (!ALLOWED.contains(resource)) {
            throw new BaseException(5, "Recurso inválido. Usa: people, films, starships, vehicles");
        }

        if (id != null && !id.isBlank()) {
            return service.getById(resource, id);
        }
        return service.list(resource, page, size, name);
    }
}
