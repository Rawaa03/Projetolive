package com.example.demo.repository;

import com.example.demo.model.Verger;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.enums.StatutVerger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface VergerRepository extends MongoRepository<Verger, String> {

    List<Verger> findByEstSupprimerFalse();

    List<Verger> findByStatut(StatutVerger statut);

    List<Verger> findByAgriculteur(Utilisateur agriculteur);
    List<Verger> findByAgriculteurId(String agriculteurId);
    @Query("{ 'agriculteur' : ?0, 'estSupprimer' : false }")
    List<Verger> findActiveByAgriculteurId(ObjectId agriculteurId);
    @Query("{ 'agriculteur': ?0 }")
    List<Verger> findByAgriculteurRef(String agriculteurId);
    @Query(value = "{ 'agriculteur' : ?0 }", exists = true)
    boolean existsByAgriculteurId(ObjectId agriculteurId);
}