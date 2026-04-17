package com.example.demo.dto;

import com.example.demo.model.enums.StatutCollecte;
import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class CollecteResponse {
    private String id;
    private String code;
    private StatutCollecte statut;
    private String annee;           // ← Vérifie que c'est "annee"
    private Integer numero;
    private String vergerId;
    private Date dateDebutCampagne;
    private Date dateFinCampagne;
    private Integer nbreTournees;
    private Double quantiteTotaleKg;
    private Integer totalArbresRecoltes;
    private Double rendementMoyenParArbre;
    private Double efficaciteMoyenne;
    private String observations;
    private Boolean estCloturee;
    private Date dateCreation;
}