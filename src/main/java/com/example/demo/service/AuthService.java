package com.example.demo.service;

import com.example.demo.config.JwtUtils;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private JwtUtils jwtUtils;
    UtilisateurRepository userRep;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public Map<String, Object> loginResponsable(String email, String motDePasse) {
        Utilisateur utilisateur = userRep.findByEmailAndRole(email, "responsable")
                .orElseThrow(() -> new RuntimeException("Accès réservé aux responsables"));
        
        if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
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
        
        return response;
    }
    
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
}}