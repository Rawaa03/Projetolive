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

/**
 * Tournée = one round trip through part of a verger with a tractor + benne + workers.
 *
 * Business rules encoded here:
 *  - A verger can have MANY tournées (depends on superficie).
 *  - Each tournée has exactly ONE verger, ONE benne, ONE tracteur,
 *    and ONE-or-MORE travailleurs.
 *  - Each completed tournée generates ONE collecte (quantiteReelle kg).
 *  - The total harvest of a verger = sum of all its tournées' collectes.
 *  - NB_ARBRES_PAR_TOURNEE = 200 trees per tournée
 *    (a standard 1-tonne benne handles ~5 kg/tree × 200 trees = 1 000 kg — perfect fit).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tournees")
public class Tournee {

    // ── Recommended default: 200 trees per tournée ──────────────────────────
    // Rationale:
    //   • Average Chemlali / Chétoui yield at maturity: 4-6 kg/tree/season.
    //   • A standard 1-tonne benne (1 000 kg capacity) is therefore filled by
    //     ~200 trees (200 × 5 kg = 1 000 kg).
    //   • This means 1 benne ↔ 1 tournée ↔ 200 trees — clean 1-to-1 mapping.
    //   • For a verger of 1 000 trees → 5 tournées, each with its own collecte.
    public static final int NB_ARBRES_PAR_TOURNEE = 200;

    @Id
    private String id;

    // Human-readable code: T-20251012-001
    private String code;

    private StatutTournee statut;

    // ── Linked entities (stored as IDs to avoid infinite loops) ─────────────
    @DocumentReference(lazy = true)
    private Verger verger;                  // mandatory – one verger per tournée

    private String benneId;                 // ID of the benne used
    private String tracteurId;              // ID of the tracteur used
    @Builder.Default
    private List<String> travailleurIds = new ArrayList<>();  // one or more workers

    // ── Schedule ─────────────────────────────────────────────────────────────
    private Date dateDebut;
    private Date dateFin;

    // ── Field data ───────────────────────────────────────────────────────────
    @Builder.Default
    private Integer nbreArbre = NB_ARBRES_PAR_TOURNEE;  // can be adjusted per tournée

    private Double distanceTotale;          // km travelled during this tournée
    private Integer tempsTotal;             // minutes (computed on terminer())

    // ── Harvest result ───────────────────────────────────────────────────────
    private Double quantiteCollecteeKg;     // kg actually collected this tournée
    private Boolean collecteFinalisee;      // true once terminer() is called

    // ── Metadata ─────────────────────────────────────────────────────────────
    private String observations;

    @CreatedDate
    private Date dateCreation;

    // ════════════════════════════════════════════════════════════════════════
    // Business methods
    // ════════════════════════════════════════════════════════════════════════

    /** Start the tournée (sets statut → EN_COURS and records dateDebut). */
    public void demarrer() {
        if (this.statut != StatutTournee.PLANIFIEE) {
            throw new IllegalStateException(
                    "Seule une tournée PLANIFIÉE peut être démarrée. Statut actuel : " + this.statut);
        }
        this.statut = StatutTournee.EN_COURS;
        this.dateDebut = new Date();
    }

    /**
     * Finish the tournée.
     * Computes tempsTotal and marks collecteFinalisee = true.
     * The actual quantiteCollecteeKg must be set before calling this
     * (or passed as a parameter via the service layer).
     */
    public void terminer(Double quantiteKg) {
        if (this.statut != StatutTournee.EN_COURS) {
            throw new IllegalStateException(
                    "Seule une tournée EN_COURS peut être terminée. Statut actuel : " + this.statut);
        }
        this.statut = StatutTournee.TERMINEE;
        this.dateFin = new Date();
        this.quantiteCollecteeKg = quantiteKg != null ? quantiteKg : 0.0;
        this.collecteFinalisee = true;

        if (this.dateDebut != null) {
            long diffMs = this.dateFin.getTime() - this.dateDebut.getTime();
            this.tempsTotal = (int) (diffMs / (1000 * 60)); // minutes
        }
    }

    /** Cancel the tournée. */
    public void annuler() {
        if (this.statut == StatutTournee.TERMINEE) {
            throw new IllegalStateException("Une tournée TERMINÉE ne peut pas être annulée.");
        }
        this.statut = StatutTournee.ANNULEE;
        this.dateFin = new Date();
        this.collecteFinalisee = false;
    }

    /**
     * Efficiency score 0-100.
     * Formula: (kg collected) / (km × hours) × scaling factor.
     * Higher = more olives per km per hour.
     */
    public double calculerEfficacite() {
        if (tempsTotal == null || tempsTotal == 0
                || distanceTotale == null || distanceTotale == 0
                || quantiteCollecteeKg == null || quantiteCollecteeKg == 0) {
            return 0.0;
        }
        double heures = tempsTotal / 60.0;
        double efficacite = (quantiteCollecteeKg / (distanceTotale * heures)) * 10.0;
        return Math.min(efficacite, 100.0);
    }

    /** Estimated yield for this tournée based on nbreArbre and verger rendement. */
    public double estimerQuantite(Double rendementEstimeParArbre) {
        if (rendementEstimeParArbre == null || nbreArbre == null) return 0.0;
        return nbreArbre * rendementEstimeParArbre;
    }
}