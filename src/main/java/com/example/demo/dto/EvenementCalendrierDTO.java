package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class EvenementCalendrierDTO {
    private String id;
    private String titre;
    private Date debut;
    private Date fin;
    
    // Verger details
    private String vergerId;
    private String vergerNom;
    private String vergerTypeOlive;
    private Double vergerSuperficie;
    private Integer vergerNbArbre;
    private String vergerStatut;
    
    // Agriculteur details
    private String agriculteurId;
    private String agriculteurNom;
    private String agriculteurPrenom;
    private String agriculteurEmail;
    private String agriculteurTelephone;
    
    // Tournée details
    private List<String> travailleursNoms;
    private List<String> travailleurIds;
    private String statut;
    private String couleur;
    private Double quantiteCollecteeKg;
    private Integer nbreArbre;
    private Double distanceTotale;
    private String collecteId;
    private String collecteCode;
    private String observations;
    private Date dateCreation;
}