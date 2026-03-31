package com.example.demo.controller;

import com.example.demo.model.Utilisateur;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ===== AUTHENTICATION ENDPOINTS =====
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String motDePasse = request.get("motDePasse");
        Map<String, Object> response = authService.login(email, motDePasse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/responsable")
    public ResponseEntity<Map<String, Object>> loginResponsable(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String motDePasse = request.get("motDePasse");
        Map<String, Object> response = authService.loginResponsable(email, motDePasse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/admin")
    public ResponseEntity<Map<String, Object>> loginAdmin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String motDePasse = request.get("motDePasse");
        Map<String, Object> response = authService.loginAdmin(email, motDePasse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyToken() {
        Map<String, String> response = Map.of("message", "Token valide", "status", "success");
        return ResponseEntity.ok(response);
    }

    // ===== USER MANAGEMENT ENDPOINTS =====
    
    @PostMapping("/utilisateurs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSABLE')")
    public ResponseEntity<Utilisateur> creerUtilisateur(@RequestBody Utilisateur utilisateur) {
        Utilisateur nouvelUtilisateur = authService.creerUtilisateur(utilisateur);
        return ResponseEntity.ok(nouvelUtilisateur);
    }

    @GetMapping("/utilisateurs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSABLE')")
    public ResponseEntity<List<Utilisateur>> listerUtilisateurs() {
        List<Utilisateur> utilisateurs = authService.listerUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/utilisateurs/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSABLE')")
    public ResponseEntity<Utilisateur> trouverUtilisateurParId(@PathVariable String id) {
        Utilisateur utilisateur = authService.trouverUtilisateurParId(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        return ResponseEntity.ok(utilisateur);
    }

    @PutMapping("/utilisateurs/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSABLE')")
    public ResponseEntity<Utilisateur> mettreAJourUtilisateur(@PathVariable String id, @RequestBody Utilisateur utilisateur) {
        Utilisateur utilisateurMisAJour = authService.mettreAJourUtilisateur(id, utilisateur);
        return ResponseEntity.ok(utilisateurMisAJour);
    }

    @DeleteMapping("/utilisateurs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable String id) {
        authService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    // ===== ENDPOINTS ADMIN CORRIGÉS =====
    
    @GetMapping("/admin/agriculteurs/en-attente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Utilisateur>> getAgriculteursEnAttente() {
        return ResponseEntity.ok(authService.getAgriculteursEnAttente());
    }

    @GetMapping("/admin/travailleurs/en-attente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Utilisateur>> getTravailleursEnAttente() {
        return ResponseEntity.ok(authService.getTravailleursEnAttente());
    }

    @GetMapping("/admin/utilisateurs/en-attente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Utilisateur>> getTousUtilisateursEnAttente() {
        return ResponseEntity.ok(authService.getTousUtilisateursEnAttente());
    }

    @GetMapping("/admin/stats/attente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getStatsAttente() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("agriculteursEnAttente", authService.compterAgriculteursEnAttente());
        stats.put("travailleursEnAttente", authService.compterTravailleursEnAttente());
        return ResponseEntity.ok(stats);
    }

    // Correction : Utiliser @RequestBody au lieu de @RequestParam
    @PostMapping("/admin/activer-agriculteur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> activerAgriculteur(
            @PathVariable String id, 
            @RequestBody Map<String, String> request) {
        String motDePasse = request.get("nouveauMotDePasse");
        Utilisateur active = authService.activerAgriculteur(id, motDePasse);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Compte agriculteur activé avec succès");
        response.put("agriculteur", active);
        return ResponseEntity.ok(response);
    }

    // Correction : Utiliser @RequestBody au lieu de @RequestParam
    @PostMapping("/admin/activer-travailleur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> activerTravailleur(
            @PathVariable String id, 
            @RequestBody Map<String, String> request) {
        String motDePasse = request.get("nouveauMotDePasse");
        Utilisateur active = authService.activerTravailleur(id, motDePasse);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Compte travailleur activé avec succès");
        response.put("travailleur", active);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/activer-compte/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> activerCompte(
            @PathVariable String id, 
            @RequestBody Map<String, String> request) {
        String motDePasse = request.get("nouveauMotDePasse");
        Utilisateur active = authService.activerCompte(id, motDePasse);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Compte activé avec succès");
        response.put("utilisateur", active);
        return ResponseEntity.ok(response);
    }
}