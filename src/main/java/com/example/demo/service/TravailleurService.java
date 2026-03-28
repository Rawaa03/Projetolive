package com.example.demo.service;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import com.example.demo.repository.TravailleurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class TravailleurService {
    
    @Autowired
    private TravailleurRepository ressourceRepository;
    
    public List<Ressource> getAllTravailleurs() {
        return ressourceRepository.findByType(TypeRessource.TRAVAILLEUR);
    }
    
    public Ressource createTravailleur(Ressource travailleur) {
        travailleur.setType(TypeRessource.TRAVAILLEUR);
        travailleur.setStatut("DISPONIBLE");
        travailleur.setDateEmbauche(new Date());
        return ressourceRepository.save(travailleur);
    }
    
    public Ressource getTravailleurById(String id) {
        Ressource travailleur = ressourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Travailleur non trouvé"));
        
        if (travailleur.getType() != TypeRessource.TRAVAILLEUR) {
            throw new RuntimeException("La ressource avec l'ID " + id + " n'est pas un travailleur");
        }
        
        return travailleur;
    }
    
    public Ressource updateTravailleur(String id, Ressource travailleur) {
        Ressource existant = getTravailleurById(id);
        
        existant.setNom(travailleur.getNom());
        existant.setPrenom(travailleur.getPrenom());
        existant.setTelephone(travailleur.getTelephone());
        existant.setSpecialite(travailleur.getSpecialite());
        existant.setSalaireJournalier(travailleur.getSalaireJournalier());
        
        return ressourceRepository.save(existant);
    }
    
    public void deleteTravailleur(String id) {
        Ressource travailleur = getTravailleurById(id);
        
        if (travailleur.getTourneeActuelleId() != null) {
            throw new RuntimeException("Impossible de supprimer un travailleur actuellement en tournée");
        }
        
        ressourceRepository.deleteById(id);
    }
    
    public List<Ressource> getTravailleursDisponibles() {
        return ressourceRepository.findByTypeAndStatutAndTourneeActuelleIdIsNull(
            TypeRessource.TRAVAILLEUR, "DISPONIBLE");
    }
    
    public List<Ressource> getTravailleursBySpecialite(String specialite) {
        return ressourceRepository.findByTypeAndSpecialite(TypeRessource.TRAVAILLEUR, specialite);
    }
}