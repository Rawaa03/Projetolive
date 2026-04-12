package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ressources")
public class Ressource {

    @Id
    private String id;

    private String nom;

    private TypeRessource type;   // TRACTEUR or BENNE only

    private String statut;        // DISPONIBLE | OCCUPE | MAINTENANCE

    private String immatriculation;

    // ===== BENNE fields =====
    private Double capaciteKg;
    private Double quantiteChargeeActuelle;
    private Double tauxRemplissage;
    private Boolean estPleine;
    private String tracteurAttacheId;

    // ===== TRACTEUR fields =====
    private String puissance;
    private String carburant;
    private Double consommationHoraire;
    private Boolean aRemorque;
    private Double kilometrage;
    private String conducteurId;

    // Track which tournées this resource was used in (IDs only — no circular ref)
    private List<String> tourneeIds = new ArrayList<>();

    // ===== Business methods =====

    public boolean estBenne()    { return this.type == TypeRessource.BENNE; }
    public boolean estTracteur() { return this.type == TypeRessource.TRACTEUR; }
    public boolean estDisponible() { return "DISPONIBLE".equals(this.statut); }
    public boolean estEnTournee()  { return "OCCUPE".equals(this.statut); }

    public int getNombreTournees() {
        return this.tourneeIds != null ? this.tourneeIds.size() : 0;
    }

    public void ajouterTourneeId(String tourneeId) {
        if (this.tourneeIds == null) this.tourneeIds = new ArrayList<>();
        this.tourneeIds.add(tourneeId);
    }

    /** Add kg to this benne's current load. */
    public void ajouterCharge(Double quantite) {
        if (this.type != TypeRessource.BENNE) {
            throw new UnsupportedOperationException("Seules les bennes peuvent recevoir des charges");
        }
        if (this.quantiteChargeeActuelle == null) this.quantiteChargeeActuelle = 0.0;

        double nouvelleCharge = this.quantiteChargeeActuelle + quantite;
        if (nouvelleCharge > this.capaciteKg) {
            throw new IllegalArgumentException("Dépassement de capacité de la benne");
        }
        this.quantiteChargeeActuelle = nouvelleCharge;
        this.estPleine = nouvelleCharge >= this.capaciteKg;
        if (this.capaciteKg != null && this.capaciteKg > 0) {
            this.tauxRemplissage = (nouvelleCharge / this.capaciteKg) * 100;
        }
    }

    /** Empty this benne. */
    public void vider() {
        if (this.type == TypeRessource.BENNE) {
            this.quantiteChargeeActuelle = 0.0;
            this.tauxRemplissage = 0.0;
            this.estPleine = false;
        }
    }

    public double getQuantiteCollectee() {
        return (this.type == TypeRessource.BENNE && this.quantiteChargeeActuelle != null)
                ? this.quantiteChargeeActuelle : 0.0;
    }
}