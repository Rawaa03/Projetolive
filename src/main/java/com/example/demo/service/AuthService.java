package com.example.demo.service;

import com.example.demo.model.Utilisateur;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuthService {

    // ========== MÉTHODES D'AUTHENTIFICATION ==========
    
    void changerMotDePasseAdmin(String id, String nouveauMotDePasse);
    
    Map<String, Object> login(String email, String motDePasse);
    
    Map<String, Object> loginResponsable(String email, String motDePasse);
    
    Map<String, Object> loginAdmin(String email, String motDePasse);
    
    // ========== ADMIN : CRÉATION D'UTILISATEURS AVEC MOT DE PASSE GÉNÉRÉ ==========
    
    Map<String, Object> creerUtilisateurParAdmin(Utilisateur utilisateur);
    
    // ========== MÉTHODES DE GESTION DES UTILISATEURS ==========
    
    Utilisateur creerUtilisateur(Utilisateur utilisateur);
    
    Optional<Utilisateur> trouverUtilisateurParId(String id);
    
    List<Utilisateur> listerUtilisateurs();
    
    Utilisateur mettreAJourUtilisateur(String id, Utilisateur utilisateur);
    
    void supprimerUtilisateur(String id);
    
    Authentication authenticate(String email, String motDePasse);
    
    // ========== MÉTHODES POUR L'ADMIN (ACTIVATION) ==========
    
    List<Utilisateur> getAgriculteursEnAttente();
    
    List<Utilisateur> getTravailleursEnAttente();
    
    List<Utilisateur> getTousUtilisateursEnAttente();
    
    Utilisateur activerAgriculteur(String id, String nouveauMotDePasse);
    
    Utilisateur activerTravailleur(String id, String nouveauMotDePasse);
    
    // ========== GESTION DU PROFIL UTILISATEUR ==========
    
    Map<String, Object> getProfil(String id);
    
    Utilisateur mettreAJourProfil(String id, Map<String, Object> updates);
    
    void changerMotDePasse(String id, String ancienMotDePasse, String nouveauMotDePasse);
    
    // ========== ADMIN : DÉSACTIVER COMPTE ==========
    
    Utilisateur desactiverCompte(String id);
    
    Utilisateur activerCompte(String id, String nouveauMotDePasse);
    
    long compterAgriculteursEnAttente();
    
    long compterTravailleursEnAttente();
}