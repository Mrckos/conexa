package com.conexa.swapi.shared.dto;

import lombok.*;

import java.util.List;

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
