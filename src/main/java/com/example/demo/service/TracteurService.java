package com.example.demo.service;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import com.example.demo.repository.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TracteurService {

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private RessourceService ressourceService;

    // ==================== TRACTOR CREATION & MANAGEMENT ====================

    /**
     * Créer un nouveau tracteur
     */
    public Ressource creerTracteur(Ressource tracteur) {
        System.out.println("🚜 Création d'un tracteur: " + tracteur.getNom());
        
        tracteur.setType(TypeRessource.TRACTEUR);
        
        if (tracteur.getPuissance() == null || tracteur.getPuissance().trim().isEmpty()) {
            throw new RuntimeException("La puissance du tracteur est requise");
        }
        
        if (tracteur.getCarburant() == null || tracteur.getCarburant().trim().isEmpty()) {
            throw new RuntimeException("Le type de carburant est requis");
        }
        
        if (tracteur.getKilometrage() == null) {
            tracteur.setKilometrage(0.0);
        }
        
        if (tracteur.getConsommationHoraire() == null) {
            tracteur.setConsommationHoraire(0.0);
        }
        
        if (tracteur.getARemorque() == null) {
            tracteur.setARemorque(false);
        }
        
        if (tracteur.getStatut() == null) {
            tracteur.setStatut("DISPONIBLE");
        }
        
        return ressourceRepository.save(tracteur);
    }

    /**
     * Récupérer un tracteur par ID
     */
    public Ressource getTracteurById(String id) {
        Ressource ressource = ressourceService.getRessourceById(id);
        
        if (ressource.getType() != TypeRessource.TRACTEUR) {
            throw new RuntimeException("Cette ressource n'est pas un tracteur");
        }
        
        return ressource;
    }

    /**
     * Lister tous les tracteurs
     */
    public List<Ressource> listerTracteurs() {
        return ressourceRepository.findAllTracteurs();
    }

    /**
     * Lister les tracteurs par statut
     */
    public List<Ressource> listerTracteursByStatut(String statut) {
        return ressourceRepository.findTracteursByStatut(statut);
    }

    /**
     * Lister les tracteurs disponibles
     */
    public List<Ressource> listerTracteurDisponibles() {
        return ressourceRepository.findTracteursByStatut("DISPONIBLE");
    }

    /**
     * Mettre à jour un tracteur
     */
    public Ressource mettreAJourTracteur(String id, Ressource tracteurUpdate) {
        Ressource tracteur = getTracteurById(id);
        
        tracteur.setNom(tracteurUpdate.getNom());
        tracteur.setImmatriculation(tracteurUpdate.getImmatriculation());
        tracteur.setStatut(tracteurUpdate.getStatut());
        
        if (tracteurUpdate.getPuissance() != null && !tracteurUpdate.getPuissance().isEmpty()) {
            tracteur.setPuissance(tracteurUpdate.getPuissance());
        }
        
        if (tracteurUpdate.getCarburant() != null && !tracteurUpdate.getCarburant().isEmpty()) {
            tracteur.setCarburant(tracteurUpdate.getCarburant());
        }
        
        if (tracteurUpdate.getConsommationHoraire() != null) {
            tracteur.setConsommationHoraire(tracteurUpdate.getConsommationHoraire());
        }
        
        if (tracteurUpdate.getARemorque() != null) {
            tracteur.setARemorque(tracteurUpdate.getARemorque());
        }
        
        return ressourceRepository.save(tracteur);
    }

    /**
     * Supprimer un tracteur
     */
    public void supprimerTracteur(String id) {
        Ressource tracteur = getTracteurById(id);
        ressourceRepository.delete(tracteur);
    }

    // ==================== ENGINE SPECIFICATIONS ====================

    /**
     * Obtenir les spécifications du moteur
     */
    public Map<String, Object> obtenirSpecs(String tracteurId) {
        Ressource tracteur = getTracteurById(tracteurId);
        
        Map<String, Object> specs = new HashMap<>();
        specs.put("tracteurId", tracteur.getId());
        specs.put("nom", tracteur.getNom());
        specs.put("immatriculation", tracteur.getImmatriculation());
        specs.put("puissance", tracteur.getPuissance());
        specs.put("carburant", tracteur.getCarburant());
        specs.put("consommationHoraire", tracteur.getConsommationHoraire());
        specs.put("aRemorque", tracteur.getARemorque());
        
        return specs;
    }

    /**
     * Mettre à jour la puissance
     */
    public Ressource mettreAJourPuissance(String tracteurId, String puissance) {
        System.out.println("⚡ Mise à jour de la puissance du tracteur");
        
        Ressource tracteur = getTracteurById(tracteurId);
        
        if (puissance == null || puissance.trim().isEmpty()) {
            throw new RuntimeException("La puissance ne peut pas être vide");
        }
        
        tracteur.setPuissance(puissance);
        return ressourceRepository.save(tracteur);
    }

    /**
     * Mettre à jour la consommation horaire
     */
    public Ressource mettreAJourConsommation(String tracteurId, Double consommation) {
        System.out.println("⛽ Mise à jour de la consommation horaire");
        
        Ressource tracteur = getTracteurById(tracteurId);
        
        if (consommation == null || consommation < 0) {
            throw new RuntimeException("La consommation doit être un nombre positif");
        }
        
        tracteur.setConsommationHoraire(consommation);
        return ressourceRepository.save(tracteur);
    }

    /**
     * Mettre à jour le type de carburant
     */
    public Ressource mettreAJourCarburant(String tracteurId, String carburant) {
        System.out.println("🛢️ Mise à jour du type de carburant");
        
        Ressource tracteur = getTracteurById(tracteurId);
        
        if (carburant == null || carburant.trim().isEmpty()) {
            throw new RuntimeException("Le type de carburant ne peut pas être vide");
        }
        
        tracteur.setCarburant(carburant);
        return ressourceRepository.save(tracteur);
    }

    // ==================== MILEAGE & FUEL TRACKING ====================

    /**
     * Mettre à jour le kilométrage
     */
    public Ressource mettreAJourKilometrage(String tracteurId, Double kilometrage) {
        System.out.println("🛣️ Mise à jour du kilométrage");
        
        Ressource tracteur = getTracteurById(tracteurId);
        
        if (kilometrage == null || kilometrage < 0) {
            throw new RuntimeException("Le kilométrage doit être positif");
        }
        
        if (kilometrage < tracteur.getKilometrage()) {
            throw new RuntimeException("Le kilométrage ne peut pas diminuer");
        }
        
        tracteur.setKilometrage(kilometrage);
        return ressourceRepository.save(tracteur);
    }

    /**
     * Obtenir les informations de kilométrage
     */
    public Map<String, Object> obtenirInfoKilometrage(String tracteurId) {
        Ressource tracteur = getTracteurById(tracteurId);
        
        Map<String, Object> info = new HashMap<>();
        info.put("tracteurId", tracteur.getId());
        info.put("nom", tracteur.getNom());
        info.put("kilometrage", tracteur.getKilometrage());
        
        return info;
    }

    /**
     * Calculer la consommation estimée pour une distance donnée
     */
    public Map<String, Double> calculerConsommationEstimee(String tracteurId, Double distance) {
        System.out.println("📊 Calcul de la consommation estimée");
        
        Ressource tracteur = getTracteurById(tracteurId);
        
        if (distance == null || distance <= 0) {
            throw new RuntimeException("La distance doit être positive");
        }
        
        // Calculer la consommation en litres (distance en km, consommation en l/h)
        // Formule simplifiée: si on a une moyenne de vitesse, on peut calculer
        // Pour maintenant: consommation totale = consommation horaire
        Double consommationEstimee = tracteur.getConsommationHoraire();
        
        Map<String, Double> result = new HashMap<>();
        result.put("distance", distance);
        result.put("consommationHoraire", tracteur.getConsommationHoraire());
        result.put("consommationEstimee", consommationEstimee);
        
        return result;
    }

    // ==================== DRIVER ASSIGNMENT ====================

    /**
     * Assigner un conducteur au tracteur
     */
    public Ressource assignerConducteur(String tracteurId, String conducteurId) {
        System.out.println("👨‍✈️ Assignation d'un conducteur au tracteur");
        
        Ressource tracteur = getTracteurById(tracteurId);
        
        // Valider que le conducteur existe (si nécessaire)
        // Ressource conducteur = ressourceService.getRessourceById(conducteurId);
        
        tracteur.setConducteurId(conducteurId);
        return ressourceRepository.save(tracteur);
    }

    /**
     * Retirer le conducteur assigné
     */
    public Ressource retirerConducteur(String tracteurId) {
        System.out.println("🔓 Retrait du conducteur");
        
        Ressource tracteur = getTracteurById(tracteurId);
        tracteur.setConducteurId(null);
        
        return ressourceRepository.save(tracteur);
    }

    /**
     * Obtenir le conducteur assigné
     */
    public String obtenirConducteurAssigne(String tracteurId) {
        Ressource tracteur = getTracteurById(tracteurId);
        return tracteur.getConducteurId();
    }

    /**
     * Lister les tracteurs assignés à un conducteur
     */
    public List<Ressource> listerTracteursDuConducteur(String conducteurId) {
        return ressourceRepository.findTracteursByDriver(conducteurId);
    }

    /**
     * Lister les tracteurs avec un type de carburant spécifique
     */
    public List<Ressource> listerTracteursByCarburant(String carburant) {
        return ressourceRepository.findTracteursByFuel(carburant);
    }

    // ==================== MAINTENANCE MANAGEMENT ====================

    /**
     * Enregistrer une opération de maintenance
     */
    public Ressource enregistrerMaintenance(String tracteurId, String description, Double cout) {
        System.out.println("🔧 Maintenance enregistrée pour le tracteur");
        
        Ressource tracteur = getTracteurById(tracteurId);
        
        // Créer une entrée de maintenance
        Map<String, Object> maintenance = new HashMap<>();
        maintenance.put("date", new Date());
        maintenance.put("description", description);
        maintenance.put("cout", cout);
        
        // Stocker dans une structure appropriée (à adapter selon votre schéma)
        tracteur.setStatut("MAINTENANCE");
        
        return ressourceRepository.save(tracteur);
    }

    /**
     * Terminer la maintenance et rendre le tracteur disponible
     */
    public Ressource terminerMaintenance(String tracteurId) {
        System.out.println("✅ Fin de la maintenance");
        
        Ressource tracteur = getTracteurById(tracteurId);
        
        if (!tracteur.getStatut().equals("MAINTENANCE")) {
            throw new RuntimeException("Ce tracteur n'est pas en maintenance");
        }
        
        tracteur.setStatut("DISPONIBLE");
        return ressourceRepository.save(tracteur);
    }

    /**
     * Lister les tracteurs en maintenance
     */
    public List<Ressource> listerTacteursEnMaintenance() {
        return ressourceRepository.findTracteursByStatut("MAINTENANCE");
    }

    /**
     * Lister les tracteurs avec une remorque
     */
    public List<Ressource> listerTracteurAvecRemorque() {
        return listerTracteurs().stream()
                .filter(t -> t.getARemorque() != null && t.getARemorque())
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les statistiques complètes d'un tracteur
     */
    public Map<String, Object> obtenirStatistiques(String tracteurId) {
        Ressource tracteur = getTracteurById(tracteurId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("tracteurId", tracteur.getId());
        stats.put("nom", tracteur.getNom());
        stats.put("immatriculation", tracteur.getImmatriculation());
        stats.put("type", "TRACTEUR");
        stats.put("statut", tracteur.getStatut());
        stats.put("puissance", tracteur.getPuissance());
        stats.put("carburant", tracteur.getCarburant());
        stats.put("consommationHoraire", tracteur.getConsommationHoraire());
        stats.put("kilometrage", tracteur.getKilometrage());
        stats.put("aRemorque", tracteur.getARemorque());
        stats.put("conducteurId", tracteur.getConducteurId());
        stats.put("nombreTournees", tracteur.getNombreTournees());
        stats.put("enTournee", tracteur.estEnTournee());
        
        return stats;
    }

    /**
     * Marquer comme ayant une remorque
     */
    public Ressource marquerAvecRemorque(String tracteurId, Boolean avecRemorque) {
        System.out.println("🚛 Mise à jour du statut de remorque");
        
        Ressource tracteur = getTracteurById(tracteurId);
        tracteur.setARemorque(avecRemorque);
        
        return ressourceRepository.save(tracteur);
    }
}
