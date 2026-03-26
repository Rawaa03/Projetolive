package com.example.demo.service;

import com.example.demo.config.JwtUtils;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // ===== AUTHENTICATION METHODS =====
    
    // Standard login method (with detailed logging from your version)
    public Map<String, Object> login(String email, String motDePasse) {
        System.out.println("🔐 Tentative de connexion pour: " + email);
        
        // Vérifier si l'utilisateur existe
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElse(null);
        
        if (utilisateur == null) {
            System.out.println("❌ Utilisateur non trouvé: " + email);
            throw new RuntimeException("Utilisateur non trouvé");
        }
        
        System.out.println("✅ Utilisateur trouvé: " + utilisateur.getEmail());
        System.out.println("🔑 Hash stocké: " + utilisateur.getMotDePasse());
        
        // Vérifier le mot de passe
        boolean matches = passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse());
        System.out.println("🔐 Mot de passe valide? " + matches);
        
        if (!matches) {
            throw new RuntimeException("Mot de passe incorrect");
        }
        
        if (!utilisateur.getEstActif()) {
            throw new RuntimeException("Compte désactivé");
        }
        
        // Générer le token
        String token = jwtUtils.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole(),
                utilisateur.getId()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", utilisateur.getId());
        response.put("email", utilisateur.getEmail());
        response.put("prenom", utilisateur.getPrenom());
        response.put("nom", utilisateur.getNom());
        response.put("role", utilisateur.getRole());
        response.put("token", token);
        
        System.out.println("✅ Connexion réussie pour: " + email);
        
        return response;
    }

    // Login method for responsables (with your detailed logging approach)
    public Map<String, Object> loginResponsable(String email, String motDePasse) {
        System.out.println("🔐 Tentative de connexion responsable pour: " + email);
        
        Utilisateur utilisateur = utilisateurRepository.findByEmailAndRole(email, "responsable")
                .orElseThrow(() -> new RuntimeException("Accès réservé aux responsables"));
        
        System.out.println("✅ Responsable trouvé: " + utilisateur.getEmail());
        
        if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            System.out.println("❌ Mot de passe incorrect pour responsable: " + email);
            throw new RuntimeException("Mot de passe incorrect");
        }
        
        if (!utilisateur.getEstActif()) {
            throw new RuntimeException("Compte désactivé");
        }
        
        String token = jwtUtils.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole(),
                utilisateur.getId()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", utilisateur.getId());
        response.put("email", utilisateur.getEmail());
        response.put("prenom", utilisateur.getPrenom());
        response.put("nom", utilisateur.getNom());
        response.put("role", utilisateur.getRole());
        response.put("token", token);
        
        System.out.println("✅ Connexion responsable réussie pour: " + email);
        
        return response;
    }

    // Login method for admin (from teammate's version)
    public Map<String, Object> loginAdmin(String email, String motDePasse) {
        System.out.println("🔐 Tentative de connexion admin pour: " + email);
        
        Utilisateur utilisateur = utilisateurRepository.findByEmailAndRole(email, "admin")
                .orElseThrow(() -> new RuntimeException("Accès réservé à l'admin"));
        
        System.out.println("✅ Admin trouvé: " + utilisateur.getEmail());
        
        if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            System.out.println("❌ Mot de passe incorrect pour admin: " + email);
            throw new RuntimeException("Mot de passe incorrect");
        }
        
        if (!utilisateur.getEstActif()) {
            throw new RuntimeException("Compte désactivé");
        }
        
        String token = jwtUtils.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole(),
                utilisateur.getId()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", utilisateur.getId());
        response.put("email", utilisateur.getEmail());
        response.put("prenom", utilisateur.getPrenom());
        response.put("nom", utilisateur.getNom());
        response.put("role", utilisateur.getRole());
        response.put("token", token);
        
        System.out.println("✅ Connexion admin réussie pour: " + email);
        
        return response;
    }

    // ===== USER MANAGEMENT METHODS (from teammate's version) =====
    
    // Create a new user
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        System.out.println("📝 Création d'un nouvel utilisateur: " + utilisateur.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateur.setEstActif(true);
        utilisateur.setDateCreation(new Date());
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        System.out.println("✅ Utilisateur créé avec succès: " + savedUser.getEmail());
        return savedUser;
    }

    // Find user by ID
    public Optional<Utilisateur> trouverUtilisateurParId(String id) {
        System.out.println("🔍 Recherche d'utilisateur par ID: " + id);
        return utilisateurRepository.findById(id);
    }

    // List all users
    public List<Utilisateur> listerUtilisateurs() {
        System.out.println("📋 Récupération de tous les utilisateurs");
        return utilisateurRepository.findAll();
    }

    // Update a user
    public Utilisateur mettreAJourUtilisateur(String id, Utilisateur utilisateur) {
        System.out.println("✏️ Mise à jour de l'utilisateur avec ID: " + id);
        
        Utilisateur utilisateurExistant = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        utilisateurExistant.setNom(utilisateur.getNom());
        utilisateurExistant.setPrenom(utilisateur.getPrenom());
        utilisateurExistant.setTelephone(utilisateur.getTelephone());
        utilisateurExistant.setRole(utilisateur.getRole());
        utilisateurExistant.setAdresse(utilisateur.getAdresse());
        
        Utilisateur updatedUser = utilisateurRepository.save(utilisateurExistant);
        System.out.println("✅ Utilisateur mis à jour avec succès: " + updatedUser.getEmail());
        
        return updatedUser;
    }

    // Delete a user
    public void supprimerUtilisateur(String id) {
        System.out.println("🗑️ Suppression de l'utilisateur avec ID: " + id);
        utilisateurRepository.deleteById(id);
        System.out.println("✅ Utilisateur supprimé avec succès");
    }
    
    // Optional: Additional helper method using AuthenticationManager (from your version)
    public Authentication authenticate(String email, String motDePasse) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, motDePasse)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (AuthenticationException e) {
            System.out.println("❌ Échec d'authentification pour: " + email);
            throw new RuntimeException("Authentification échouée: " + e.getMessage());
        }
    }
}