package com.example.demo.repository;

import com.example.demo.model.StatutTournee;
import com.example.demo.model.Tournee;
import com.example.demo.model.Verger;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourneeRepository extends MongoRepository<Tournee, String> {

    Optional<Tournee> findByCode(String code);

    List<Tournee> findByStatut(StatutTournee statut);

    List<Tournee> findByVerger(Verger verger);

    // Find all tournées for a verger by verger ID
    @Query("{ 'verger.$id': { $oid: ?0 } }")
    List<Tournee> findByVergerId(String vergerId);

    // Find only TERMINEE tournées for a verger (used for total harvest calculation)
    @Query("{ 'verger.$id': { $oid: ?0 }, 'statut': 'TERMINEE' }")
    List<Tournee> findTermineesByVergerId(String vergerId);

    // Find tournées where a specific travailleur is assigned
    @Query("{ 'travailleurs.$id': { $oid: ?0 } }")
    List<Tournee> findByTravailleurId(String travailleurId);

    // Find tournées where a specific benne is assigned
    @Query("{ 'benne.$id': { $oid: ?0 } }")
    List<Tournee> findByBenneId(String benneId);

    // Find tournées where a specific tracteur is assigned
    @Query("{ 'tracteur.$id': { $oid: ?0 } }")
    List<Tournee> findByTracteurId(String tracteurId);

    // Active tournées (not yet finished or cancelled)
    @Query("{ 'statut': { $in: ['PLANIFIEE', 'EN_COURS'] } }")
    List<Tournee> findActive();

    boolean existsByCode(String code);
}