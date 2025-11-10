package com.conexa.swapi.conn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Item SWAPI")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapiItem {
    private String uid;
    private String name;
    private String url;
}
