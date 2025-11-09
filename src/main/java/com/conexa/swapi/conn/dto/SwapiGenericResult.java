package com.conexa.swapi.conn.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapiGenericResult {
    private String uid;
    private String name;
    private String url;
    private Map<String, Object> properties;
}
