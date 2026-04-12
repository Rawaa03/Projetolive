package com.example.demo.service;

import com.example.demo.dto.TerminerTourneeRequest;
import com.example.demo.dto.TourneeRequest;
import com.example.demo.dto.TourneeResponse;
import com.example.demo.model.StatutTournee;
import com.example.demo.model.Tournee;

import java.util.List;

public interface TourneeService {

    /** Create a new PLANIFIEE tournée. */
    TourneeResponse creer(TourneeRequest request);

    /** Get one tournée by id (DTO). */
    TourneeResponse getById(String id);

    /** Get raw Tournee entity by id (used internally by other services). */
    Tournee getTourneeById(String id);

    /** List all tournées. */
    List<TourneeResponse> getAll();

    /** List tournées by verger. */
    List<TourneeResponse> getByVerger(String vergerId);

    /** List tournées by statut. */
    List<TourneeResponse> getByStatut(StatutTournee statut);

    /** List active tournées (PLANIFIEE or EN_COURS). */
    List<TourneeResponse> getActive();

    /** Move statut to EN_COURS and set dateDebut = now. */
    TourneeResponse demarrer(String id);

    /**
     * Move statut to TERMINEE, record collected kg, update benne charge,
     * update verger statut if all trees are done.
     */
    TourneeResponse terminer(String id, TerminerTourneeRequest request);

    /** Cancel the tournée. */
    TourneeResponse annuler(String id);

    /** Update planning fields (only while PLANIFIEE). */
    TourneeResponse mettreAJour(String id, TourneeRequest request);

    /** Delete (only while PLANIFIEE or ANNULEE). */
    void supprimer(String id);

    /** Sum of quantiteCollecteeKg of all TERMINEE tournées for a given verger. */
    Double getTotalCollecteParVerger(String vergerId);

    /** How many tournées are needed to cover all trees of a verger? */
    int calculerNbTourneesNecessaires(String vergerId);
}