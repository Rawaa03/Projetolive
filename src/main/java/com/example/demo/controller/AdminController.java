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
@RequestMapping("/api/auth/admin")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AuthService authService;

    // ========== CRUD UTILISATEURS ==========

    @PostMapping("/utilisateurs")
    public ResponseEntity<Map<String, Object>> creerUtilisateurParAdmin(@RequestBody Utilisateur utilisateur) {
        Map<String, Object> response = authService.creerUtilisateurParAdmin(utilisateur);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> listerUtilisateurs() {
        List<Utilisateur> utilisateurs = authService.listerUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/utilisateurs/{id}")
    public ResponseEntity<Utilisateur> trouverUtilisateurParId(@PathVariable String id) {
        Utilisateur utilisateur = authService.trouverUtilisateurParId(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        return ResponseEntity.ok(utilisateur);
    }

    @PutMapping("/utilisateurs/{id}")
    public ResponseEntity<Utilisateur> mettreAJourUtilisateur(@PathVariable String id, @RequestBody Utilisateur utilisateur) {
        Utilisateur utilisateurMisAJour = authService.mettreAJourUtilisateur(id, utilisateur);
        return ResponseEntity.ok(utilisateurMisAJour);
    }

    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable String id) {
        authService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    // ========== GESTION DES COMPTES ==========

    @PostMapping("/desactiver-compte/{id}")
    public ResponseEntity<Map<String, Object>> desactiverCompte(@PathVariable String id) {
        Utilisateur desactive = authService.desactiverCompte(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Compte désactivé avec succès");
        response.put("utilisateur", desactive);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/activer-compte/{id}")
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

    // ========== ACTIVATION PAR RÔLE ==========
    @PostMapping("/changer-mot-de-passe/{id}")
    public ResponseEntity<Map<String, String>> changerMotDePasseAdmin(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String nouveauMotDePasse = request.get("nouveauMotDePasse");
        authService.changerMotDePasseAdmin(id, nouveauMotDePasse);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe changé avec succès");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/activer-agriculteur/{id}")
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

    @PostMapping("/activer-travailleur/{id}")
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

    // ========== LISTES DES UTILISATEURS EN ATTENTE ==========

    @GetMapping("/agriculteurs/en-attente")
    public ResponseEntity<List<Utilisateur>> getAgriculteursEnAttente() {
        return ResponseEntity.ok(authService.getAgriculteursEnAttente());
    }

    @GetMapping("/travailleurs/en-attente")
    public ResponseEntity<List<Utilisateur>> getTravailleursEnAttente() {
        return ResponseEntity.ok(authService.getTravailleursEnAttente());
    }

    @GetMapping("/utilisateurs/en-attente")
    public ResponseEntity<List<Utilisateur>> getTousUtilisateursEnAttente() {
        return ResponseEntity.ok(authService.getTousUtilisateursEnAttente());
    }

    // ========== STATISTIQUES ==========

    @GetMapping("/stats/attente")
    public ResponseEntity<Map<String, Long>> getStatsAttente() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("agriculteursEnAttente", authService.compterAgriculteursEnAttente());
        stats.put("travailleursEnAttente", authService.compterTravailleursEnAttente());
        return ResponseEntity.ok(stats);
    }
}