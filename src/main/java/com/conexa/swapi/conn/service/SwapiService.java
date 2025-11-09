package com.conexa.swapi.conn.service;

import com.conexa.swapi.conn.client.SwapiClient;
import com.conexa.swapi.conn.dto.*;
import com.conexa.swapi.shared.dto.BasePagResponse;
import com.conexa.swapi.shared.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwapiService {

    private final SwapiClient client;

    public BasePagResponse<SwapiDTO> list(String resource, Integer page, Integer size, String name) throws BaseException {

        var response = client.getList(resource, page, size);

        List<SwapiDTO> mapped = response.getResults().stream()
                .map(x -> new SwapiDTO(x.getUid(), x.getName()))
                .collect(Collectors.toList());

        // Filtro local
        if (name != null && !name.isBlank()) {
            mapped = mapped.stream()
                    .filter(x -> x.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        BasePagResponse<SwapiDTO> resp = new BasePagResponse<>();
        resp.setCode(0);
        resp.setMessage("OK");
        resp.setPage(page);
        resp.setSize(size);
        resp.setTotal(response.getTotal_records().longValue());
        resp.setItems(mapped);

        return resp;
    }

    public SwapiItemResponse getById(String resource, String id) throws BaseException {

        var response = client.getById(resource, id);

        if (response.getResult() == null) {
            throw new BaseException(4, "No se encontró el ID");
        }

        var result = response.getResult();

        String name = null;

        if (result.getProperties() != null && result.getProperties().get("name") != null) {
            name = result.getProperties().get("name").toString();
        } else {
            name = result.getName();
        }

        return new SwapiItemResponse(result.getUid(), name);
    }
}
