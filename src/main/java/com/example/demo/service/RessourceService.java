package com.example.demo.service;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import com.example.demo.repository.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RessourceService {

    @Autowired
    private RessourceRepository ressourceRepository;

    // ── CRUD ──────────────────────────────────────────────────────

    public Ressource creerRessource(Ressource ressource) {
        if (ressource.getType() == null) {
            throw new RuntimeException("Le type de ressource est requis (BENNE ou TRACTEUR)");
        }
        if (ressource.getNom() == null || ressource.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom de la ressource est requis");
        }
        if (ressource.getStatut() == null) {
            ressource.setStatut("DISPONIBLE");
        }
        if (ressource.getType() == TypeRessource.BENNE)    validateBenne(ressource);
        if (ressource.getType() == TypeRessource.TRACTEUR) validateTracteur(ressource);
        return ressourceRepository.save(ressource);
    }

    public Ressource getRessourceById(String id) {
        return ressourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource non trouvée : " + id));
    }

    public List<Ressource> listerToutesLesRessources() {
        return ressourceRepository.findAll();
    }

    public Ressource mettreAJourRessource(String id, Ressource update) {
        Ressource existant = getRessourceById(id);
        existant.setNom(update.getNom());
        existant.setStatut(update.getStatut());
        existant.setImmatriculation(update.getImmatriculation());

        if (existant.getType() == TypeRessource.BENNE) {
            existant.setCapaciteKg(update.getCapaciteKg());
            existant.setTracteurAttacheId(update.getTracteurAttacheId());
        } else if (existant.getType() == TypeRessource.TRACTEUR) {
            existant.setPuissance(update.getPuissance());
            existant.setCarburant(update.getCarburant());
            existant.setConsommationHoraire(update.getConsommationHoraire());
            existant.setKilometrage(update.getKilometrage());
            existant.setConducteurId(update.getConducteurId());
            existant.setARemorque(update.getARemorque());
        }
        return ressourceRepository.save(existant);
    }

    public void supprimerRessource(String id) {
        ressourceRepository.delete(getRessourceById(id));
    }

    // ── Search ────────────────────────────────────────────────────

    public List<Ressource> listerBennes()    { return ressourceRepository.findAllBennes(); }
    public List<Ressource> listerTracteurs() { return ressourceRepository.findAllTracteurs(); }
    public List<Ressource> listerDisponibles() { return ressourceRepository.findAllAvailable(); }
    public List<Ressource> listerDisponiblesParType(TypeRessource type) {
        return ressourceRepository.findAvailableByType(type);
    }

    // ── Status info ───────────────────────────────────────────────

    public Map<String, Object> obtenirStatut(String id) {
        Ressource r = getRessourceById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("id", r.getId());
        map.put("nom", r.getNom());
        map.put("type", r.getType());
        map.put("statut", r.getStatut());
        map.put("disponible", r.estDisponible());
        map.put("enTournee", r.estEnTournee());
        map.put("nombreTournees", r.getNombreTournees());
        return map;
    }

    // ── Validation ────────────────────────────────────────────────

    private void validateBenne(Ressource r) {
        if (r.getCapaciteKg() == null || r.getCapaciteKg() <= 0)
            throw new RuntimeException("La capacité d'une benne doit être positive");
        if (r.getQuantiteChargeeActuelle() == null) r.setQuantiteChargeeActuelle(0.0);
        if (r.getTauxRemplissage() == null)         r.setTauxRemplissage(0.0);
        r.setEstPleine(r.getQuantiteChargeeActuelle() >= r.getCapaciteKg());
    }

    private void validateTracteur(Ressource r) {
        if (r.getPuissance() == null || r.getPuissance().trim().isEmpty())
            throw new RuntimeException("La puissance du tracteur est requise");
        if (r.getCarburant() == null || r.getCarburant().trim().isEmpty())
            throw new RuntimeException("Le type de carburant est requis");
        if (r.getKilometrage() == null)  r.setKilometrage(0.0);
        if (r.getARemorque() == null)    r.setARemorque(false);
    }
}