package com.conexa.swapi.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Respuesta base")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {

    private Integer code = 0;
    private String message = "OK";
}
