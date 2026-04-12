package com.example.demo.service;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import com.example.demo.repository.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BenneService {

    @Autowired private RessourceRepository ressourceRepository;
    @Autowired private RessourceService    ressourceService;

    // ── CRUD ──────────────────────────────────────────────────────

    public Ressource creerBenne(Ressource benne) {
        benne.setType(TypeRessource.BENNE);
        if (benne.getCapaciteKg() == null || benne.getCapaciteKg() <= 0)
            throw new RuntimeException("La capacité d'une benne doit être positive");
        benne.setQuantiteChargeeActuelle(0.0);
        benne.setTauxRemplissage(0.0);
        benne.setEstPleine(false);
        if (benne.getStatut() == null) benne.setStatut("DISPONIBLE");
        return ressourceRepository.save(benne);
    }

    public Ressource getBenneById(String id) {
        Ressource r = ressourceService.getRessourceById(id);
        if (r.getType() != TypeRessource.BENNE)
            throw new RuntimeException("Cette ressource n'est pas une benne");
        return r;
    }

    public List<Ressource> listerBennes()            { return ressourceRepository.findAllBennes(); }
    public List<Ressource> listerBennesDisponibles() { return ressourceRepository.findBennesByStatut("DISPONIBLE"); }
    public List<Ressource> listerBennesPleines()     { return ressourceRepository.findFullBennes(); }

    public Ressource mettreAJourBenne(String id, Ressource update) {
        Ressource benne = getBenneById(id);
        benne.setNom(update.getNom());
        benne.setImmatriculation(update.getImmatriculation());
        benne.setStatut(update.getStatut());
        if (update.getCapaciteKg() != null && update.getCapaciteKg() > 0)
            benne.setCapaciteKg(update.getCapaciteKg());
        return ressourceRepository.save(benne);
    }

    public void supprimerBenne(String id) {
        ressourceRepository.delete(getBenneById(id));
    }

    // ── Load management ───────────────────────────────────────────

    public Ressource ajouterCharge(String id, Double quantite) {
        if (quantite == null || quantite <= 0)
            throw new RuntimeException("La quantité à ajouter doit être positive");
        Ressource benne = getBenneById(id);
        try {
            benne.ajouterCharge(quantite);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erreur lors de l'ajout de charge : " + e.getMessage());
        }
        return ressourceRepository.save(benne);
    }

    public Ressource viderBenne(String id) {
        Ressource benne = getBenneById(id);
        benne.vider();
        return ressourceRepository.save(benne);
    }

    // ── Maintenance ───────────────────────────────────────────────

    public Ressource enregistrerMaintenance(String id, String description, Double cout) {
        Ressource benne = getBenneById(id);
        benne.setStatut("MAINTENANCE");
        return ressourceRepository.save(benne);
    }

    public Ressource terminerMaintenance(String id) {
        Ressource benne = getBenneById(id);
        if (!"MAINTENANCE".equals(benne.getStatut()))
            throw new RuntimeException("Cette benne n'est pas en maintenance");
        benne.setStatut("DISPONIBLE");
        return ressourceRepository.save(benne);
    }

    // ── Tractor assignment ────────────────────────────────────────

    public Ressource assignerTracteur(String benneId, String tracteurId) {
        Ressource benne = getBenneById(benneId);
        Ressource tracteur = ressourceService.getRessourceById(tracteurId);
        if (tracteur.getType() != TypeRessource.TRACTEUR)
            throw new RuntimeException("Cette ressource n'est pas un tracteur");
        benne.setTracteurAttacheId(tracteurId);
        return ressourceRepository.save(benne);
    }

    public Ressource retirerTracteur(String benneId) {
        Ressource benne = getBenneById(benneId);
        benne.setTracteurAttacheId(null);
        return ressourceRepository.save(benne);
    }

    public List<Ressource> listerBennesDuTracteur(String tracteurId) {
        return ressourceRepository.findBennesByTracteur(tracteurId);
    }

    // ── Stats ─────────────────────────────────────────────────────

    public Map<String, Object> obtenirStatistiques(String id) {
        Ressource benne = getBenneById(id);
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("id",                benne.getId());
        stats.put("nom",               benne.getNom());
        stats.put("statut",            benne.getStatut());
        stats.put("capaciteMax",       benne.getCapaciteKg());
        stats.put("quantiteActuelle",  benne.getQuantiteChargeeActuelle());
        stats.put("tauxRemplissage",   benne.getTauxRemplissage());
        stats.put("estPleine",         benne.getEstPleine());
        stats.put("tracteurAssigne",   benne.getTracteurAttacheId());
        stats.put("nombreTournees",    benne.getNombreTournees());
        return stats;
    }
}