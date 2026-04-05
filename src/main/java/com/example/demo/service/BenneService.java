package com.example.demo.service;

import com.example.demo.model.Ressource;
import com.example.demo.model.TypeRessource;
import com.example.demo.repository.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BenneService {

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private RessourceService ressourceService;

    // ==================== BENNE CREATION & MANAGEMENT ====================

    /**
     * Créer une nouvelle benne
     */
    public Ressource creerBenne(Ressource benne) {
        System.out.println("🛢️ Création d'une benne: " + benne.getNom());
        
        benne.setType(TypeRessource.BENNE);
        
        if (benne.getCapaciteKg() == null || benne.getCapaciteKg() <= 0) {
            throw new RuntimeException("La capacité d'une benne doit être positive");
        }
        
        benne.setQuantiteChargeeActuelle(0.0);
        benne.setTauxRemplissage(0.0);
        benne.setEstPleine(false);
        
        if (benne.getStatut() == null) {
            benne.setStatut("DISPONIBLE");
        }
        
        return ressourceRepository.save(benne);
    }

    /**
     * Récupérer une benne par ID
     */
    public Ressource getBenneById(String id) {
        Ressource ressource = ressourceService.getRessourceById(id);
        
        if (ressource.getType() != TypeRessource.BENNE) {
            throw new RuntimeException("Cette ressource n'est pas une benne");
        }
        
        return ressource;
    }

    /**
     * Lister toutes les bennes
     */
    public List<Ressource> listerBennes() {
        return ressourceRepository.findAllBennes();
    }

    /**
     * Lister les bennes par statut
     */
    public List<Ressource> listerBennesByStatut(String statut) {
        return ressourceRepository.findBennesByStatut(statut);
    }

    /**
     * Lister les bennes disponibles
     */
    public List<Ressource> listerBennesDisponibles() {
        return ressourceRepository.findBennesByStatut("DISPONIBLE");
    }

    /**
     * Mettre à jour une benne
     */
    public Ressource mettreAJourBenne(String id, Ressource benneUpdate) {
        Ressource benne = getBenneById(id);
        
        benne.setNom(benneUpdate.getNom());
        benne.setImmatriculation(benneUpdate.getImmatriculation());
        benne.setStatut(benneUpdate.getStatut());
        
        if (benneUpdate.getCapaciteKg() != null && benneUpdate.getCapaciteKg() > 0) {
            benne.setCapaciteKg(benneUpdate.getCapaciteKg());
        }
        
        return ressourceRepository.save(benne);
    }

    /**
     * Supprimer une benne
     */
    public void supprimerBenne(String id) {
        Ressource benne = getBenneById(id);
        ressourceRepository.delete(benne);
    }

    // ==================== LOAD MANAGEMENT ====================

    /**
     * Ajouter une charge à la benne
     */
    public Ressource ajouterCharge(String benneId, Double quantite) {
        System.out.println("📦 Ajout de charge à la benne: " + quantite + " kg");
        
        Ressource benne = getBenneById(benneId);
        
        if (quantite == null || quantite <= 0) {
            throw new RuntimeException("La quantité à ajouter doit être positive");
        }
        
        try {
            benne.ajouterCharge(quantite);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erreur lors de l'ajout de charge: " + e.getMessage());
        }
        
        return ressourceRepository.save(benne);
    }

    /**
     * Vider une benne
     */
    public Ressource viderBenne(String benneId) {
        System.out.println("🧹 Vidage d'une benne");
        
        Ressource benne = getBenneById(benneId);
        benne.vider();
        
        return ressourceRepository.save(benne);
    }

    /**
     * Obtenir les informations de capacité
     */
    public Map<String, Object> obtenirInfoCapacite(String benneId) {
        Ressource benne = getBenneById(benneId);
        
        Map<String, Object> capacite = new HashMap<>();
        capacite.put("benneId", benne.getId());
        capacite.put("nom", benne.getNom());
        capacite.put("capaciteMax", benne.getCapaciteKg());
        capacite.put("quantiteActuelle", benne.getQuantiteChargeeActuelle());
        capacite.put("tauxRemplissage", benne.getTauxRemplissage());
        capacite.put("estPleine", benne.getEstPleine());
        capacite.put("capaciteDisponible", benne.getCapaciteKg() - benne.getQuantiteChargeeActuelle());
        
        return capacite;
    }

    /**
     * Vérifier si une benne est pleine
     */
    public boolean estPleine(String benneId) {
        Ressource benne = getBenneById(benneId);
        return benne.getEstPleine();
    }

    /**
     * Obtenir le taux de remplissage
     */
    public Double obtenirTauxRemplissage(String benneId) {
        Ressource benne = getBenneById(benneId);
        return benne.getTauxRemplissage();
    }

    /**
     * Lister les bennes pleines
     */
    public List<Ressource> listerBennesPleines() {
        return ressourceRepository.findFullBennes();
    }

    /**
     * Lister les bennes avec un taux de remplissage dans une plage
     */
    public List<Ressource> listerBennesByFillPercentage(Double minPercentage, Double maxPercentage) {
        return ressourceRepository.findBennesByFillPercentage(minPercentage, maxPercentage);
    }

    // ==================== TRACTOR ASSIGNMENT ====================

    /**
     * Assigner un tracteur à une benne
     */
    public Ressource assignerTracteur(String benneId, String tracteurId) {
        System.out.println("🔗 Assignation d'un tracteur à une benne");
        
        Ressource benne = getBenneById(benneId);
        Ressource tracteur = ressourceService.getRessourceById(tracteurId);
        
        if (tracteur.getType() != TypeRessource.TRACTEUR) {
            throw new RuntimeException("Cette ressource n'est pas un tracteur");
        }
        
        benne.setTracteurAttacheId(tracteurId);
        return ressourceRepository.save(benne);
    }

    /**
     * Retirer l'assignation d'un tracteur
     */
    public Ressource retirerTracteur(String benneId) {
        System.out.println("🔓 Retrait de l'assignation du tracteur");
        
        Ressource benne = getBenneById(benneId);
        benne.setTracteurAttacheId(null);
        
        return ressourceRepository.save(benne);
    }

    /**
     * Lister les bennes assignées à un tracteur
     */
    public List<Ressource> listerBennesAssigneesAuTracteur(String tracteurId) {
        return ressourceRepository.findBennesByTracteur(tracteurId);
    }

    /**
     * Obtenir le tracteur assigné à une benne
     */
    public Ressource obtenirTracteurAssigne(String benneId) {
        Ressource benne = getBenneById(benneId);
        
        if (benne.getTracteurAttacheId() == null) {
            throw new RuntimeException("Aucun tracteur assigné à cette benne");
        }
        
        return ressourceService.getRessourceById(benne.getTracteurAttacheId());
    }

    // ==================== MAINTENANCE MANAGEMENT ====================

    /**
     * Enregistrer une opération de maintenance
     */
    public Ressource enregistrerMaintenance(String benneId, String description, Double cout) {
        System.out.println("🔧 Maintenance enregistrée pour la benne");
        
        Ressource benne = getBenneById(benneId);
        
        // Créer une entrée de maintenance
        Map<String, Object> maintenance = new HashMap<>();
        maintenance.put("date", new Date());
        maintenance.put("description", description);
        maintenance.put("cout", cout);
        
        // Stocker dans une structure appropriée (à adapter selon votre schéma)
        benne.setStatut("MAINTENANCE");
        
        return ressourceRepository.save(benne);
    }

    /**
     * Terminer la maintenance et rendre la benne disponible
     */
    public Ressource terminerMaintenance(String benneId) {
        System.out.println("✅ Fin de la maintenance");
        
        Ressource benne = getBenneById(benneId);
        
        if (!benne.getStatut().equals("MAINTENANCE")) {
            throw new RuntimeException("Cette benne n'est pas en maintenance");
        }
        
        benne.setStatut("DISPONIBLE");
        return ressourceRepository.save(benne);
    }

    /**
     * Lister les bennes en maintenance
     */
    public List<Ressource> listerBennesEnMaintenance() {
        return ressourceRepository.findBennesByStatut("MAINTENANCE");
    }

    // ==================== WEAR & STATUS MANAGEMENT ====================

    /**
     * Mettre à jour le statut d'usure
     */
    public Ressource mettreAJourStatutUsure(String benneId, String statutUsure) {
        System.out.println("⚠️ Mise à jour du statut d'usure: " + statutUsure);
        
        Ressource benne = getBenneById(benneId);
        
        // Statuts possibles: BON, MOYEN, MAUVAIS
        if (!statutUsure.matches("BON|MOYEN|MAUVAIS")) {
            throw new RuntimeException("Statut d'usure invalide. Valeurs acceptées: BON, MOYEN, MAUVAIS");
        }
        
        // Ajouter un champ pour le statut d'usure si nécessaire
        // Pour maintenant, on peut l'ajouter via une propriété personnalisée
        
        return benne;
    }

    /**
     * Obtenir les statistiques d'une benne
     */
    public Map<String, Object> obtenirStatistiques(String benneId) {
        Ressource benne = getBenneId(benneId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("benneId", benne.getId());
        stats.put("nom", benne.getNom());
        stats.put("type", "BENNE");
        stats.put("statut", benne.getStatut());
        stats.put("capaciteMax", benne.getCapaciteKg());
        stats.put("quantiteActuelle", benne.getQuantiteChargeeActuelle());
        stats.put("tauxRemplissage", benne.getTauxRemplissage());
        stats.put("estPleine", benne.getEstPleine());
        stats.put("tracteurAssigne", benne.getTracteurAttacheId());
        stats.put("nombreTournees", benne.getNombreTournees());
        stats.put("enTournee", benne.estEnTournee());
        stats.put("quantiteTotalCollectee", benne.getQuantiteCollectee());
        
        return stats;
    }

    // ==================== HELPER METHODS ====================

    private Ressource getBenneId(String benneId) {
        return ressourceService.getRessourceById(benneId);
    }
}
