package com.conexa.swapi.conn.service;

import com.conexa.swapi.conn.client.SwapiClient;
import com.conexa.swapi.conn.dto.*;
import com.conexa.swapi.shared.dto.BasePagResponse;
import com.conexa.swapi.shared.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwapiService {

    private final SwapiClient client;

    private List<SwapiDTO> filmsCache = null;

    public SwapiItemResponse getById(String resource, String id) throws BaseException {
        var response = client.getById(resource, id);

        if (response == null || response.getResult() == null) {
            throw new BaseException(4, "No se encontró el ID");
        }

        var result = response.getResult();
        Map<String, Object> props = result.getProperties();

        // Resolver nombre para todos los recursos: name o title (films)
        String displayName = null;
        if (props != null) {
            Object n = props.get("name");
            if (n == null) n = props.get("title");
            if (n != null) displayName = n.toString();
        }
        if (displayName == null || displayName.isBlank()) {
            displayName = result.getName();
        }
        if (displayName == null || displayName.isBlank()) {
            throw new BaseException(4, "No se encontró el ID");
        }

        return new SwapiItemResponse(result.getUid(), displayName);
    }

    public BasePagResponse<SwapiDTO> list(String resource, Integer page, Integer size, String name) throws BaseException {

        if (resource.equals("films")) {
            return listFilmsPaged(page, size, name);
        }

        var response = client.getList(resource, page, size);

        if (response == null || response.getResults() == null) {
            throw new BaseException(6, "El recurso no soporta paginación o no hay resultados");
        }

        List<SwapiDTO> mapped =
                response.getResults().stream()
                        .map(x -> new SwapiDTO(x.getUid(), x.getName()))
                        .toList();

        if (name != null && !name.isBlank()) {
            String q = name.toLowerCase();
            mapped = mapped.stream()
                    .filter(x -> x.getName() != null && x.getName().toLowerCase().contains(q))
                    .toList();
        }

        BasePagResponse<SwapiDTO> resp = new BasePagResponse<>();
        resp.setCode(0);
        resp.setMessage("OK");
        resp.setPage(page);
        resp.setSize(size);
        resp.setTotal(response.getTotal_records() != null ? response.getTotal_records().longValue() : mapped.size());
        resp.setItems(mapped);

        return resp;
    }

    private BasePagResponse<SwapiDTO> listFilmsPaged(Integer page, Integer size, String name) throws BaseException {

        // cargar cache si no existe
        if (filmsCache == null) {
            filmsCache = loadAllFilms();
        }

        List<SwapiDTO> filtered = filmsCache;

        if (name != null && !name.isBlank()) {
            String q = name.toLowerCase();
            filtered = filtered.stream()
                    .filter(x -> x.getName() != null && x.getName().toLowerCase().contains(q))
                    .toList();
        }

        int total = filtered.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);

        if (from >= total) {
            throw new BaseException(7, "La página solicitada está fuera de rango");
        }

        List<SwapiDTO> items = filtered.subList(from, to);

        BasePagResponse<SwapiDTO> resp = new BasePagResponse<>();
        resp.setCode(0);
        resp.setMessage("OK");
        resp.setPage(page);
        resp.setSize(size);
        resp.setTotal((long) total);
        resp.setItems(items);

        return resp;
    }

    private List<SwapiDTO> loadAllFilms() throws BaseException {
        // Films = del 1 al 7
        List<String> ids = List.of("1","2","3","4","5","6","7");

        return ids.stream()
                .map(id -> {
                    try {
                        var resp = getById("films", id);
                        return new SwapiDTO(resp.getId(), resp.getName());
                    } catch (BaseException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
