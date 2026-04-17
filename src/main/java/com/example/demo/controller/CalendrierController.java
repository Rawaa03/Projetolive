package com.example.demo.controller;

import com.example.demo.dto.EvenementCalendrierDTO;
import com.example.demo.service.CalendrierService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/calendrier")
@RequiredArgsConstructor
public class CalendrierController {

    private final CalendrierService calendrierService;
    @GetMapping("/verger/{vergerId}/travailleur/{travailleurId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    @Operation(summary = "Planning par verger ET par travailleur")
    public ResponseEntity<List<EvenementCalendrierDTO>> getPlanningByVergerAndTravailleur(
            @PathVariable String vergerId,
            @PathVariable String travailleurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fin) {
        return ResponseEntity.ok(calendrierService.getEvenementsByVergerAndTravailleur(vergerId, travailleurId, debut, fin));
    }
    // ✅ MODIFIÉ - Accessible à tous les rôles
    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'AGRICULTEUR', 'TRAVAILLEUR')")
    @Operation(summary = "Consulter le planning des collectes (filtré par rôle)")
    public ResponseEntity<List<EvenementCalendrierDTO>> getPlanning(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fin) {
        
        // Le service filtre automatiquement selon l'utilisateur connecté
        return ResponseEntity.ok(calendrierService.getEvenements(debut, fin));
    }

    // ✅ GARDER - Pour admin/responsable uniquement
    @GetMapping("/verger/{vergerId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN', 'AGRICULTEUR')")
    @Operation(summary = "Planning par verger")
    public ResponseEntity<List<EvenementCalendrierDTO>> getPlanningByVerger(
            @PathVariable String vergerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fin) {
        return ResponseEntity.ok(calendrierService.getEvenementsByVerger(vergerId, debut, fin));
    }

    // ✅ GARDER - Pour admin/responsable uniquement
    @GetMapping("/travailleur/{travailleurId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    @Operation(summary = "Planning par travailleur (admin uniquement)")
    public ResponseEntity<List<EvenementCalendrierDTO>> getPlanningByTravailleur(
            @PathVariable String travailleurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fin) {
        return ResponseEntity.ok(calendrierService.getEvenementsByTravailleur(travailleurId, debut, fin));
    }

    // ✅ AJOUTER - Pour qu'un travailleur voie son propre planning
    @GetMapping("/mon-planning")
    @PreAuthorize("hasAnyRole('TRAVAILLEUR', 'AGRICULTEUR')")
    @Operation(summary = "Mon planning personnel (pour travailleur et agriculteur)")
    public ResponseEntity<List<EvenementCalendrierDTO>> getMonPlanning(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fin) {
        
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        return ResponseEntity.ok(calendrierService.getEvenementsByUserEmail(email, debut, fin));
    }

    // ✅ GARDER - Pour admin/responsable uniquement
    @PutMapping("/{tourneeId}/reprogrammer")
    @PreAuthorize("hasAnyRole('RESPONSABLE', 'ADMIN')")
    @Operation(summary = "Reprogrammer une collecte")
    public ResponseEntity<EvenementCalendrierDTO> reprogrammer(
            @PathVariable String tourneeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date nouvelleDate,
            @RequestParam(required = false) String raison) {
        return ResponseEntity.ok(calendrierService.reprogrammerEvenement(tourneeId, nouvelleDate, raison));
    }
}