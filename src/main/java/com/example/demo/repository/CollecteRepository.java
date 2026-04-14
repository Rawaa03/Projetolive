package com.example.demo.repository;

import com.example.demo.model.Collecte;
import com.example.demo.model.enums.StatutCollecte;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollecteRepository extends MongoRepository<Collecte, String> {
    
    List<Collecte> findByStatut(StatutCollecte statut);
    
    List<Collecte> findByStatutIn(List<StatutCollecte> statuts);
    
    List<Collecte> findByTourneesVergerId(String vergerId);
}