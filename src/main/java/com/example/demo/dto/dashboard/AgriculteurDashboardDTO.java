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
public class AgriculteurDashboardDTO {
    
    private String utilisateurId;
    private String nom;
    private String prenom;
    
    // Verger Overview
    private List<VergerCardDTO> vergers;
    private Integer totalVergers;
    private Integer vergerActifs;
    
    // Production Stats
    private Double totalProductionKg;
    private Double targetProductionKg;
    private Double productionPercentage;
    
    // Collection Tours
    private List<TourneeCardDTO> activeTournees;
    private Integer totalActiveTournees;
    
    // Progress Chart Data
    private List<VergerProgressDTO> vergerProgressData;
    
    // Alerts and Tasks
    private List<AlertDTO> alerts;
    private Integer totalAlerts;
    
    // Summary Stats
    private Integer totalArbreCollecte;
    private Double moyenneRendementParArbre;
}
