package com.example.demo.service;

import com.example.demo.model.Utilisateur;

import java.util.List;

public interface AgriculteurService {

    Utilisateur creerAgriculteur(Utilisateur agriculteur);
    
    Utilisateur mettreAJourAgriculteur(String id, Utilisateur agriculteur);
    
    Utilisateur trouverAgriculteurParId(String id);
    
    List<Utilisateur> listerAgriculteurs();
    
    void supprimerAgriculteur(String id);
}