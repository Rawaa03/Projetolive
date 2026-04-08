package com.example.demo.controller;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import com.example.demo.service.RessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ressources")
@CrossOrigin(origins = "http://localhost:4200")
public class RessourceController {
    
    @Autowired
    private RessourceService ressourceService;
    
    /**
     * Create a new resource (BENNE, TRACTEUR, or TRAVAILLEUR)
     * Each call creates a NEW resource with unique ID
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE')")
    public ResponseEntity<?> create(@RequestBody Ressource ressource) {
        try {
            // Ensure a new ID is generated
            ressource.setId(null);
            
            // Validate resource type
            if (ressource.getType() == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Type de ressource est obligatoire")
                );
            }
            
            // Create the resource - MongoDB will generate unique ID
            Ressource created = ressourceService.create(ressource);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    /**
     * Get all resources (with optional type filter)
     */
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String type) {
        try {
            List<Ressource> ressources;
            
            if (type != null && !type.isEmpty()) {
                try {
                    TypeRessource typeRessource = TypeRessource.valueOf(type.toUpperCase());
                    ressources = ressourceService.getByType(typeRessource);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "Type de ressource invalide: " + type)
                    );
                }
            } else {
                ressources = ressourceService.getAll();
            }
            
            return ResponseEntity.ok(ressources);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", "Erreur lors de la récupération des ressources: " + e.getMessage())
            );
        }
    }
    
    /**
     * Get a specific resource by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            Optional<Ressource> ressource = ressourceService.getById(id);
            
            if (ressource.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Ressource non trouvée: " + id)
                );
            }
            
            return ResponseEntity.ok(ressource.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    /**
     * Update a resource
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE')")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @RequestBody Ressource ressource) {
        try {
            Ressource updated = ressourceService.update(id, ressource);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    /**
     * Delete a resource
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE')")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            ressourceService.delete(id);
            return ResponseEntity.ok(
                Map.of("message", "Ressource supprimée avec succès")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    /**
     * Get resources by status (DISPONIBLE, EN_USE, MAINTENANCE, HORS_SERVICE)
     */
    @GetMapping("/status/{statut}")
    public ResponseEntity<?> getByStatut(@PathVariable String statut) {
        try {
            List<Ressource> ressources = ressourceService.getByStatut(statut);
            return ResponseEntity.ok(ressources);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    /**
     * Special endpoint for benne operations
     */
    @PostMapping("/{id}/charger")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE')")
    public ResponseEntity<?> chargerBenne(
            @PathVariable String id,
            @RequestBody Map<String, Double> payload) {
        try {
            Optional<Ressource> ressource = ressourceService.getById(id);
            
            if (ressource.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Benne non trouvée")
                );
            }
            
            Ressource benne = ressource.get();
            Double quantite = payload.get("quantite");
            
            if (quantite == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Quantité est obligatoire")
                );
            }
            
            benne.ajouterCharge(quantite);
            Ressource updated = ressourceService.update(id, benne);
            
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", e.getMessage())
            );
        }
    }
    
    /**
     * Empty a benne
     */
    @PostMapping("/{id}/vider")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESPONSABLE')")
    public ResponseEntity<?> viderBenne(@PathVariable String id) {
        try {
            Optional<Ressource> ressource = ressourceService.getById(id);
            
            if (ressource.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Benne non trouvée")
                );
            }
            
            Ressource benne = ressource.get();
            benne.vider();
            Ressource updated = ressourceService.update(id, benne);
            
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", e.getMessage())
            );
        }
    }
}
