package com.conexa.swapi.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "Respuesta paginada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasePagResponse<T> extends BaseResponse {
    private Integer page;
    private Integer size;
    private Long total;
    private List<T> items;
}
