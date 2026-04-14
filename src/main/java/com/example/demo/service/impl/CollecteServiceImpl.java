package com.example.demo.service.impl;

import com.example.demo.dto.CollecteRequest;
import com.example.demo.dto.CollecteResponse;
import com.example.demo.model.Collecte;
import com.example.demo.model.StatutTournee;
import com.example.demo.model.Tournee;
import com.example.demo.model.Verger;
import com.example.demo.model.enums.StatutCollecte;
import com.example.demo.model.enums.StatutVerger;
import com.example.demo.repository.CollecteRepository;
import com.example.demo.repository.TourneeRepository;
import com.example.demo.repository.VergerRepository;
import com.example.demo.service.CollecteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CollecteServiceImpl implements CollecteService {

    private final CollecteRepository collecteRepository;
    private final TourneeRepository tourneeRepository;
    private final VergerRepository vergerRepository;

    private static final int NB_ARBRES_PAR_TOURNEE = 200;
@Override
public CollecteResponse creer(CollecteRequest request) {
    log.info("Création d'une nouvelle collecte");

    // Supprimez ou commentez cette partie
    // Verger verger = vergerRepository.findById(request.getVergerId())
    //         .orElseThrow(() -> new RuntimeException("Verger non trouvé"));
    //
    // if (verger.getStatut() == StatutVerger.RECOLTE) {
    //     throw new IllegalStateException("Ce verger a déjà été récolté");
    // }
    //
    // int nbTourneesPrevues = (int) Math.ceil(
    //         (double) verger.getNbArbre() / NB_ARBRES_PAR_TOURNEE
    //     );

    String code = genererCode();

    Collecte collecte = Collecte.builder()
            .code(code)
            .statut(StatutCollecte.PLANIFIEE)
            .tournees(new ArrayList<>())  // Liste vide au début
            .quantiteTotaleKg(0.0)
            .observations(request.getObservations())
            .build();

    Collecte saved = collecteRepository.save(collecte);
    log.info("Collecte créée avec succès: {}", saved.getId());

    return convertToResponse(saved);
}
    @Override
    public CollecteResponse getById(String id) {
        Collecte collecte = getCollecteById(id);
        return convertToResponse(collecte);
    }

    @Override
    public Collecte getCollecteById(String id) {
        return collecteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collecte non trouvée avec l'id: " + id));
    }

    @Override
    public List<CollecteResponse> getAll() {
        return collecteRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollecteResponse> getByVerger(String vergerId) {
        // Vérifier que le verger existe
        vergerRepository.findById(vergerId)
                .orElseThrow(() -> new RuntimeException("Verger non trouvé"));

        // Récupérer toutes les collectes dont les tournées appartiennent à ce verger
        List<Tournee> tournees = tourneeRepository.findByVergerId(vergerId);
        List<String> collecteIds = tournees.stream()
        	    .map(t -> t.getCollecte() != null ? t.getCollecte().getId() : null)  // ✅ Works
        	    .filter(id -> id != null)
        	    .distinct()
        	    .collect(Collectors.toList());
        return collecteRepository.findAllById(collecteIds).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollecteResponse> getByStatut(StatutCollecte statut) {
        return collecteRepository.findByStatut(statut).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollecteResponse> getActive() {
        return collecteRepository.findByStatutIn(List.of(StatutCollecte.PLANIFIEE, StatutCollecte.EN_COURS))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CollecteResponse demarrer(String id) {
        log.info("Démarrage de la collecte: {}", id);

        Collecte collecte = getCollecteById(id);

        if (collecte.getStatut() != StatutCollecte.PLANIFIEE) {
            throw new IllegalStateException(
                    "Seule une collecte PLANIFIÉE peut être démarrée. Statut actuel: " + collecte.getStatut());
        }

        collecte.setStatut(StatutCollecte.EN_COURS);
        collecte.setDateDebutCampagne(new Date());

        Collecte saved = collecteRepository.save(collecte);
        log.info("Collecte démarrée avec succès: {}", id);

        return convertToResponse(saved);
    }

    @Override
    public CollecteResponse terminer(String id) {
        log.info("Terminaison de la collecte: {}", id);

        Collecte collecte = getCollecteById(id);

        if (collecte.getStatut() != StatutCollecte.EN_COURS) {
            throw new IllegalStateException(
                    "Seule une collecte EN_COURS peut être terminée. Statut actuel: " + collecte.getStatut());
        }

        // Vérifier que toutes les tournées sont terminées
        boolean allTerminees = true;
        String vergerId = null;

        for (Tournee tournee : collecte.getTournees()) {
            if (tournee.getStatut() != StatutTournee.TERMINEE) {
                allTerminees = false;
                break;
            }
            if (vergerId == null) {
                vergerId = tournee.getVerger().getId();
            }
        }

        if (!allTerminees) {
            throw new IllegalStateException(
                    "Impossible de terminer la collecte: toutes les tournées ne sont pas terminées");
        }

        collecte.setStatut(StatutCollecte.TERMINEE);
        collecte.setDateFinCampagne(new Date());

        // Marquer le verger comme récolté
        if (vergerId != null) {
            Verger verger = vergerRepository.findById(vergerId).orElse(null);
            if (verger != null && verger.getStatut() != StatutVerger.RECOLTE) {
                verger.setStatut(StatutVerger.RECOLTE);
                verger.setDateDerniereRecolte(new Date());
                vergerRepository.save(verger);
                log.info("Verger {} marqué comme RECOLTE", vergerId);
            }
        }

        Collecte saved = collecteRepository.save(collecte);
        log.info("Collecte terminée avec succès: {}", id);

        return convertToResponse(saved);
    }

    @Override
    public CollecteResponse annuler(String id) {
        log.info("Annulation de la collecte: {}", id);

        Collecte collecte = getCollecteById(id);

        if (collecte.getStatut() == StatutCollecte.TERMINEE) {
            throw new IllegalStateException("Une collecte TERMINÉE ne peut pas être annulée");
        }

        collecte.setStatut(StatutCollecte.ANNULEE);
        collecte.setDateFinCampagne(new Date());

        Collecte saved = collecteRepository.save(collecte);
        log.info("Collecte annulée avec succès: {}", id);

        return convertToResponse(saved);
    }

    @Override
    public CollecteResponse mettreAJour(String id, CollecteRequest request) {
        log.info("Mise à jour de la collecte: {}", id);

        Collecte collecte = getCollecteById(id);

        if (collecte.getStatut() != StatutCollecte.PLANIFIEE && collecte.getStatut() != StatutCollecte.EN_COURS) {
            throw new IllegalStateException(
                    "Seule une collecte PLANIFIÉE peut être modifiée. Statut actuel: " + collecte.getStatut());
        }

        if (request.getObservations() != null) {
            collecte.setObservations(request.getObservations());
        }

        Collecte saved = collecteRepository.save(collecte);
        return convertToResponse(saved);
    }

    @Override
    public void supprimer(String id) {
        log.info("Suppression de la collecte: {}", id);

        Collecte collecte = getCollecteById(id);

        // Vérifier qu'il n'y a pas de tournées en cours
        boolean hasActiveTournees = collecte.getTournees().stream()
                .anyMatch(t -> t.getStatut() != StatutTournee.ANNULEE);

        if (hasActiveTournees) {
            throw new IllegalStateException(
                    "Impossible de supprimer une collecte qui a des tournées non annulées");
        }

        collecteRepository.delete(collecte);
        log.info("Collecte supprimée avec succès: {}", id);
    }

    @Override
    public CollecteResponse ajouterTournee(String collecteId, String tourneeId) {
        log.info("Ajout de la tournée {} à la collecte {}", tourneeId, collecteId);

        Collecte collecte = getCollecteById(collecteId);
        Tournee tournee = tourneeRepository.findById(tourneeId)
                .orElseThrow(() -> new RuntimeException("Tournée non trouvée"));

        if (collecte.getStatut() != StatutCollecte.PLANIFIEE && collecte.getStatut() != StatutCollecte.EN_COURS) {
            throw new IllegalStateException(
                    "Impossible d'ajouter une tournée à une collecte qui n'est pas PLANIFIEE ou en cours");
        }

        if (collecte.getTournees() == null) {
            collecte.setTournees(new ArrayList<>());
        }

        collecte.getTournees().add(tournee);
        Collecte saved = collecteRepository.save(collecte);

        return convertToResponse(saved);
    }

    @Override
    public void mettreAJourQuantiteTotale(String collecteId, Double quantiteKg) {
        Collecte collecte = getCollecteById(collecteId);

        if (collecte.getQuantiteTotaleKg() == null) {
            collecte.setQuantiteTotaleKg(0.0);
        }

        collecte.setQuantiteTotaleKg(collecte.getQuantiteTotaleKg() + quantiteKg);

        // Calculer le rendement moyen par arbre
        if (!collecte.getTournees().isEmpty()) {
            Verger verger = collecte.getTournees().get(0).getVerger();
            if (verger != null && verger.getNbArbre() > 0) {
                collecte.setRendementMoyenParArbre(
                        collecte.getQuantiteTotaleKg() / verger.getNbArbre()
                );
            }
        }

        collecteRepository.save(collecte);
        log.info("Quantité totale mise à jour pour la collecte {}: {} kg", collecteId, collecte.getQuantiteTotaleKg());
    }

    @Override
    public Double calculerQuantiteTotale(String collecteId) {
        Collecte collecte = getCollecteById(collecteId);

        return collecte.getTournees().stream()
                .filter(t -> t.getStatut() == StatutTournee.TERMINEE)
                .mapToDouble(t -> t.getQuantiteCollecteeKg() != null ? t.getQuantiteCollecteeKg() : 0.0)
                .sum();
    }

    @Override
    public boolean isComplete(String collecteId) {
        Collecte collecte = getCollecteById(collecteId);

        if (collecte.getTournees() == null || collecte.getTournees().isEmpty()) {
            return false;
        }

        return collecte.getTournees().stream()
                .allMatch(t -> t.getStatut() == StatutTournee.TERMINEE);
    }

    @Override
    public String getVergerIdByCollecte(String collecteId) {
        Collecte collecte = getCollecteById(collecteId);

        if (collecte.getTournees() == null || collecte.getTournees().isEmpty()) {
            throw new IllegalStateException("La collecte n'a pas encore de tournées");
        }

        return collecte.getTournees().get(0).getVerger().getId();
    }

    // ─── Private helper methods ─────────────────────────────────────────────

    private String genererCode() {
        // Format: C-YYYYMMDD-XXX
        String date = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        long count = collecteRepository.count() + 1;
        return String.format("C-%s-%03d", date, count);
    }

    private CollecteResponse convertToResponse(Collecte collecte) {
        CollecteResponse response = new CollecteResponse();
        response.setId(collecte.getId());
        response.setCode(collecte.getCode());
        response.setStatut(collecte.getStatut());
        response.setDateDebutCampagne(collecte.getDateDebutCampagne());
        response.setDateFinCampagne(collecte.getDateFinCampagne());
        response.setQuantiteTotaleKg(collecte.getQuantiteTotaleKg());
        response.setRendementMoyenParArbre(collecte.getRendementMoyenParArbre());
        response.setObservations(collecte.getObservations());
        response.setDateCreation(collecte.getDateCreation());

        if (collecte.getTournees() != null) {
            response.setNombreTournees(collecte.getTournees().size());
            response.setNombreTourneesTerminees(
                    (int) collecte.getTournees().stream()
                            .filter(t -> t.getStatut() == StatutTournee.TERMINEE)
                            .count()
            );
        }

        // Récupérer l'ID du verger depuis la première tournée
        if (collecte.getTournees() != null && !collecte.getTournees().isEmpty()) {
            response.setVergerId(collecte.getTournees().get(0).getVerger().getId());
        }

        return response;
    }
}