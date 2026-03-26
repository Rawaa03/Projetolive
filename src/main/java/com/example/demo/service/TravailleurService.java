package com.example.demo.service;

import com.example.demo.model.Travailleur;
import com.example.demo.repository.TravailleurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class TravailleurService {
    
    @Autowired
    private TravailleurRepository travailleurRepository;
    
    public List<Travailleur> getAllTravailleurs() {
        return travailleurRepository.findAll();
    }
    
    public Travailleur createTravailleur(Travailleur travailleur) {
        travailleur.setStatut("ACTIF");
        travailleur.setDateEmbauche(new Date());
        return travailleurRepository.save(travailleur);
    }
    
    public Travailleur getTravailleurById(String id) {
        return travailleurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Travailleur non trouvé"));
    }
    
    public Travailleur updateTravailleur(String id, Travailleur travailleur) {
        Travailleur existant = getTravailleurById(id);
        existant.setNom(travailleur.getNom());
        existant.setPrenom(travailleur.getPrenom());
        existant.setTelephone(travailleur.getTelephone());
        existant.setAdresse(travailleur.getAdresse());
        existant.setEmail(travailleur.getEmail());
        existant.setSpecialite(travailleur.getSpecialite());
        existant.setSalaireJournalier(travailleur.getSalaireJournalier());
        return travailleurRepository.save(existant);
    }
    
    public void deleteTravailleur(String id) {
        travailleurRepository.deleteById(id);
    }
}