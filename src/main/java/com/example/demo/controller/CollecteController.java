package com.example.demo.controller;

import com.example.demo.dto.CollecteRequest;
import com.example.demo.dto.CollecteResponse;
import com.example.demo.model.enums.StatutCollecte;
import com.example.demo.service.CollecteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collectes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CollecteController {

    private final CollecteService collecteService;

    // ─── CREATE ────────────────────────────────────────────────────────────

    /**
     * POST /api/collectes
     * Create a new harvest campaign (PLANIFIEE).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<CollecteResponse> creer(@Valid @RequestBody CollecteRequest request) {
        return ResponseEntity.ok(collecteService.creer(request));
    }

    // ─── READ ──────────────────────────────────────────────────────────────

    /**
     * GET /api/collectes
     * List all collectes.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'AGRICULTEUR')")
    public ResponseEntity<List<CollecteResponse>> getAll() {
        return ResponseEntity.ok(collecteService.getAll());
    }

    /**
     * GET /api/collectes/active
     * List collectes with statut PLANIFIEE or EN_COURS.
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'EQUIPE_RECOLTE')")
    public ResponseEntity<List<CollecteResponse>> getActive() {
        return ResponseEntity.ok(collecteService.getActive());
    }

    /**
     * GET /api/collectes/{id}
     * Get a single collecte.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'AGRICULTEUR')")
    public ResponseEntity<CollecteResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(collecteService.getById(id));
    }

    /**
     * GET /api/collectes/verger/{vergerId}
     * All collectes for a specific verger.
     */
    @GetMapping("/verger/{vergerId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'AGRICULTEUR')")
    public ResponseEntity<List<CollecteResponse>> getByVerger(@PathVariable String vergerId) {
        return ResponseEntity.ok(collecteService.getByVerger(vergerId));
    }

    /**
     * GET /api/collectes/statut?statut=PLANIFIEE
     * Filter collectes by statut.
     */
    @GetMapping("/statut")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<List<CollecteResponse>> getByStatut(@RequestParam StatutCollecte statut) {
        return ResponseEntity.ok(collecteService.getByStatut(statut));
    }

    // ─── STATE TRANSITIONS ─────────────────────────────────────────────────

    /**
     * PATCH /api/collectes/{id}/demarrer
     * Start a harvest campaign (PLANIFIEE → EN_COURS).
     */
    @PatchMapping("/{id}/demarrer")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<CollecteResponse> demarrer(@PathVariable String id) {
        return ResponseEntity.ok(collecteService.demarrer(id));
    }

    /**
     * PATCH /api/collectes/{id}/terminer
     * Finish a harvest campaign (EN_COURS → TERMINEE).
     * Only possible if ALL tournées in this collecte are TERMINEE.
     */
    @PatchMapping("/{id}/terminer")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<CollecteResponse> terminer(@PathVariable String id) {
        return ResponseEntity.ok(collecteService.terminer(id));
    }

    /**
     * PATCH /api/collectes/{id}/annuler
     * Cancel a harvest campaign.
     */
    @PatchMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<CollecteResponse> annuler(@PathVariable String id) {
        return ResponseEntity.ok(collecteService.annuler(id));
    }

    // ─── UPDATE / DELETE ───────────────────────────────────────────────────

    /**
     * PUT /api/collectes/{id}
     * Update a PLANIFIEE collecte.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<CollecteResponse> mettreAJour(
            @PathVariable String id,
            @Valid @RequestBody CollecteRequest request) {
        return ResponseEntity.ok(collecteService.mettreAJour(id, request));
    }

    /**
     * DELETE /api/collectes/{id}
     * Delete a collecte (only if no tournées or all tournées are ANNULEE).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable String id) {
        collecteService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    // ─── STATISTICS ────────────────────────────────────────────────────────

    /**
     * GET /api/collectes/{id}/quantite-totale
     * Get total harvest quantity for this collecte.
     */
    @GetMapping("/{id}/quantite-totale")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'AGRICULTEUR')")
    public ResponseEntity<Double> getQuantiteTotale(@PathVariable String id) {
        return ResponseEntity.ok(collecteService.calculerQuantiteTotale(id));
    }

    /**
     * GET /api/collectes/{id}/complete
     * Check if all tournées in this collecte are finished.
     */
    @GetMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'AGRICULTEUR')")
    public ResponseEntity<Boolean> isComplete(@PathVariable String id) {
        return ResponseEntity.ok(collecteService.isComplete(id));
    }
}