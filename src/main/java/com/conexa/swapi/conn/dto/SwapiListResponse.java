package com.conexa.swapi.conn.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapiListResponse {
    private String message;
    private Integer total_records;
    private List<SwapiItem> results;
}
