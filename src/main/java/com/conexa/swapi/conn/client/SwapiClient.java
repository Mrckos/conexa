package com.conexa.swapi.conn.client;

import com.conexa.swapi.conn.dto.SwapiGenericResponse;
import com.conexa.swapi.conn.dto.SwapiListResponse;
import com.conexa.swapi.shared.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class SwapiClient {

    private final WebClient swapiWebClient;

    public SwapiListResponse getList(String resource, int page, int limit) throws BaseException {
        try {
            return swapiWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + resource)
                            .queryParam("page", page)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(SwapiListResponse.class)
                    .block();
        } catch (Exception ex) {
            throw new BaseException(9, "Error consultando listado SWAPI: " + ex.getMessage());
        }
    }

    public SwapiGenericResponse getById(String resource, String id) throws BaseException {
        try {
            return swapiWebClient.get()
                    .uri("/" + resource + "/" + id)
                    .retrieve()
                    .bodyToMono(SwapiGenericResponse.class)
                    .block();
        } catch (Exception ex) {
            throw new BaseException(9, "Error consultando SWAPI por ID: " + ex.getMessage());
        }
    }
}
