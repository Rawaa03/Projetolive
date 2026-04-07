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

    // ==================== BASE CRUD ENDPOINTS ====================

    /**
     * Créer une nouvelle ressource
     */
    @PostMapping
    public ResponseEntity<?> createRessource(@RequestBody Ressource ressource) {
        try {
            Ressource created = ressourceService.creerRessource(ressource);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Ressource créée avec succès");
            response.put("ressource", created);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Récupérer une ressource par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRessource(@PathVariable String id) {
        try {
            Ressource ressource = ressourceService.getRessourceById(id);
            return ResponseEntity.ok(ressource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lister toutes les ressources
     */
    @GetMapping
    public ResponseEntity<List<Ressource>> getAllRessources() {
        List<Ressource> ressources = ressourceService.listerToutesLesRessources();
        return ResponseEntity.ok(ressources);
    }

    /**
     * Mettre à jour une ressource
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRessource(@PathVariable String id, @RequestBody Ressource ressource) {
        try {
            Ressource updated = ressourceService.mettreAJourRessource(id, ressource);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Ressource modifiée avec succès");
            response.put("ressource", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer une ressource
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRessource(@PathVariable String id) {
        try {
            ressourceService.supprimerRessource(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Ressource supprimée avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== SEARCH & FILTER ENDPOINTS ====================

    /**
     * Rechercher les ressources par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Ressource>> getRessourcesByType(@PathVariable TypeRessource type) {
        List<Ressource> ressources = ressourceService.rechercherParType(type);
        return ResponseEntity.ok(ressources);
    }

    /**
     * Rechercher les ressources par statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Ressource>> getRessourcesByStatus(@PathVariable String statut) {
        List<Ressource> ressources = ressourceService.rechercherParStatut(statut);
        return ResponseEntity.ok(ressources);
    }

    /**
     * Rechercher les ressources par type et statut
     */
    @GetMapping("/type/{type}/statut/{statut}")
    public ResponseEntity<List<Ressource>> getRessourcesByTypeAndStatus(
            @PathVariable TypeRessource type,
            @PathVariable String statut) {
        List<Ressource> ressources = ressourceService.rechercherParTypeEtStatut(type, statut);
        return ResponseEntity.ok(ressources);
    }

    /**
     * Lister les ressources disponibles
     */
    @GetMapping("/available")
    public ResponseEntity<List<Ressource>> getAvailableRessources() {
        List<Ressource> ressources = ressourceService.listerRessourcesDisponibles();
        return ResponseEntity.ok(ressources);
    }

    /**
     * Lister les ressources en maintenance
     */
    @GetMapping("/maintenance")
    public ResponseEntity<List<Ressource>> getRessourcesUnderMaintenance() {
        List<Ressource> ressources = ressourceService.listerRessourcesEnMaintenance();
        return ResponseEntity.ok(ressources);
    }

    // ==================== AVAILABILITY ENDPOINTS ====================

    /**
     * Vérifier la disponibilité d'une ressource pour une période
     */
    @GetMapping("/{id}/available")
    public ResponseEntity<?> checkAvailability(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDate) {
        try {
            boolean available = ressourceService.estDisponiblePour(id, startDate, endDate);
            Map<String, Object> response = new HashMap<>();
            response.put("ressourceId", id);
            response.put("disponible", available);
            response.put("dateDebut", startDate);
            response.put("dateFin", endDate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lister les ressources disponibles pour une période
     */
    @GetMapping("/available/period")
    public ResponseEntity<List<Ressource>> getAvailableForPeriod(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDate) {
        List<Ressource> ressources = ressourceService.listerRessourcesDisponiblesPour(startDate, endDate);
        return ResponseEntity.ok(ressources);
    }

    /**
     * Lister les ressources disponibles d'un type pour une période
     */
    @GetMapping("/available/{type}/period")
    public ResponseEntity<List<Ressource>> getAvailableByTypeForPeriod(
            @PathVariable TypeRessource type,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDate) {
        List<Ressource> ressources = ressourceService.listerRessourcesDisponiblesParTypePour(type, startDate, endDate);
        return ResponseEntity.ok(ressources);
    }

    // ==================== TOUR ASSIGNMENT ENDPOINTS ====================

    /**
     * Assigner une ressource à une tournée
     */
    @PostMapping("/{ressourceId}/assign-tour/{tourneeId}")
    public ResponseEntity<?> assignToTour(
            @PathVariable String ressourceId,
            @PathVariable String tourneeId) {
        try {
            Ressource updated = ressourceService.assignerAuTour(ressourceId, tourneeId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Ressource assignée à la tournée avec succès");
            response.put("ressource", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Retirer une ressource d'une tournée
     */
    @DeleteMapping("/{ressourceId}/unassign-tour/{tourneeId}")
    public ResponseEntity<?> unassignFromTour(
            @PathVariable String ressourceId,
            @PathVariable String tourneeId) {
        try {
            Ressource updated = ressourceService.retirerDuTour(ressourceId, tourneeId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Ressource retirée de la tournée avec succès");
            response.put("ressource", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Obtenir le statut d'une ressource
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getStatus(@PathVariable String id) {
        try {
            Map<String, Object> status = ressourceService.obtenirStatutRessource(id);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== BENNE ENDPOINTS ====================

    /**
     * Créer une benne
     */
    @PostMapping("/bennes")
    public ResponseEntity<?> createBenne(@RequestBody Ressource benne) {
        try {
            Ressource created = benneService.creerBenne(benne);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Benne créée avec succès");
            response.put("benne", created);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Lister toutes les bennes
     */
    @GetMapping("/bennes")
    public ResponseEntity<List<Ressource>> getAllBennes() {
        List<Ressource> bennes = benneService.listerBennes();
        return ResponseEntity.ok(bennes);
    }

    /**
     * Lister les bennes disponibles
     */
    @GetMapping("/bennes/available")
    public ResponseEntity<List<Ressource>> getAvailableBennes() {
        List<Ressource> bennes = benneService.listerBennesDisponibles();
        return ResponseEntity.ok(bennes);
    }

    /**
     * Récupérer une benne par ID
     */
    @GetMapping("/bennes/{id}")
    public ResponseEntity<?> getBenne(@PathVariable String id) {
        try {
            Ressource benne = benneService.getBenneById(id);
            return ResponseEntity.ok(benne);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mettre à jour une benne
     */
    @PutMapping("/bennes/{id}")
    public ResponseEntity<?> updateBenne(@PathVariable String id, @RequestBody Ressource benne) {
        try {
            Ressource updated = benneService.mettreAJourBenne(id, benne);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Benne modifiée avec succès");
            response.put("benne", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer une benne
     */
    @DeleteMapping("/bennes/{id}")
    public ResponseEntity<?> deleteBenne(@PathVariable String id) {
        try {
            benneService.supprimerBenne(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Benne supprimée avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== BENNE LOAD MANAGEMENT ====================

    /**
     * Ajouter une charge à une benne
     */
    @PostMapping("/bennes/{id}/charger")
    public ResponseEntity<?> addLoad(@PathVariable String id, @RequestParam Double quantite) {
        try {
            Ressource updated = benneService.ajouterCharge(id, quantite);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Charge ajoutée avec succès");
            response.put("benne", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Vider une benne
     */
    @PostMapping("/bennes/{id}/vider")
    public ResponseEntity<?> emptyBenne(@PathVariable String id) {
        try {
            Ressource updated = benneService.viderBenne(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Benne vidée avec succès");
            response.put("benne", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Obtenir les informations de capacité
     */
    @GetMapping("/bennes/{id}/capacite")
    public ResponseEntity<?> getCapacityInfo(@PathVariable String id) {
        try {
            Map<String, Object> capacite = benneService.obtenirInfoCapacite(id);
            return ResponseEntity.ok(capacite);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lister les bennes pleines
     */
    @GetMapping("/bennes/full")
    public ResponseEntity<List<Ressource>> getFullBennes() {
        List<Ressource> bennes = benneService.listerBennesPleines();
        return ResponseEntity.ok(bennes);
    }

    /**
     * Obtenir les statistiques d'une benne
     */
    @GetMapping("/bennes/{id}/stats")
    public ResponseEntity<?> getBenneStats(@PathVariable String id) {
        try {
            Map<String, Object> stats = benneService.obtenirStatistiques(id);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== BENNE-TRACTOR ASSIGNMENT ====================

    /**
     * Assigner un tracteur à une benne
     */
    @PostMapping("/bennes/{benneId}/assign-tracteur/{tracteurId}")
    public ResponseEntity<?> assignTractorToBenne(
            @PathVariable String benneId,
            @PathVariable String tracteurId) {
        try {
            Ressource updated = benneService.assignerTracteur(benneId, tracteurId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tracteur assigné à la benne avec succès");
            response.put("benne", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Retirer le tracteur d'une benne
     */
    @DeleteMapping("/bennes/{benneId}/unassign-tracteur")
    public ResponseEntity<?> unassignTractorFromBenne(@PathVariable String benneId) {
        try {
            Ressource updated = benneService.retirerTracteur(benneId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tracteur retiré de la benne avec succès");
            response.put("benne", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    // ==================== BENNE MAINTENANCE ====================

    /**
     * Enregistrer une maintenance pour une benne
     */
    @PostMapping("/bennes/{id}/maintenance")
    public ResponseEntity<?> recordBenneMaintenance(
            @PathVariable String id,
            @RequestParam String description,
            @RequestParam(required = false) Double cout) {
        try {
            Ressource updated = benneService.enregistrerMaintenance(id, description, cout);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Maintenance enregistrée avec succès");
            response.put("benne", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Terminer la maintenance d'une benne
     */
    @PostMapping("/bennes/{id}/maintenance/end")
    public ResponseEntity<?> endBenneMaintenance(@PathVariable String id) {
        try {
            Ressource updated = benneService.terminerMaintenance(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Maintenance terminée avec succès");
            response.put("benne", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Lister les bennes en maintenance
     */
    @GetMapping("/bennes/maintenance-list")
    public ResponseEntity<List<Ressource>> getBennesUnderMaintenance() {
        List<Ressource> bennes = benneService.listerBennesEnMaintenance();
        return ResponseEntity.ok(bennes);
    }

    // ==================== TRACTOR ENDPOINTS ====================

    /**
     * Créer un tracteur
     */
    @PostMapping("/tracteurs")
    public ResponseEntity<?> createTracteur(@RequestBody Ressource tracteur) {
        try {
            Ressource created = tracteurService.creerTracteur(tracteur);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tracteur créé avec succès");
            response.put("tracteur", created);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Lister tous les tracteurs
     */
    @GetMapping("/tracteurs")
    public ResponseEntity<List<Ressource>> getAllTracteurs() {
        List<Ressource> tracteurs = tracteurService.listerTracteurs();
        return ResponseEntity.ok(tracteurs);
    }

    /**
     * Lister les tracteurs disponibles
     */
    @GetMapping("/tracteurs/available")
    public ResponseEntity<List<Ressource>> getAvailableTracteurs() {
        List<Ressource> tracteurs = tracteurService.listerTracteurDisponibles();
        return ResponseEntity.ok(tracteurs);
    }

    /**
     * Récupérer un tracteur par ID
     */
    @GetMapping("/tracteurs/{id}")
    public ResponseEntity<?> getTracteur(@PathVariable String id) {
        try {
            Ressource tracteur = tracteurService.getTracteurById(id);
            return ResponseEntity.ok(tracteur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mettre à jour un tracteur
     */
    @PutMapping("/tracteurs/{id}")
    public ResponseEntity<?> updateTracteur(@PathVariable String id, @RequestBody Ressource tracteur) {
        try {
            Ressource updated = tracteurService.mettreAJourTracteur(id, tracteur);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tracteur modifié avec succès");
            response.put("tracteur", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un tracteur
     */
    @DeleteMapping("/tracteurs/{id}")
    public ResponseEntity<?> deleteTracteur(@PathVariable String id) {
        try {
            tracteurService.supprimerTracteur(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tracteur supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== TRACTOR SPECIFICATIONS ====================

    /**
     * Obtenir les spécifications d'un tracteur
     */
    @GetMapping("/tracteurs/{id}/specs")
    public ResponseEntity<?> getTracteurSpecs(@PathVariable String id) {
        try {
            Map<String, Object> specs = tracteurService.obtenirSpecs(id);
            return ResponseEntity.ok(specs);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtenir les statistiques d'un tracteur
     */
    @GetMapping("/tracteurs/{id}/stats")
    public ResponseEntity<?> getTracteurStats(@PathVariable String id) {
        try {
            Map<String, Object> stats = tracteurService.obtenirStatistiques(id);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== TRACTOR MILEAGE & FUEL ====================

    /**
     * Mettre à jour le kilométrage
     */
    @PutMapping("/tracteurs/{id}/update-mileage")
    public ResponseEntity<?> updateMileage(@PathVariable String id, @RequestParam Double mileage) {
        try {
            Ressource updated = tracteurService.mettreAJourKilometrage(id, mileage);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Kilométrage mis à jour avec succès");
            response.put("tracteur", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Calculer la consommation estimée
     */
    @PostMapping("/tracteurs/{id}/consumption-estimate")
    public ResponseEntity<?> estimateConsumption(@PathVariable String id, @RequestParam Double distance) {
        try {
            Map<String, Double> result = tracteurService.calculerConsommationEstimee(id, distance);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    // ==================== TRACTOR DRIVER ASSIGNMENT ====================

    /**
     * Assigner un conducteur
     */
    @PostMapping("/tracteurs/{id}/assign-driver/{driverId}")
    public ResponseEntity<?> assignDriver(@PathVariable String id, @PathVariable String driverId) {
        try {
            Ressource updated = tracteurService.assignerConducteur(id, driverId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Conducteur assigné avec succès");
            response.put("tracteur", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Retirer le conducteur
     */
    @DeleteMapping("/tracteurs/{id}/unassign-driver")
    public ResponseEntity<?> unassignDriver(@PathVariable String id) {
        try {
            Ressource updated = tracteurService.retirerConducteur(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Conducteur retiré avec succès");
            response.put("tracteur", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    // ==================== TRACTOR MAINTENANCE ====================

    /**
     * Enregistrer une maintenance pour un tracteur
     */
    @PostMapping("/tracteurs/{id}/maintenance")
    public ResponseEntity<?> recordTracteurMaintenance(
            @PathVariable String id,
            @RequestParam String description,
            @RequestParam(required = false) Double cout) {
        try {
            Ressource updated = tracteurService.enregistrerMaintenance(id, description, cout);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Maintenance enregistrée avec succès");
            response.put("tracteur", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Terminer la maintenance d'un tracteur
     */
    @PostMapping("/tracteurs/{id}/maintenance/end")
    public ResponseEntity<?> endTracteurMaintenance(@PathVariable String id) {
        try {
            Ressource updated = tracteurService.terminerMaintenance(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Maintenance terminée avec succès");
            response.put("tracteur", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", e.getMessage());
            }});
        }
    }

    /**
     * Lister les tracteurs en maintenance
     */
    @GetMapping("/tracteurs/maintenance-list")
    public ResponseEntity<List<Ressource>> getTracteursUnderMaintenance() {
        List<Ressource> tracteurs = tracteurService.listerTacteursEnMaintenance();
        return ResponseEntity.ok(tracteurs);
    }

    /**
     * Lister les tracteurs avec remorque
     */
    @GetMapping("/tracteurs/with-trailer")
    public ResponseEntity<List<Ressource>> getTracteurWithTrailer() {
        List<Ressource> tracteurs = tracteurService.listerTracteurAvecRemorque();
        return ResponseEntity.ok(tracteurs);
    }
}
