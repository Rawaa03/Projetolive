package com.example.demo.controller;

import com.example.demo.dto.CollecteDetailDTO;
import com.example.demo.dto.CollecteRequest;
import com.example.demo.dto.CollecteResponse;
import com.example.demo.dto.CollecteStatsDTO;
import com.example.demo.model.Collecte;
import com.example.demo.model.enums.StatutCollecte;
import com.example.demo.service.CollecteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/collectes")
@RequiredArgsConstructor
@Tag(name = "Collecte", description = "Gestion des campagnes de récolte")
public class CollecteController {

    private final CollecteService collecteService;

    // ═══════════════════════════════════════════════════════════════
    // BASIC CRUD
    // ═══════════════════════════════════════════════════════════════

    @GetMapping
    @Operation(summary = "Récupérer toutes les collectes")
    public ResponseEntity<List<CollecteResponse>> getAllCollectes() {
        List<CollecteResponse> responses = collecteService.getAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une collecte par son ID")
    public ResponseEntity<CollecteResponse> getCollecteById(@PathVariable String id) {
        Collecte collecte = collecteService.getById(id);
        return ResponseEntity.ok(toResponse(collecte));
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Récupérer une collecte avec toutes ses tournées")
    public ResponseEntity<CollecteDetailDTO> getCollecteWithTournees(@PathVariable String id) {
        CollecteDetailDTO detail = collecteService.getCollecteWithTournees(id);
        return ResponseEntity.ok(detail);
    }

    // ═══════════════════════════════════════════════════════════════
    // QUERIES BY VERGER
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/verger/{vergerId}")
    @Operation(summary = "Récupérer toutes les collectes d'un verger")
    public ResponseEntity<List<CollecteResponse>> getCollectesByVerger(@PathVariable String vergerId) {
        List<CollecteResponse> responses = collecteService.getByVerger(vergerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/verger/{vergerId}/active")
    @Operation(summary = "Récupérer les collectes actives d'un verger")
    public ResponseEntity<List<CollecteResponse>> getActiveCollectesByVerger(@PathVariable String vergerId) {
        List<CollecteResponse> responses = collecteService.getByVerger(vergerId).stream()
                .filter(c -> c.getStatut() == StatutCollecte.PLANIFIEE || c.getStatut() == StatutCollecte.EN_COURS)
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ═══════════════════════════════════════════════════════════════
    // QUERIES BY STATUS
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les collectes par statut")
    public ResponseEntity<List<CollecteResponse>> getCollectesByStatut(@PathVariable StatutCollecte statut) {
        List<CollecteResponse> responses = collecteService.getByStatut(statut).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer toutes les collectes actives (PLANIFIEE ou EN_COURS)")
    public ResponseEntity<List<CollecteResponse>> getActiveCollectes() {
        List<CollecteResponse> responses = collecteService.getActiveCollectes().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ═══════════════════════════════════════════════════════════════
    // QUERIES BY YEAR
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/annee/{annee}")
    @Operation(summary = "Récupérer les collectes par année de campagne")
    public ResponseEntity<List<CollecteResponse>> getCollectesByAnnee(@PathVariable String annee) {
        List<CollecteResponse> responses = collecteService.getByAnnee(annee).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ═══════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/{id}/statistiques")
    @Operation(summary = "Récupérer les statistiques détaillées d'une collecte")
    public ResponseEntity<CollecteStatsDTO> getCollecteStats(@PathVariable String id) {
        Collecte collecte = collecteService.getById(id);
        CollecteStatsDTO stats = CollecteStatsDTO.builder()
                .collecteId(collecte.getId())
                .code(collecte.getCode())
                .statut(collecte.getStatut())
                .annee(collecte.getAnnee())
                .nbreTournees(collecte.getNbreTournees())
                .quantiteTotaleKg(collecte.getQuantiteTotaleKg())
                .totalArbresRecoltes(collecte.getTotalArbresRecoltes())
                .rendementMoyenParArbre(collecte.getRendementMoyenParArbre())
                .efficaciteMoyenne(collecte.getEfficaciteMoyenne())
                .dateDebutCampagne(collecte.getDateDebutCampagne())
                .dateFinCampagne(collecte.getDateFinCampagne())
                .estCloturee(collecte.getEstCloturee())
                .build();
        return ResponseEntity.ok(stats);
    }

    // ═══════════════════════════════════════════════════════════════
    // STATE TRANSITIONS
    // ═══════════════════════════════════════════════════════════════

    @PostMapping("/{id}/demarrer")
    @Operation(summary = "Démarrer une collecte (passer de PLANIFIEE à EN_COURS)")
    public ResponseEntity<CollecteResponse> demarrerCollecte(@PathVariable String id) {
        collecteService.demarrerCollecte(id);
        Collecte collecte = collecteService.getById(id);
        return ResponseEntity.ok(toResponse(collecte));
    }

    @PostMapping("/{id}/terminer")
    @Operation(summary = "Terminer une collecte (passer de EN_COURS à TERMINEE)")
    public ResponseEntity<CollecteResponse> terminerCollecte(@PathVariable String id) {
        collecteService.terminerCollecte(id);
        Collecte collecte = collecteService.getById(id);
        return ResponseEntity.ok(toResponse(collecte));
    }

    // ═══════════════════════════════════════════════════════════════
    // UPDATE
    // ═══════════════════════════════════════════════════════════════

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations d'une collecte")
    public ResponseEntity<CollecteResponse> updateCollecte(
            @PathVariable String id,
            @RequestBody CollecteRequest request) {
        Collecte updated = collecteService.updateCollecte(id, request);
        return ResponseEntity.ok(toResponse(updated));
    }

    // ═══════════════════════════════════════════════════════════════
    // DELETE
    // ═══════════════════════════════════════════════════════════════

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une collecte (seulement si aucune tournée associée)")
    public ResponseEntity<Void> deleteCollecte(@PathVariable String id) {
        collecteService.deleteCollecte(id);
        return ResponseEntity.noContent().build();
    }

    // ═══════════════════════════════════════════════════════════════
    // REPORTS
    // ═══════════════════════════════════════════════════════════════

    @GetMapping("/rapport/verger/{vergerId}")
    @Operation(summary = "Rapport des collectes par verger")
    public ResponseEntity<List<CollecteResponse>> getRapportByVerger(@PathVariable String vergerId) {
        List<CollecteResponse> responses = collecteService.getByVerger(vergerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/rapport/annee/{annee}")
    @Operation(summary = "Rapport des collectes par année")
    public ResponseEntity<List<CollecteResponse>> getRapportByAnnee(@PathVariable String annee) {
        List<CollecteResponse> responses = collecteService.getByAnnee(annee).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private CollecteResponse toResponse(Collecte collecte) {
        return CollecteResponse.builder()
                .id(collecte.getId())
                .code(collecte.getCode())
                .statut(collecte.getStatut())
                .annee(collecte.getAnnee())
                .numero(collecte.getNumero())
                .vergerId(collecte.getVergerId())
                .dateDebutCampagne(collecte.getDateDebutCampagne())
                .dateFinCampagne(collecte.getDateFinCampagne())
                .nbreTournees(collecte.getNbreTournees())
                .quantiteTotaleKg(collecte.getQuantiteTotaleKg())
                .totalArbresRecoltes(collecte.getTotalArbresRecoltes())
                .rendementMoyenParArbre(collecte.getRendementMoyenParArbre())
                .efficaciteMoyenne(collecte.getEfficaciteMoyenne())
                .observations(collecte.getObservations())
                .estCloturee(collecte.getEstCloturee())
                .dateCreation(collecte.getDateCreation())
                .build();
    }
}