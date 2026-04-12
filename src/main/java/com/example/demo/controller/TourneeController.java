package com.example.demo.controller;

import com.example.demo.dto.TerminerTourneeRequest;
import com.example.demo.dto.TourneeRequest;
import com.example.demo.dto.TourneeResponse;
import com.example.demo.model.StatutTournee;
import com.example.demo.service.TourneeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TourneeController {

    private final TourneeService tourneeService;

    // ─── CREATE ────────────────────────────────────────────────────────────

    /**
     * POST /api/tournees
     * Create a new tournée (PLANIFIEE).
     * Accessible by RESPONSABLE and ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<TourneeResponse> creer(@Valid @RequestBody TourneeRequest request) {
        return ResponseEntity.ok(tourneeService.creer(request));
    }

    // ─── READ ──────────────────────────────────────────────────────────────

    /**
     * GET /api/tournees
     * List all tournées.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'EQUIPE_RECOLTE')")
    public ResponseEntity<List<TourneeResponse>> getAll() {
        return ResponseEntity.ok(tourneeService.getAll());
    }

    /**
     * GET /api/tournees/active
     * List tournées with statut PLANIFIEE or EN_COURS.
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'EQUIPE_RECOLTE')")
    public ResponseEntity<List<TourneeResponse>> getActive() {
        return ResponseEntity.ok(tourneeService.getActive());
    }

    /**
     * GET /api/tournees/{id}
     * Get a single tournée.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'EQUIPE_RECOLTE')")
    public ResponseEntity<TourneeResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(tourneeService.getById(id));
    }

    /**
     * GET /api/tournees/verger/{vergerId}
     * All tournées for a specific verger.
     */
    @GetMapping("/verger/{vergerId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'AGRICULTEUR')")
    public ResponseEntity<List<TourneeResponse>> getByVerger(@PathVariable String vergerId) {
        return ResponseEntity.ok(tourneeService.getByVerger(vergerId));
    }

    /**
     * GET /api/tournees/statut?statut=PLANIFIEE
     * Filter tournées by statut.
     */
    @GetMapping("/statut")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<List<TourneeResponse>> getByStatut(@RequestParam StatutTournee statut) {
        return ResponseEntity.ok(tourneeService.getByStatut(statut));
    }

    /**
     * GET /api/tournees/verger/{vergerId}/total-collecte
     * Total kg collected across all TERMINEE tournées of a verger.
     */
    @GetMapping("/verger/{vergerId}/total-collecte")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'AGRICULTEUR')")
    public ResponseEntity<Map<String, Object>> getTotalCollecte(@PathVariable String vergerId) {
        Double total = tourneeService.getTotalCollecteParVerger(vergerId);
        int nbNecessaires = tourneeService.calculerNbTourneesNecessaires(vergerId);
        return ResponseEntity.ok(Map.of(
                "vergerId", vergerId,
                "totalCollecteKg", total,
                "nbTourneesNecessaires", nbNecessaires
        ));
    }

    // ─── STATE TRANSITIONS ─────────────────────────────────────────────────

    /**
     * PATCH /api/tournees/{id}/demarrer
     * Start a tournée (PLANIFIEE → EN_COURS).
     */
    @PatchMapping("/{id}/demarrer")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'EQUIPE_RECOLTE')")
    public ResponseEntity<TourneeResponse> demarrer(@PathVariable String id) {
        return ResponseEntity.ok(tourneeService.demarrer(id));
    }

    /**
     * PATCH /api/tournees/{id}/terminer
     * Finish a tournée (EN_COURS → TERMINEE).
     * Body: { "quantiteCollecteeKg": 850.0, "distanceTotale": 12.5, "observations": "..." }
     */
    @PatchMapping("/{id}/terminer")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'EQUIPE_RECOLTE')")
    public ResponseEntity<TourneeResponse> terminer(
            @PathVariable String id,
            @Valid @RequestBody TerminerTourneeRequest request) {
        return ResponseEntity.ok(tourneeService.terminer(id, request));
    }

    /**
     * PATCH /api/tournees/{id}/annuler
     * Cancel a tournée.
     */
    @PatchMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<TourneeResponse> annuler(@PathVariable String id) {
        return ResponseEntity.ok(tourneeService.annuler(id));
    }

    // ─── UPDATE / DELETE ───────────────────────────────────────────────────

    /**
     * PUT /api/tournees/{id}
     * Update a PLANIFIEE tournée.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<TourneeResponse> mettreAJour(
            @PathVariable String id,
            @Valid @RequestBody TourneeRequest request) {
        return ResponseEntity.ok(tourneeService.mettreAJour(id, request));
    }

    /**
     * DELETE /api/tournees/{id}
     * Delete a PLANIFIEE or ANNULEE tournée.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    public ResponseEntity<Map<String, String>> supprimer(@PathVariable String id) {
        tourneeService.supprimer(id);
        return ResponseEntity.ok(Map.of("message", "Tournée supprimée avec succès"));
    }
}