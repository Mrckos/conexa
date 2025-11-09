package com.conexa.swapi.conn.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapiResult<T> {
    private String message;
    private Integer total_records;
    private T result;
}
