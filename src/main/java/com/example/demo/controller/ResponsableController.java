package com.example.demo.controller;

import com.example.demo.model.Ressource;
import com.example.demo.service.TravailleurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employe")
@PreAuthorize("hasRole('RESPONSABLE')")
@CrossOrigin(origins = "http://localhost:4200")
public class ResponsableController {
    
    @Autowired
    private TravailleurService travailleurService;
    
    @GetMapping("/travailleurs")
    public ResponseEntity<List<Ressource>> getAllTravailleurs() {
        return ResponseEntity.ok(travailleurService.getAllTravailleurs());
    }
    
    @PostMapping("/travailleurs")
    public ResponseEntity<?> createTravailleur(@RequestBody Ressource travailleur) {
        Ressource created = travailleurService.createTravailleur(travailleur);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Travailleur créé");
        response.put("travailleur", created);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/travailleurs/{id}")
    public ResponseEntity<Ressource> getTravailleurById(@PathVariable String id) {
        return ResponseEntity.ok(travailleurService.getTravailleurById(id));
    }
    
    @PutMapping("/travailleurs/{id}")
    public ResponseEntity<?> updateTravailleur(@PathVariable String id, @RequestBody Ressource travailleur) {
        Ressource updated = travailleurService.updateTravailleur(id, travailleur);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Travailleur modifié");
        response.put("travailleur", updated);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/travailleurs/{id}")
    public ResponseEntity<?> deleteTravailleur(@PathVariable String id) {
        travailleurService.deleteTravailleur(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Travailleur supprimé");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/travailleurs/disponibles")
    public ResponseEntity<List<Ressource>> getTravailleursDisponibles() {
        return ResponseEntity.ok(travailleurService.getTravailleursDisponibles());
    }
    
    @GetMapping("/travailleurs/specialite/{specialite}")
    public ResponseEntity<List<Ressource>> getTravailleursBySpecialite(@PathVariable String specialite) {
        return ResponseEntity.ok(travailleurService.getTravailleursBySpecialite(specialite));
    }
}