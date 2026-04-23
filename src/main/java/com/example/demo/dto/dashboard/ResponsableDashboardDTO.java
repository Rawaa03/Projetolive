package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsableDashboardDTO {
    
    private String utilisateurId;
    private String nom;
    private String prenom;
    
    // Assigned Vergers Overview
    private List<VergerCardDTO> assignedVergers;
    private Integer totalAssignedVergers;
    
    // Worker Management
    private Integer totalWorkers;
    private Integer activeWorkers;
    private List<WorkerCardDTO> workers;
    
    // Collection Progress
    private Double totalCollectionKg;
    private Double targetCollectionKg;
    private Double collectionPercentage;
    
    // Tours/Tournees Status
    private Integer planifiedTournees;
    private Integer ongoingTournees;
    private Integer completedTournees;
    private List<TourneeCardDTO> recentTournees;
    
    // Production Stats vs Targets
    private List<VergerProductionDTO> vergerProduction;
    
    // Quality and Efficiency Ratings
    private Double averageKgPerWorker;
    private Double efficiencyRating;
    private List<EfficiencyMetricDTO> efficiencyMetrics;
    
    // Alerts
    private List<AlertDTO> alerts;
    private Integer totalAlerts;
}
