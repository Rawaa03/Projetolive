package com.example.demo.dto;

import com.example.demo.model.StatutTournee;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class TourneeResponse {

    private String id;
    private String code;
    private StatutTournee statut;

    // Verger info
    private String vergerId;
    private String vergerTypeOlive;
    private String vergerAgriculteurNom;
    private Double vergerSuperficie;

    // Benne info
    private String benneId;
    private String benneNom;
    private Double benneCapaciteKg;

    // Tracteur info
    private String tracteurId;
    private String tracteurNom;
    private String tracteurImmatriculation;

    // Travailleurs
    private List<String> travailleurIds;
    private List<String> travailleurNoms;

    // Planning window (planned or actual after demarrer/terminer)
    private Date dateDebut;
    private Date dateFin;

    // Field data
    private Integer nbreArbre;
    private Double  distanceTotale;
    private Integer tempsTotal;         // minutes, set on terminer

    // Harvest result
    private Double  quantiteCollecteeKg;
    private Boolean collecteFinalisee;
    private Double  efficacite;         // 0–100

    // Metadata
    private String observations;
    private Date   dateCreation;

    // Verger aggregate
    private Double totalCollecteVergerKg;  // sum of all TERMINEE tournées for same verger
}