package com.example.demo.service;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import com.example.demo.repository.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RessourceService {
    
    @Autowired
    private RessourceRepository ressourceRepository;
    
    /**
     * Create a new resource with a unique ID
     */
    public Ressource create(Ressource ressource) {
        // MongoDB will auto-generate an ID if one is not provided
        if (ressource.getId() == null) {
            // ID will be generated automatically
        }
        
        // Ensure required fields are set
        if (ressource.getType() == null) {
            throw new IllegalArgumentException("Type de ressource est obligatoire");
        }
        
        if (ressource.getStatut() == null) {
            ressource.setStatut("DISPONIBLE");
        }
        
        // Save and return - MongoDB will generate a new ID each time
        return ressourceRepository.save(ressource);
    }
    
    /**
     * Get all resources
     */
    public List<Ressource> getAll() {
        return ressourceRepository.findAll();
    }
    
    /**
     * Get all resources by type
     */
    public List<Ressource> getByType(TypeRessource type) {
        return ressourceRepository.findByType(type);
    }
    
    /**
     * Get resource by ID
     */
    public Optional<Ressource> getById(String id) {
        return ressourceRepository.findById(id);
    }
    
    /**
     * Update a resource
     */
    public Ressource update(String id, Ressource ressource) {
        Optional<Ressource> existing = ressourceRepository.findById(id);
        
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Ressource non trouvée: " + id);
        }
        
        Ressource current = existing.get();
        
        // Update only provided fields
        if (ressource.getNom() != null) {
            current.setNom(ressource.getNom());
        }
        if (ressource.getStatut() != null) {
            current.setStatut(ressource.getStatut());
        }
        if (ressource.getImmatriculation() != null) {
            current.setImmatriculation(ressource.getImmatriculation());
        }
        
        // For BENNE specific fields
        if (ressource.getCapaciteKg() != null) {
            current.setCapaciteKg(ressource.getCapaciteKg());
        }
        if (ressource.getTauxRemplissage() != null) {
            current.setTauxRemplissage(ressource.getTauxRemplissage());
        }
        if (ressource.getQuantiteChargeeActuelle() != null) {
            current.setQuantiteChargeeActuelle(ressource.getQuantiteChargeeActuelle());
        }
        
        // For TRACTEUR specific fields
        if (ressource.getPuissance() != null) {
            current.setPuissance(ressource.getPuissance());
        }
        if (ressource.getCarburant() != null) {
            current.setCarburant(ressource.getCarburant());
        }
        if (ressource.getConsommationHoraire() != null) {
            current.setConsommationHoraire(ressource.getConsommationHoraire());
        }
        if (ressource.getKilometrage() != null) {
            current.setKilometrage(ressource.getKilometrage());
        }
        if (ressource.getaRemorque() != null) {
            current.setaRemorque(ressource.getaRemorque());
        }
        
        return ressourceRepository.save(current);
    }
    
    /**
     * Delete a resource
     */
    public void delete(String id) {
        if (!ressourceRepository.existsById(id)) {
            throw new IllegalArgumentException("Ressource non trouvée: " + id);
        }
        ressourceRepository.deleteById(id);
    }
    
    /**
     * Get resources by status
     */
    public List<Ressource> getByStatut(String statut) {
        return ressourceRepository.findByStatut(statut);
    }
}
