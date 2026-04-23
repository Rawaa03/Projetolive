package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopPerformerDTO {
    
    private String id;
    private String name;
    private Double performanceScore;
    private Double productionKg;
    private String unit;
    private Integer rank;
}
