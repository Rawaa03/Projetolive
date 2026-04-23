package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VergerProductionDTO {
    
    private String vergerId;
    private String vergerName;
    private Double actualProduction;
    private Double targetProduction;
    private Double percentageOfTarget;
    private String status;
}
