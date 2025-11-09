package com.conexa.swapi.conn.client;

import com.conexa.swapi.conn.dto.SwapiGenericResponse;
import com.conexa.swapi.conn.dto.SwapiListResponse;
import com.conexa.swapi.shared.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BaseException(4, "Recurso o página no encontrada");
            }
            throw new BaseException(9, "Error consultando listado SWAPI: " + e.getStatusCode());
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
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BaseException(4, "No se encontró el ID");
            }
            throw new BaseException(9, "Error consultando SWAPI por ID: " + e.getStatusCode());
        } catch (Exception ex) {
            throw new BaseException(9, "Error consultando SWAPI por ID: " + ex.getMessage());
        }
    }
}
