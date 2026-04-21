package com.example.demo.service;

import com.example.demo.model.Utilisateur;

import java.util.List;

public interface TravailleurService {

    Utilisateur creerTravailleur(Utilisateur travailleur);
    
    List<Utilisateur> listerTravailleurs();
    
    List<Utilisateur> listerTravailleursParSpecialite(String specialite);
    
    Utilisateur trouverTravailleurParId(String id);
    
    Utilisateur mettreAJourTravailleur(String id, Utilisateur travailleur);
    
    void supprimerTravailleur(String id);
}