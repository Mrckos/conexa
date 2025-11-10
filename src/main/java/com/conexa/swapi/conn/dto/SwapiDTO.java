package com.conexa.swapi.conn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Elemento resumido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapiDTO {
    private String id;
    private String name;
}
