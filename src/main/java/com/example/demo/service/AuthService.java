package com.example.demo.service;

import com.example.demo.config.JwtUtils;
import com.example.demo.model.Role;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    // ========== MÉTHODES D'AUTHENTIFICATION ==========
    
    public Map<String, Object> login(String email, String motDePasse) {
        System.out.println("🔐 Tentative de connexion pour: " + email);
        
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            throw new RuntimeException("Mot de passe incorrect");
        }
        
        if (!utilisateur.getEstActif()) {
            throw new RuntimeException("Compte désactivé");
        }
        
        String token = jwtUtils.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole().toString(),
                utilisateur.getId()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", utilisateur.getId());
        response.put("email", utilisateur.getEmail());
        response.put("prenom", utilisateur.getPrenom());
        response.put("nom", utilisateur.getNom());
        response.put("role", utilisateur.getRole());
        response.put("token", token);
        response.put("compteActif", utilisateur.isCompteActif());
        
        return response;
    }

    public Map<String, Object> loginResponsable(String email, String motDePasse) {
        System.out.println("🔐 Tentative de connexion responsable pour: " + email);
        
        Utilisateur utilisateur = utilisateurRepository.findByEmailAndRole(email, Role.RESPONSABLE)
                .orElseThrow(() -> new RuntimeException("Accès réservé aux responsables"));
        
        if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            throw new RuntimeException("Mot de passe incorrect");
        }
        
        if (!utilisateur.getEstActif()) {
            throw new RuntimeException("Compte désactivé");
        }
        
        String token = jwtUtils.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole().toString(),
                utilisateur.getId()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", utilisateur.getId());
        response.put("email", utilisateur.getEmail());
        response.put("prenom", utilisateur.getPrenom());
        response.put("nom", utilisateur.getNom());
        response.put("role", utilisateur.getRole());
        response.put("token", token);
        
        return response;
    }

    public Map<String, Object> loginAdmin(String email, String motDePasse) {
        System.out.println("🔐 Tentative de connexion admin pour: " + email);
        
        Utilisateur utilisateur = utilisateurRepository.findByEmailAndRole(email, Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("Accès réservé à l'admin"));
        
        if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            throw new RuntimeException("Mot de passe incorrect");
        }
        
        if (!utilisateur.getEstActif()) {
            throw new RuntimeException("Compte désactivé");
        }
        
        String token = jwtUtils.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole().toString(),
                utilisateur.getId()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", utilisateur.getId());
        response.put("email", utilisateur.getEmail());
        response.put("prenom", utilisateur.getPrenom());
        response.put("nom", utilisateur.getNom());
        response.put("role", utilisateur.getRole());
        response.put("token", token);
        response.put("compteActif", utilisateur.isCompteActif());
        
        return response;
    }

    // ========== MÉTHODES DE GESTION DES UTILISATEURS ==========
    
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        System.out.println("📝 Création d'un nouvel utilisateur: " + utilisateur.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateur.setEstActif(true);
        utilisateur.setCompteActif(true);
        utilisateur.setDateCreation(new Date());
        return utilisateurRepository.save(utilisateur);
    }

    public Optional<Utilisateur> trouverUtilisateurParId(String id) {
        return utilisateurRepository.findById(id);
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur mettreAJourUtilisateur(String id, Utilisateur utilisateur) {
        Utilisateur existant = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        existant.setNom(utilisateur.getNom());
        existant.setPrenom(utilisateur.getPrenom());
        existant.setTelephone(utilisateur.getTelephone());
        existant.setRole(utilisateur.getRole());
        existant.setAdresse(utilisateur.getAdresse());
        
        return utilisateurRepository.save(existant);
    }

    public void supprimerUtilisateur(String id) {
        utilisateurRepository.deleteById(id);
    }

    public Authentication authenticate(String email, String motDePasse) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, motDePasse)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentification échouée: " + e.getMessage());
        }
    }

    // ========== MÉTHODES POUR L'ADMIN (ACTIVATION DES COMPTES) - CORRIGÉES ==========
    
    public List<Utilisateur> getAgriculteursEnAttente() {
        // Changé: findByRoleAndACompteIsFalse -> findByRoleAndCompteActifFalse
        return utilisateurRepository.findByRoleAndCompteActifFalse(Role.AGRICULTEUR);
    }

    public List<Utilisateur> getTravailleursEnAttente() {
        // Changé: findByRoleAndACompteIsFalse -> findByRoleAndCompteActifFalse
        return utilisateurRepository.findByRoleAndCompteActifFalse(Role.EQUIPE_RECOLTE);
    }

    public List<Utilisateur> getTousUtilisateursEnAttente() {
        // Changé: findByACompteFalse -> findByCompteActifFalse
        return utilisateurRepository.findByCompteActifFalse();
    }

 // Ajoutez dans AuthService.java
    @Autowired
    private EmailService emailService;

    public Utilisateur activerTravailleur(String id, String nouveauMotDePasse) {
        Utilisateur travailleur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Travailleur non trouvé"));
        
        if (travailleur.getRole() != Role.EQUIPE_RECOLTE) {
            throw new RuntimeException("Cet utilisateur n'est pas un travailleur");
        }
        
        if (travailleur.isCompteActif()) {
            throw new RuntimeException("Le compte de ce travailleur est déjà activé");
        }
        
        // Encoder et sauvegarder le mot de passe
        travailleur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        travailleur.setCompteActif(true);
        travailleur.setEstActif(true);
        
        Utilisateur sauvegarde = utilisateurRepository.save(travailleur);
        
        // Envoyer l'email avec le mot de passe
        try {
            emailService.envoyerMotDePasse(
                travailleur.getEmail(),
                travailleur.getNom(),
                travailleur.getPrenom(),
                nouveauMotDePasse
            );
            System.out.println("✅ Email envoyé à " + travailleur.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
        
        return sauvegarde;
    }

    public Utilisateur activerAgriculteur(String id, String nouveauMotDePasse) {
        Utilisateur agriculteur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agriculteur non trouvé"));
        
        if (agriculteur.getRole() != Role.AGRICULTEUR) {
            throw new RuntimeException("Cet utilisateur n'est pas un agriculteur");
        }
        
        if (agriculteur.isCompteActif()) {
            throw new RuntimeException("Le compte de cet agriculteur est déjà activé");
        }
        
        agriculteur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        agriculteur.setCompteActif(true);
        agriculteur.setEstActif(true);
        
        Utilisateur sauvegarde = utilisateurRepository.save(agriculteur);
        
        // Envoyer l'email avec le mot de passe
        try {
            emailService.envoyerMotDePasse(
                agriculteur.getEmail(),
                agriculteur.getNom(),
                agriculteur.getPrenom(),
                nouveauMotDePasse
            );
            System.out.println("✅ Email envoyé à " + agriculteur.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
        
        return sauvegarde;
    }


    public Utilisateur activerCompte(String id, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        if (utilisateur.isCompteActif()) {
            throw new RuntimeException("Le compte est déjà activé");
        }
        
        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateur.setCompteActif(true);
        utilisateur.setEstActif(true);
        
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> activerPlusieursAgriculteurs(List<String> ids, String motDePasseParDefaut) {
        List<Utilisateur> agriculteurs = utilisateurRepository.findAllById(ids);
        
        for (Utilisateur agriculteur : agriculteurs) {
            if (agriculteur.getRole() == Role.AGRICULTEUR && !agriculteur.isCompteActif()) {
                agriculteur.setMotDePasse(passwordEncoder.encode(motDePasseParDefaut));
                agriculteur.setCompteActif(true);
                agriculteur.setEstActif(true);
            }
        }
        
        return utilisateurRepository.saveAll(agriculteurs);
    }

    public List<Utilisateur> activerPlusieursTravailleurs(List<String> ids, String motDePasseParDefaut) {
        List<Utilisateur> travailleurs = utilisateurRepository.findAllById(ids);
        
        for (Utilisateur travailleur : travailleurs) {
            if (travailleur.getRole() == Role.EQUIPE_RECOLTE && !travailleur.isCompteActif()) {
                travailleur.setMotDePasse(passwordEncoder.encode(motDePasseParDefaut));
                travailleur.setCompteActif(true);
                travailleur.setEstActif(true);
            }
        }
        
        return utilisateurRepository.saveAll(travailleurs);
    }

    public long compterAgriculteursEnAttente() {
        // Changé: countByRoleAndAccountFalse -> countByRoleAndCompteActifFalse
        return utilisateurRepository.countByRoleAndCompteActifFalse(Role.AGRICULTEUR);
    }

    public long compterTravailleursEnAttente() {
        // Changé: countByRoleAndAccountFalse -> countByRoleAndCompteActifFalse
        return utilisateurRepository.countByRoleAndCompteActifFalse(Role.EQUIPE_RECOLTE);
    }
}