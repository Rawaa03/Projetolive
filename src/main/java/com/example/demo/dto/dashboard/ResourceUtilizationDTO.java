package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceUtilizationDTO {
    
    private Integer totalBennes;
    private Integer activeBennes;
    private Double benneUtilizationPercent;
    
    private Integer totalTracteurs;
    private Integer activeTracteurs;
    private Double tracteurUtilizationPercent;
    
    private Double costPerKg;
    private Double vehicleMaintenanceCost;
    private String lastResourceUpdate;
}
