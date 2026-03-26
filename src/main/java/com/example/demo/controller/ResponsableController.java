package com.example.demo.controller;

import com.example.demo.model.Travailleur;
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
@CrossOrigin(origins = "http://localhost:4200")  // ← AJOUTEZ CETTE LIGNE

public class ResponsableController {
    
    @Autowired
    private TravailleurService travailleurService;
    
    @GetMapping("/travailleurs")
    public ResponseEntity<List<Travailleur>> getAllTravailleurs() {
        return ResponseEntity.ok(travailleurService.getAllTravailleurs());
    }
    
    @PostMapping("/travailleurs")
    public ResponseEntity<?> createTravailleur(@RequestBody Travailleur travailleur) {
        Travailleur created = travailleurService.createTravailleur(travailleur);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Travailleur créé");
        response.put("travailleur", created);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/travailleurs/{id}")
    public ResponseEntity<Travailleur> getTravailleurById(@PathVariable String id) {
        return ResponseEntity.ok(travailleurService.getTravailleurById(id));
    }
    
    @PutMapping("/travailleurs/{id}")
    public ResponseEntity<?> updateTravailleur(@PathVariable String id, @RequestBody Travailleur travailleur) {
        Travailleur updated = travailleurService.updateTravailleur(id, travailleur);
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
}