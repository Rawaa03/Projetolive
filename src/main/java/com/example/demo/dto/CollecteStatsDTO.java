package com.example.demo.dto;

import com.example.demo.model.enums.StatutCollecte;
import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class CollecteStatsDTO {
    private String collecteId;
    private String code;
    private StatutCollecte statut;
    private String annee;
    private Integer nbreTournees;
    private Double quantiteTotaleKg;
    private Integer totalArbresRecoltes;
    private Double rendementMoyenParArbre;
    private Double efficaciteMoyenne;
    private Date dateDebutCampagne;
    private Date dateFinCampagne;
    private Boolean estCloturee;
}