package com.conexa.swapi.conn.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapiGenericResponse {
    private String message;
    private SwapiGenericResult result;
}
