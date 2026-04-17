package com.example.demo.controller;

import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class UserProfileController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfil(Authentication authentication) {
        String email = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return ResponseEntity.ok(authService.getProfil(utilisateur.getId()));
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> mettreAJourProfil(
            Authentication authentication,
            @RequestBody Map<String, Object> updates) {
        String email = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Utilisateur misAJour = authService.mettreAJourProfil(utilisateur.getId(), updates);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profil mis à jour avec succès");
        response.put("utilisateur", misAJour);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changer-mot-de-passe")
    public ResponseEntity<Map<String, String>> changerMotDePasse(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        String email = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        authService.changerMotDePasse(
                utilisateur.getId(),
                request.get("ancienMotDePasse"),
                request.get("nouveauMotDePasse")
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe changé avec succès");
        return ResponseEntity.ok(response);
    }
}