package com.example.demo.repository;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RessourceRepository extends MongoRepository<Ressource, String> {

    List<Ressource> findByType(TypeRessource type);
    List<Ressource> findByStatut(String statut);
    List<Ressource> findByTypeAndStatut(TypeRessource type, String statut);
    Optional<Ressource> findByImmatriculation(String immatriculation);
    List<Ressource> findByNomContainingIgnoreCase(String nom);

    // ── Benne queries ──────────────────────────────────────────────
    @Query("{ 'type': 'BENNE' }")
    List<Ressource> findAllBennes();

    @Query("{ 'type': 'BENNE', 'statut': ?0 }")
    List<Ressource> findBennesByStatut(String statut);

    @Query("{ 'type': 'BENNE', 'tracteurAttacheId': ?0 }")
    List<Ressource> findBennesByTracteur(String tracteurId);

    @Query("{ 'type': 'BENNE', 'estPleine': true }")
    List<Ressource> findFullBennes();

    @Query("{ 'type': 'BENNE', 'tauxRemplissage': { $gte: ?0, $lte: ?1 } }")
    List<Ressource> findBennesByFillPercentage(Double min, Double max);

    // ── Tracteur queries ───────────────────────────────────────────
    @Query("{ 'type': 'TRACTEUR' }")
    List<Ressource> findAllTracteurs();

    @Query("{ 'type': 'TRACTEUR', 'statut': ?0 }")
    List<Ressource> findTracteursByStatut(String statut);

    @Query("{ 'type': 'TRACTEUR', 'conducteurId': ?0 }")
    List<Ressource> findTracteursByDriver(String driverId);

    @Query("{ 'type': 'TRACTEUR', 'carburant': ?0 }")
    List<Ressource> findTracteursByFuel(String fuel);

    // ── Availability ───────────────────────────────────────────────
    @Query("{ 'statut': 'DISPONIBLE' }")
    List<Ressource> findAllAvailable();

    @Query("{ 'statut': 'DISPONIBLE', 'type': ?0 }")
    List<Ressource> findAvailableByType(TypeRessource type);

    @Query("{ 'statut': 'MAINTENANCE' }")
    List<Ressource> findAllUnderMaintenance();

    @Query("{ 'type': ?0, 'statut': 'MAINTENANCE' }")
    List<Ressource> findUnderMaintenanceByType(TypeRessource type);
}