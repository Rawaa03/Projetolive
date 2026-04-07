package com.example.demo.service;

import com.example.demo.model.Ressource;
import com.example.demo.model.Tournee;
import com.example.demo.model.TypeRessource;
import com.example.demo.repository.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RessourceService {

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private TourneeService tourneeService;

    // ==================== CRUD OPERATIONS ====================

    /**
     * Créer une nouvelle ressource
     */
    public Ressource creerRessource(Ressource ressource) {
        System.out.println("🚜 Création d'une ressource: " + ressource.getNom() + " (Type: " + ressource.getType() + ")");
        
        if (ressource.getType() == null) {
            throw new RuntimeException("Le type de ressource est requis");
        }
        
        if (ressource.getNom() == null || ressource.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom de la ressource est requis");
        }
        
        if (ressource.getStatut() == null) {
            ressource.setStatut("DISPONIBLE");
        }
        
        // Validation spécifique par type
        if (ressource.getType() == TypeRessource.BENNE) {
            validateBenne(ressource);
        } else if (ressource.getType() == TypeRessource.TRACTEUR) {
            validateTracteur(ressource);
        }
        
        return ressourceRepository.save(ressource);
    }

    /**
     * Récupérer une ressource par ID
     */
    public Ressource getRessourceById(String id) {
        return ressourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource non trouvée: " + id));
    }

    /**
     * Lister toutes les ressources
     */
    public List<Ressource> listerToutesLesRessources() {
        return ressourceRepository.findAll();
    }

    /**
     * Mettre à jour une ressource
     */
    public Ressource mettreAJourRessource(String id, Ressource ressourceUpdate) {
        Ressource existant = getRessourceById(id);
        
        existant.setNom(ressourceUpdate.getNom());
        existant.setStatut(ressourceUpdate.getStatut());
        existant.setImmatriculation(ressourceUpdate.getImmatriculation());
        
        // Mise à jour selon le type
        if (existant.getType() == TypeRessource.BENNE) {
            existant.setCapaciteKg(ressourceUpdate.getCapaciteKg());
            existant.setTracteurAttacheId(ressourceUpdate.getTracteurAttacheId());
        } else if (existant.getType() == TypeRessource.TRACTEUR) {
            existant.setPuissance(ressourceUpdate.getPuissance());
            existant.setCarburant(ressourceUpdate.getCarburant());
            existant.setConsommationHoraire(ressourceUpdate.getConsommationHoraire());
            existant.setKilometrage(ressourceUpdate.getKilometrage());
            existant.setConducteurId(ressourceUpdate.getConducteurId());
            existant.setARemorque(ressourceUpdate.getARemorque());
        }
        
        return ressourceRepository.save(existant);
    }

    /**
     * Supprimer une ressource
     */
    public void supprimerRessource(String id) {
        Ressource ressource = getRessourceById(id);
        ressourceRepository.delete(ressource);
    }

    // ==================== SEARCH OPERATIONS ====================

    /**
     * Rechercher par type
     */
    public List<Ressource> rechercherParType(TypeRessource type) {
        return ressourceRepository.findByType(type);
    }

    /**
     * Rechercher par statut
     */
    public List<Ressource> rechercherParStatut(String statut) {
        return ressourceRepository.findByStatut(statut);
    }

    /**
     * Rechercher par type et statut
     */
    public List<Ressource> rechercherParTypeEtStatut(TypeRessource type, String statut) {
        return ressourceRepository.findByTypeAndStatut(type, statut);
    }

    /**
     * Lister les bennes
     */
    public List<Ressource> listerBennes() {
        return ressourceRepository.findAllBennes();
    }

    /**
     * Lister les tracteurs
     */
    public List<Ressource> listerTracteurs() {
        return ressourceRepository.findAllTracteurs();
    }

    /**
     * Lister toutes les ressources disponibles
     */
    public List<Ressource> listerRessourcesDisponibles() {
        return ressourceRepository.findAllAvailable();
    }

    /**
     * Lister les ressources disponibles d'un type donné
     */
    public List<Ressource> listerRessourcesDisponiblesParType(TypeRessource type) {
        return ressourceRepository.findAvailableByType(type);
    }

    /**
     * Lister les ressources en maintenance
     */
    public List<Ressource> listerRessourcesEnMaintenance() {
        return ressourceRepository.findAllUnderMaintenance();
    }

    // ==================== AVAILABILITY OPERATIONS ====================

    /**
     * Vérifier la disponibilité pour une période donnée
     */
    public boolean estDisponiblePour(String ressourceId, Date dateDebut, Date dateFin) {
        Ressource ressource = getRessourceById(ressourceId);
        
        if (!ressource.getStatut().equals("DISPONIBLE")) {
            return false;
        }
        
        return ressource.estDisponiblePour(dateDebut, dateFin);
    }

    /**
     * Lister les ressources disponibles pour une période donnée
     */
    public List<Ressource> listerRessourcesDisponiblesPour(Date dateDebut, Date dateFin) {
        List<Ressource> ressources = listerRessourcesDisponibles();
        return ressources.stream()
                .filter(r -> r.estDisponiblePour(dateDebut, dateFin))
                .collect(Collectors.toList());
    }

    /**
     * Lister les ressources disponibles d'un type pour une période donnée
     */
    public List<Ressource> listerRessourcesDisponiblesParTypePour(TypeRessource type, Date dateDebut, Date dateFin) {
        List<Ressource> ressources = listerRessourcesDisponiblesParType(type);
        return ressources.stream()
                .filter(r -> r.estDisponiblePour(dateDebut, dateFin))
                .collect(Collectors.toList());
    }

    // ==================== TOUR ASSIGNMENT ====================

    /**
     * Assigner une ressource à une tournée
     */
    public Ressource assignerAuTour(String ressourceId, String tourneeId) {
        Ressource ressource = getRessourceById(ressourceId);
        Tournee tournee = tourneeService.getTourneeById(tourneeId);
        
        if (!ressource.estDisponiblePour(tournee.getDateDebut(), tournee.getDateFin())) {
            throw new RuntimeException("Ressource non disponible pour cette période");
        }
        
        ressource.ajouterTournee(tournee);
        ressource.setStatut("OCCUPE");
        
        return ressourceRepository.save(ressource);
    }

    /**
     * Retirer une ressource d'une tournée
     */
    public Ressource retirerDuTour(String ressourceId, String tourneeId) {
        Ressource ressource = getRessourceById(ressourceId);
        
        if (ressource.getTournees() != null) {
            ressource.getTournees().removeIf(t -> t.getId().equals(tourneeId));
        }
        
        // Vérifier si la ressource a d'autres tournées en cours
        if (!ressource.estEnTournee()) {
            ressource.setStatut("DISPONIBLE");
        }
        
        return ressourceRepository.save(ressource);
    }

    /**
     * Lister les ressources assignées à une tournée
     */
    public List<Ressource> listerRessourcesDuTour(String tourneeId) {
        List<Ressource> toutes = listerToutesLesRessources();
        return toutes.stream()
                .filter(r -> r.getTournees() != null && 
                           r.getTournees().stream().anyMatch(t -> t.getId().equals(tourneeId)))
                .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================

    private void validateBenne(Ressource ressource) {
        if (ressource.getCapaciteKg() == null || ressource.getCapaciteKg() <= 0) {
            throw new RuntimeException("La capacité d'une benne doit être positive");
        }
        
        if (ressource.getQuantiteChargeeActuelle() == null) {
            ressource.setQuantiteChargeeActuelle(0.0);
        }
        
        if (ressource.getTauxRemplissage() == null) {
            ressource.setTauxRemplissage(0.0);
        }
        
        ressource.setEstPleine(ressource.getQuantiteChargeeActuelle() >= ressource.getCapaciteKg());
    }

    private void validateTracteur(Ressource ressource) {
        if (ressource.getPuissance() == null || ressource.getPuissance().trim().isEmpty()) {
            throw new RuntimeException("La puissance du tracteur est requise");
        }
        
        if (ressource.getCarburant() == null || ressource.getCarburant().trim().isEmpty()) {
            throw new RuntimeException("Le type de carburant est requis");
        }
        
        if (ressource.getKilometrage() == null) {
            ressource.setKilometrage(0.0);
        }
        
        if (ressource.getARemorque() == null) {
            ressource.setARemorque(false);
        }
    }

    /**
     * Récupérer le statut d'une ressource
     */
    public Map<String, Object> obtenirStatutRessource(String ressourceId) {
        Ressource ressource = getRessourceById(ressourceId);
        
        Map<String, Object> statut = new HashMap<>();
        statut.put("id", ressource.getId());
        statut.put("nom", ressource.getNom());
        statut.put("type", ressource.getType());
        statut.put("statut", ressource.getStatut());
        statut.put("disponible", ressource.getStatut().equals("DISPONIBLE"));
        statut.put("enTournee", ressource.estEnTournee());
        statut.put("tourneeActuelleId", ressource.getTourneeActuelleId());
        statut.put("nombreTournees", ressource.getNombreTournees());
        
        return statut;
    }
}
