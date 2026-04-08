package com.example.demo.repository;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RessourceRepository extends MongoRepository<Ressource, String> {
    
    // Find all resources by type
    List<Ressource> findByType(TypeRessource type);
    
    // Find all resources by status
    List<Ressource> findByStatut(String statut);
    
    // Find resources by type and status
    List<Ressource> findByTypeAndStatut(TypeRessource type, String statut);
    
    // Find by immatriculation
    Optional<Ressource> findByImmatriculation(String immatriculation);
    
    // Find all BENNE type resources
    List<Ressource> findByTypeEquals(TypeRessource type);
}
