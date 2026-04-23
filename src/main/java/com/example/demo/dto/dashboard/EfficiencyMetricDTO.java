package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EfficiencyMetricDTO {
    
    private String metricName;
    private Double value;
    private String unit;
    private Double benchmark;
    private String status; // GOOD, WARNING, POOR
}
