package com.conexa.swapi.auth.dto;

import com.conexa.swapi.shared.dto.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Respuesta de autenticación")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse extends BaseResponse {
    private String token;
}
