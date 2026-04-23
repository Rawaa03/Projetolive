package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDTO {
    
    // User Management Overview
    private Integer totalUsers;
    private Map<String, Integer> usersByRole;
    private Integer activeUsers;
    private Integer inactiveUsers;
    
    // All Vergers Overview
    private Integer totalVergers;
    private List<VergerCardDTO> allVergers;
    private Map<String, Integer> vergersByStatus;
    
    // Resource Utilization
    private ResourceUtilizationDTO resourceUtilization;
    
    // System-wide Production
    private Double totalSystemProductionKg;
    private Integer totalSystemCollectionTours;
    private Double systemAverageProductionPerVerger;
    
    // Alerts and Issues
    private List<AlertDTO> systemAlerts;
    private Map<String, Integer> alertsByLevel;
    private Integer totalAlerts;
    
    // Performance Benchmarking
    private List<TopPerformerDTO> topPerformingVergers;
    private List<TopPerformerDTO> topPerformingResponsables;
    private List<BottleneckDTO> bottlenecks;
    
    // Additional Metrics
    private Double systemEfficiencyRating;
    private Integer totalWorkers;
}
