package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VergerProgressDTO {
    
    private String vergerId;
    private String vergerName;
    private Double collectedKg;
    private Double estimatedKg;
    private Double progressPercentage;
    private String statut;
    private Integer maturite;
}
