package com.example.demo.model;

import com.example.demo.model.enums.StatutCollecte;
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

/**
 * Collecte = a harvest campaign.
 * 
 * The verger is known through the tournées (each tournée has a verger).
 * All tournées in a collecte must belong to the SAME verger.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "collectes")
public class Collecte {

    @Id
    private String id;

    private String code;
    private StatutCollecte statut;

    // ── Linked entities ─────────────────────────────────────────────────────
    // NO verger here! It's accessed via tournees
    
    @Builder.Default
    @DocumentReference(lazy = true)
    private List<Tournee> tournees = new ArrayList<>();

    // ── Campaign dates ───────────────────────────────────────────────────────
    private Date dateDebutCampagne;
    private Date dateFinCampagne;

    // ── Campaign statistics (cached for performance) ────────────────────────
    private Double quantiteTotaleKg;
    private Double rendementMoyenParArbre;

    // ── Metadata ─────────────────────────────────────────────────────────────
    private String observations;
    
    @CreatedDate
    private Date dateCreation;
}