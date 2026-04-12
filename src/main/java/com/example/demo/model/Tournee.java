package com.example.demo.model;

import com.example.demo.model.enums.StatutVerger;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tournees")
public class Tournee {

    // Recommended default: 200 trees per tournée
    // 200 trees × 5 kg/tree avg yield = 1 000 kg → fills one standard 1-tonne benne
    public static final int NB_ARBRES_PAR_TOURNEE = 200;

    @Id
    private String id;

    private String code;                // e.g. T-20251012-001

    private StatutTournee statut;       // PLANIFIEE | EN_COURS | TERMINEE | ANNULEE

    @DocumentReference(lazy = true)
    private Verger verger;              // one verger per tournée

    private String benneId;             // ID of the assigned benne
    private String tracteurId;          // ID of the assigned tracteur

    @Builder.Default
    private List<String> travailleurIds = new ArrayList<>();  // one or more worker IDs

    private Date dateDebut;
    private Date dateFin;

    @Builder.Default
    private Integer nbreArbre = NB_ARBRES_PAR_TOURNEE;  // adjustable per tournée

    private Double distanceTotale;      // km
    private Integer tempsTotal;         // minutes (dateDebut → dateFin)

    private Double quantiteCollecteeKg; // actual kg harvested this tournée
    private Boolean collecteFinalisee;  // true once tournée is TERMINEE

    private String observations;

    @CreatedDate
    private Date dateCreation;
}