package com.conexa.swapi.conn.dto;

import com.conexa.swapi.shared.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapiItemResponse extends BaseResponse {
    private String id;
    private String name;
}
