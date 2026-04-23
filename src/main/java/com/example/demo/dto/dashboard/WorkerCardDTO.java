package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerCardDTO {
    
    private String id;
    private String nom;
    private String prenom;
    private String type;
    private String specialite;
    private String statut;
    private Date dateEmbauche;
    private Double salaireJournalier;
    private String currentAssignment;
}
