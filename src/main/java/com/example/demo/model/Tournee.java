package com.example.demo.model;

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

    // 200 trees × ~5 kg/tree avg yield = 1 000 kg → fills one standard 1-tonne benne
    public static final int NB_ARBRES_PAR_TOURNEE = 200;

    @Id
    private String id;

    private String code;            // e.g. T-20251012-001

    private StatutTournee statut;   // PLANIFIEE | EN_COURS | TERMINEE | ANNULEE

    @DocumentReference(lazy = true)
    private Verger verger;          // one verger per tournée

    @DocumentReference(lazy = true)
    private Ressource benne;        // the benne used for this tournée

    @DocumentReference(lazy = true)
    private Ressource tracteur;     // the tracteur used for this tournée

    @DocumentReference(lazy = true)
    @Builder.Default
    private List<Utilisateur> travailleurs = new ArrayList<>();  // one or more workers

    private Date dateDebut;
    private Date dateFin;

    @Builder.Default
    private Integer nbreArbre = NB_ARBRES_PAR_TOURNEE;

    private Double distanceTotale;      // km
    private Integer tempsTotal;         // minutes (dateDebut → dateFin)

    private Double quantiteCollecteeKg; // actual kg harvested this tournée
    private Boolean collecteFinalisee;  // true once tournée is TERMINEE

    private String observations;

    @CreatedDate
    private Date dateCreation;
}