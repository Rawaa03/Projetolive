package com.example.demo.dto;

import com.example.demo.model.enums.StatutCollecte;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollecteResponse {

    private String id;
    private String code;
    private StatutCollecte statut;
    
    // Verger info (déduit des tournées)
    private String vergerId;
    private String vergerNom;
    
    // Tournées info
    private Integer nombreTournees;
    private Integer nombreTourneesTerminees;
    private List<String> tourneeIds;
    
    // Dates
    private Date dateDebutCampagne;
    private Date dateFinCampagne;
    
    // Statistiques
    private Double quantiteTotaleKg;
    private Double rendementMoyenParArbre;
    private Integer nombreArbresTotal;
    private Integer nombreTourneesPrevues;
    
    // Métadonnées
    private String observations;
    private Date dateCreation;
}