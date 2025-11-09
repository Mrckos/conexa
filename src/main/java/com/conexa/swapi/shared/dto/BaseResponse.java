package com.conexa.swapi.shared.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {

    private Integer code = 0;
    private String message = "OK";
}
