package com.conexa.swapi.auth.dto;

import com.conexa.swapi.shared.dto.BaseResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse extends BaseResponse {
    private String token;
}
