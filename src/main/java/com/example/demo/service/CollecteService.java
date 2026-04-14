package com.example.demo.service;

import com.example.demo.dto.CollecteRequest;
import com.example.demo.dto.CollecteResponse;
import com.example.demo.model.Collecte;
import com.example.demo.model.enums.StatutCollecte;

import java.util.List;

public interface CollecteService {

    /**
     * Create a new harvest campaign for a verger.
     * Automatically calculates number of tournées needed based on verger.nbArbre.
     */
    CollecteResponse creer(CollecteRequest request);

    /**
     * Get one collecte by id (DTO).
     */
    CollecteResponse getById(String id);

    /**
     * Get raw Collecte entity by id (used internally by other services).
     */
    Collecte getCollecteById(String id);

    /**
     * List all collectes.
     */
    List<CollecteResponse> getAll();

    /**
     * List collectes by verger.
     */
    List<CollecteResponse> getByVerger(String vergerId);

    /**
     * List collectes by statut.
     */
    List<CollecteResponse> getByStatut(StatutCollecte statut);

    /**
     * List active collectes (PLANIFIEE or EN_COURS).
     */
    List<CollecteResponse> getActive();

    /**
     * Start the harvest campaign (PLANIFIEE → EN_COURS).
     */
    CollecteResponse demarrer(String id);

    /**
     * Finish the harvest campaign (EN_COURS → TERMINEE).
     * Only possible if ALL tournées in this collecte are TERMINEE.
     * Automatically marks the verger as RECOLTE.
     */
    CollecteResponse terminer(String id);

    /**
     * Cancel the harvest campaign (PLANIFIEE or EN_COURS → ANNULEE).
     */
    CollecteResponse annuler(String id);

    /**
     * Update collecte information (only while PLANIFIEE).
     */
    CollecteResponse mettreAJour(String id, CollecteRequest request);

    /**
     * Delete collecte (only if no tournées or all tournées are ANNULEE).
     */
    void supprimer(String id);

    /**
     * Add a tournée to this collecte.
     */
    CollecteResponse ajouterTournee(String collecteId, String tourneeId);

    /**
     * Update total harvest quantity (called when a tournée is terminated).
     */
    void mettreAJourQuantiteTotale(String collecteId, Double quantiteKg);

    /**
     * Calculate total harvest from all terminated tournées.
     */
    Double calculerQuantiteTotale(String collecteId);

    /**
     * Check if all tournées in this collecte are TERMINEE.
     */
    boolean isComplete(String collecteId);

    /**
     * Get the verger of this collecte (from the first tournée).
     */
    String getVergerIdByCollecte(String collecteId);
}