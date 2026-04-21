package com.example.demo.service;

import com.example.demo.model.Ressource;

import java.util.List;

public interface TracteurService {

    Ressource creerTracteur(Ressource tracteur);
    
    Ressource getTracteurById(String id);
    
    List<Ressource> listerTracteurs();
    
    List<Ressource> listerTracteurDisponibles();
    
    Ressource mettreAJourTracteur(String id, Ressource update);
    
    void supprimerTracteur(String id);
    
    Ressource mettreAJourKilometrage(String id, Double km);
    
    Ressource enregistrerMaintenance(String id, String description, Double cout);
    
    Ressource terminerMaintenance(String id);
    
    Ressource assignerConducteur(String tracteurId, String conducteurId);
    
    Ressource retirerConducteur(String tracteurId);
    
    List<Ressource> listerTracteursDuConducteur(String conducteurId);
    
    List<Ressource> listerAvecRemorque();
}