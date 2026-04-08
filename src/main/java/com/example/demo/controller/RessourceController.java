package com.example.demo.controller;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import com.example.demo.service.RessourceService;
import com.example.demo.service.BenneService;
import com.example.demo.service.TracteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ressources")
@PreAuthorize("hasRole('ADMIN') or hasRole('RESPONSABLE')")
@CrossOrigin(origins = "http://localhost:4200")
public class RessourceController {

    @Autowired
    private RessourceService ressourceService;

    @Autowired
    private BenneService benneService;

    @Autowired
    private TracteurService tracteurService;

    // ==================== BENNE ENDPOINTS ====================

    @PostMapping("/bennes")
    public ResponseEntity<?> createBenne(@RequestBody Ressource benne) {
        try {
            System.out.println("📦 Création benne - Données reçues:");
            System.out.println("   - nom: " + benne.getNom());
            System.out.println("   - immatriculation: " + benne.getImmatriculation());
            System.out.println("   - capaciteKg: " + benne.getCapaciteKg());

            // Validation
            if (benne.getNom() == null || benne.getNom().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le nom de la benne est requis"));
            }

            if (benne.getImmatriculation() == null || benne.getImmatriculation().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "L'immatriculation de la benne est requise"));
            }

            if (benne.getCapaciteKg() == null || benne.getCapaciteKg() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "La capacité de la benne doit être positive"));
            }

            Ressource created = benneService.creerBenne(benne);

            System.out.println("✅ Benne créée avec succès:");
            System.out.println("   - ID: " + created.getId());
            System.out.println("   - nom: " + created.getNom());
            System.out.println("   - immatriculation: " + created.getImmatriculation());

            // Retourner directement l'objet
            return ResponseEntity.ok(created);

        } catch (RuntimeException e) {
            System.err.println("❌ Erreur création benne: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bennes")
    public ResponseEntity<List<Ressource>> getAllBennes() {
        List<Ressource> bennes = benneService.listerBennes();
        System.out.println("📦 Nombre de bennes retournées: " + bennes.size());
        return ResponseEntity.ok(bennes);
    }

    @GetMapping("/bennes/{id}")
    public ResponseEntity<?> getBenne(@PathVariable String id) {
        try {
            Ressource benne = benneService.getBenneById(id);
            System.out.println("📦 Benne trouvée: " + benne.getNom() + " - " + benne.getImmatriculation());
            return ResponseEntity.ok(benne);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/bennes/{id}")
    public ResponseEntity<?> updateBenne(@PathVariable String id, @RequestBody Ressource benne) {
        try {
            Ressource updated = benneService.mettreAJourBenne(id, benne);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/bennes/{id}")
    public ResponseEntity<?> deleteBenne(@PathVariable String id) {
        try {
            benneService.supprimerBenne(id);
            return ResponseEntity.ok(Map.of("message", "Benne supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/bennes/{id}/charger")
    public ResponseEntity<?> addLoad(@PathVariable String id, @RequestParam Double quantite) {
        try {
            Ressource updated = benneService.ajouterCharge(id, quantite);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bennes/{id}/vider")
    public ResponseEntity<?> emptyBenne(@PathVariable String id) {
        try {
            Ressource updated = benneService.viderBenne(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bennes/{id}/maintenance")
    public ResponseEntity<?> startBenneMaintenance(@PathVariable String id) {
        try {
            Ressource updated = benneService.enregistrerMaintenance(id, "Maintenance programmée", null);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bennes/{id}/maintenance/end")
    public ResponseEntity<?> endBenneMaintenance(@PathVariable String id) {
        try {
            Ressource updated = benneService.terminerMaintenance(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== TRACTEUR ENDPOINTS ====================

    @PostMapping("/tracteurs")
    public ResponseEntity<?> createTracteur(@RequestBody Ressource tracteur) {
        try {
            System.out.println("🚜 Création tracteur - Données reçues:");
            System.out.println("   - nom: " + tracteur.getNom());
            System.out.println("   - immatriculation: " + tracteur.getImmatriculation());
            System.out.println("   - puissance: " + tracteur.getPuissance());
            System.out.println("   - carburant: " + tracteur.getCarburant());

            // Validation
            if (tracteur.getNom() == null || tracteur.getNom().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le nom du tracteur est requis"));
            }

            if (tracteur.getImmatriculation() == null || tracteur.getImmatriculation().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "L'immatriculation du tracteur est requise"));
            }

            if (tracteur.getPuissance() == null || tracteur.getPuissance().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La puissance du tracteur est requise"));
            }

            if (tracteur.getCarburant() == null || tracteur.getCarburant().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le carburant du tracteur est requis"));
            }

            Ressource created = tracteurService.creerTracteur(tracteur);

            System.out.println("✅ Tracteur créé avec succès:");
            System.out.println("   - ID: " + created.getId());
            System.out.println("   - nom: " + created.getNom());
            System.out.println("   - immatriculation: " + created.getImmatriculation());

            return ResponseEntity.ok(created);

        } catch (RuntimeException e) {
            System.err.println("❌ Erreur création tracteur: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/tracteurs")
    public ResponseEntity<List<Ressource>> getAllTracteurs() {
        List<Ressource> tracteurs = tracteurService.listerTracteurs();
        System.out.println("🚜 Nombre de tracteurs retournés: " + tracteurs.size());
        return ResponseEntity.ok(tracteurs);
    }

    @GetMapping("/tracteurs/{id}")
    public ResponseEntity<?> getTracteur(@PathVariable String id) {
        try {
            Ressource tracteur = tracteurService.getTracteurById(id);
            System.out.println("🚜 Tracteur trouvé: " + tracteur.getNom() + " - " + tracteur.getImmatriculation());
            return ResponseEntity.ok(tracteur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/tracteurs/{id}")
    public ResponseEntity<?> updateTracteur(@PathVariable String id, @RequestBody Ressource tracteur) {
        try {
            Ressource updated = tracteurService.mettreAJourTracteur(id, tracteur);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/tracteurs/{id}")
    public ResponseEntity<?> deleteTracteur(@PathVariable String id) {
        try {
            tracteurService.supprimerTracteur(id);
            return ResponseEntity.ok(Map.of("message", "Tracteur supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/tracteurs/{id}/update-mileage")
    public ResponseEntity<?> updateMileage(@PathVariable String id, @RequestParam Double mileage) {
        try {
            Ressource updated = tracteurService.mettreAJourKilometrage(id, mileage);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tracteurs/{id}/maintenance")
    public ResponseEntity<?> startTracteurMaintenance(@PathVariable String id) {
        try {
            Ressource updated = tracteurService.enregistrerMaintenance(id, "Maintenance programmée", null);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tracteurs/{id}/maintenance/end")
    public ResponseEntity<?> endTracteurMaintenance(@PathVariable String id) {
        try {
            Ressource updated = tracteurService.terminerMaintenance(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}