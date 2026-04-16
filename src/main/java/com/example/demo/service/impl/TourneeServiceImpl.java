package com.example.demo.service.impl;

import com.example.demo.dto.TerminerTourneeRequest;
import com.example.demo.dto.TourneeRequest;
import com.example.demo.dto.TourneeResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.model.enums.StatutVerger;
import com.example.demo.repository.*;
import com.example.demo.service.TourneeService;
import com.example.demo.service.CollecteService;  // ✅ AJOUTÉ
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TourneeServiceImpl implements TourneeService {

    private final TourneeRepository     tourneeRepo;
    private final VergerRepository      vergerRepo;
    private final RessourceRepository   ressourceRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final CollecteRepository    collecteRepo;     // ✅ AJOUTÉ
    private final CollecteService       collecteService;  // ✅ AJOUTÉ

    private static final String NO_EXCLUDE = "000000000000000000000000";

    // ═══════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════

@Override
public TourneeResponse creer(TourneeRequest req) {

    System.out.println("🚀 === DÉBUT CRÉATION TOURNÉE ===");
    
    validateDates(req.getDateDebut(), req.getDateFin());

    // Resolve verger
    System.out.println("🔍 Recherche du verger: " + req.getVergerId());
    Verger verger = vergerRepo.findById(req.getVergerId())
            .orElseThrow(() -> new ResourceNotFoundException("Verger introuvable : " + req.getVergerId()));
    if (Boolean.TRUE.equals(verger.getEstSupprimer()))
        throw new IllegalStateException("Le verger est supprimé.");
    System.out.println("✅ Verger trouvé: " + verger.getId());

    // Resolve and check benne availability
    System.out.println("🔍 Recherche de la benne: " + req.getBenneId());
    Ressource benne = ressourceRepo.findById(req.getBenneId())
            .orElseThrow(() -> new ResourceNotFoundException("Benne introuvable : " + req.getBenneId()));
    if (benne.getType() != TypeRessource.BENNE)
        throw new IllegalArgumentException(req.getBenneId() + " n'est pas une benne.");
    checkBenneDisponible(benne, req.getDateDebut(), req.getDateFin(), NO_EXCLUDE);
    System.out.println("✅ Benne trouvée: " + benne.getId());

    // Resolve and check tracteur availability
    System.out.println("🔍 Recherche du tracteur: " + req.getTracteurId());
    Ressource tracteur = ressourceRepo.findById(req.getTracteurId())
            .orElseThrow(() -> new ResourceNotFoundException("Tracteur introuvable : " + req.getTracteurId()));
    if (tracteur.getType() != TypeRessource.TRACTEUR)
        throw new IllegalArgumentException(req.getTracteurId() + " n'est pas un tracteur.");
    checkTracteurDisponible(tracteur, req.getDateDebut(), req.getDateFin(), NO_EXCLUDE);
    System.out.println("✅ Tracteur trouvé: " + tracteur.getId());

    // Resolve and check each travailleur availability
    if (req.getTravailleurIds() == null || req.getTravailleurIds().isEmpty())
        throw new IllegalArgumentException("Au moins un travailleur doit être assigné.");
    List<Utilisateur> travailleurs = new ArrayList<>();
    System.out.println("🔍 Recherche des travailleurs: " + req.getTravailleurIds());
    for (String tid : req.getTravailleurIds()) {
        Utilisateur t = utilisateurRepo.findById(tid)
                .orElseThrow(() -> new ResourceNotFoundException("Travailleur introuvable : " + tid));
        checkTravailleurDisponible(t, req.getDateDebut(), req.getDateFin(), NO_EXCLUDE);
        travailleurs.add(t);
    }
    System.out.println("✅ " + travailleurs.size() + " travailleur(s) trouvé(s)");

    int nbreArbre = (req.getNbreArbre() != null && req.getNbreArbre() > 0)
            ? req.getNbreArbre() : Tournee.NB_ARBRES_PAR_TOURNEE;

    // ✅ Gestion de la collecte
    System.out.println("\n📋 === GESTION DE LA COLLECTE ===");
    String annee = collecteService.getCampagneAnnee(req.getDateDebut());
    System.out.println("📅 1. Date début: " + req.getDateDebut());
    System.out.println("📅 2. Année campagne calculée: '" + annee + "'");
    System.out.println("📅 3. Verger ID: " + verger.getId());

    Optional<Collecte> existingCollecte = collecteRepo.findByVergerIdAndAnnee(verger.getId(), annee);
    System.out.println("🔍 4. Collecte existante trouvée? " + existingCollecte.isPresent());

    Collecte collecte;
    if (existingCollecte.isPresent()) {
        collecte = existingCollecte.get();
        System.out.println("♻️ 5. Réutilisation collecte existante: ID=" + collecte.getId() + ", Code=" + collecte.getCode());
    } else {
        System.out.println("🆕 5. Création d'une nouvelle collecte...");
        collecte = collecteService.createNewCollecte(verger, annee, req.getDateDebut());
        System.out.println("✅ 6. Nouvelle collecte créée: ID=" + collecte.getId() + ", Code=" + collecte.getCode());
    }

    if (collecte == null) {
        System.err.println("❌ ERREUR CRITIQUE: collecte est null!");
        throw new IllegalStateException("La collecte n'a pas pu être créée");
    }

    System.out.println("\n🏗️ === CONSTRUCTION DE LA TOURNÉE ===");
    Tournee tournee = Tournee.builder()
            .code(genererCode())
            .statut(StatutTournee.PLANIFIEE)
            .verger(verger)
            .collecte(collecte)  // ← Liaison avec la collecte
            .benne(benne)
            .tracteur(tracteur)
            .travailleurs(travailleurs)
            .nbreArbre(nbreArbre)
            .dateDebut(req.getDateDebut())
            .dateFin(req.getDateFin())
            .distanceTotale(req.getDistanceTotale())
            .observations(req.getObservations())
            .collecteFinalisee(false)
            .dateCreation(new Date())
            .build();
    
    System.out.println("✅ Tournée construite avec collecteId=" + (tournee.getCollecte() != null ? tournee.getCollecte().getId() : "null"));

    // Move verger to EN_COURS if still idle
    if (verger.getStatut() == StatutVerger.NON_RECOLTE) {
        verger.setStatut(StatutVerger.EN_COURS);
        vergerRepo.save(verger);
        System.out.println("📊 Verger passé à EN_COURS");
    }

    System.out.println("💾 Sauvegarde de la tournée...");
    Tournee saved = tourneeRepo.save(tournee);
    System.out.println("✅ Tournée sauvegardée: ID=" + saved.getId() + ", Code=" + saved.getCode());
    
    // Mettre à jour les statistiques de la collecte
    System.out.println("📊 Mise à jour des statistiques de la collecte...");
    collecteService.updateCollecteStats(collecte.getId());
    System.out.println("✅ Statistiques mises à jour");
    
    System.out.println("🎉 === FIN CRÉATION TOURNÉE ===\n");
    
    return toResponse(saved);
}
    // ═══════════════════════════════════════════════════════════════
    // READ
    // ═══════════════════════════════════════════════════════════════

    @Override
    public TourneeResponse getById(String id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public Tournee getTourneeById(String id) {
        return findOrThrow(id);
    }

    @Override
    public List<TourneeResponse> getAll() {
        return tourneeRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TourneeResponse> getByVerger(String vergerId) {
        return tourneeRepo.findByVergerId(vergerId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TourneeResponse> getByStatut(StatutTournee statut) {
        return tourneeRepo.findByStatut(statut).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TourneeResponse> getActive() {
        return tourneeRepo.findActive().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // STATE TRANSITIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public TourneeResponse demarrer(String id) {
        Tournee tournee = findOrThrow(id);

        if (tournee.getStatut() != StatutTournee.PLANIFIEE)
            throw new IllegalStateException(
                    "Seule une tournée PLANIFIÉE peut être démarrée. Statut actuel : " + tournee.getStatut());

        // Replace planned dateDebut with the actual start time
        tournee.setStatut(StatutTournee.EN_COURS);
        tournee.setDateDebut(new Date());

        // ✅ Démarrer la collecte si ce n'est pas déjà fait
        if (tournee.getCollecte() != null) {
            Collecte collecte = tournee.getCollecte();
            if (collecte.getStatut() == com.example.demo.model.enums.StatutCollecte.PLANIFIEE) {
                collecteService.demarrerCollecte(collecte.getId());
            }
        }

        return toResponse(tourneeRepo.save(tournee));
    }

    @Override
    public TourneeResponse terminer(String id, TerminerTourneeRequest req) {
        Tournee tournee = findOrThrow(id);

        if (tournee.getStatut() != StatutTournee.EN_COURS)
            throw new IllegalStateException(
                    "Seule une tournée EN_COURS peut être terminée. Statut actuel : " + tournee.getStatut());

        Date now = new Date();
        tournee.setStatut(StatutTournee.TERMINEE);
        tournee.setDateFin(now);
        tournee.setQuantiteCollecteeKg(req.getQuantiteCollecteeKg());
        tournee.setCollecteFinalisee(true);

        if (req.getDistanceTotale() != null) tournee.setDistanceTotale(req.getDistanceTotale());
        if (req.getObservations() != null) tournee.setObservations(req.getObservations());

        // Compute actual duration in minutes
        if (tournee.getDateDebut() != null) {
            long diffMs = now.getTime() - tournee.getDateDebut().getTime();
            tournee.setTempsTotal((int) (diffMs / (1000 * 60)));
        }

        if (tournee.getBenne() != null) {
            // 1. RECHARGER la benne depuis la base de données
            Ressource benne = ressourceRepo.findById(tournee.getBenne().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Benne non trouvée"));
            
            // 2. Maintenant l'objet est ATTACHÉ à Hibernate
            try {
                benne.ajouterCharge(req.getQuantiteCollecteeKg());
            } catch (IllegalArgumentException e) {
                benne.setQuantiteChargeeActuelle(benne.getCapaciteKg());
                benne.setEstPleine(true);
                benne.setTauxRemplissage(100.0);
            }
            
            // 3. Sauvegarde (UPDATE au lieu de INSERT)
            ressourceRepo.save(benne);}
        tourneeRepo.save(tournee);
        
        // ✅ Mettre à jour les statistiques de la collecte
     // ✅ Mettre à jour les statistiques de la collecte
        if (tournee.getCollecte() != null) {
            System.out.println("🔄 Appel de updateCollecteStats pour la collecte: " + tournee.getCollecte().getId());
            collecteService.updateCollecteStats(tournee.getCollecte().getId());
        }
        
        checkAndCloseVerger(tournee.getVerger().getId());

        return toResponse(tournee);
    }

    @Override
    public TourneeResponse annuler(String id) {
        Tournee tournee = findOrThrow(id);

        if (tournee.getStatut() == StatutTournee.TERMINEE)
            throw new IllegalStateException("Une tournée TERMINÉE ne peut pas être annulée.");

        tournee.setStatut(StatutTournee.ANNULEE);
        tournee.setDateFin(new Date());
        tournee.setCollecteFinalisee(false);

        return toResponse(tourneeRepo.save(tournee));
    }

    // ═══════════════════════════════════════════════════════════════
    // UPDATE
    // ═══════════════════════════════════════════════════════════════

    @Override
    public TourneeResponse mettreAJour(String id, TourneeRequest req) {
        Tournee tournee = findOrThrow(id);

        if (tournee.getStatut() != StatutTournee.PLANIFIEE)
            throw new IllegalStateException("Seule une tournée PLANIFIÉE peut être modifiée.");

        Date newDebut = req.getDateDebut() != null ? req.getDateDebut() : tournee.getDateDebut();
        Date newFin   = req.getDateFin()   != null ? req.getDateFin()   : tournee.getDateFin();
        validateDates(newDebut, newFin);

        // Swap benne if changed
        if (!tournee.getBenne().getId().equals(req.getBenneId())) {
            Ressource newBenne = ressourceRepo.findById(req.getBenneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Benne introuvable : " + req.getBenneId()));
            if (newBenne.getType() != TypeRessource.BENNE)
                throw new IllegalArgumentException(req.getBenneId() + " n'est pas une benne.");
            checkBenneDisponible(newBenne, newDebut, newFin, id);
            tournee.setBenne(newBenne);
        } else {
            checkBenneDisponible(tournee.getBenne(), newDebut, newFin, id);
        }

        // Swap tracteur if changed
        if (!tournee.getTracteur().getId().equals(req.getTracteurId())) {
            Ressource newTracteur = ressourceRepo.findById(req.getTracteurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tracteur introuvable : " + req.getTracteurId()));
            if (newTracteur.getType() != TypeRessource.TRACTEUR)
                throw new IllegalArgumentException(req.getTracteurId() + " n'est pas un tracteur.");
            checkTracteurDisponible(newTracteur, newDebut, newFin, id);
            tournee.setTracteur(newTracteur);
        } else {
            checkTracteurDisponible(tournee.getTracteur(), newDebut, newFin, id);
        }

        // Update travailleurs if provided
        if (req.getTravailleurIds() != null && !req.getTravailleurIds().isEmpty()) {
            List<Utilisateur> travailleurs = new ArrayList<>();
            for (String tid : req.getTravailleurIds()) {
                Utilisateur t = utilisateurRepo.findById(tid)
                        .orElseThrow(() -> new ResourceNotFoundException("Travailleur introuvable : " + tid));
                checkTravailleurDisponible(t, newDebut, newFin, id);
                travailleurs.add(t);
            }
            tournee.setTravailleurs(travailleurs);
        } else {
            for (Utilisateur t : tournee.getTravailleurs()) {
                checkTravailleurDisponible(t, newDebut, newFin, id);
            }
        }

        tournee.setDateDebut(newDebut);
        tournee.setDateFin(newFin);
        if (req.getNbreArbre()      != null && req.getNbreArbre() > 0) tournee.setNbreArbre(req.getNbreArbre());
        if (req.getDistanceTotale() != null) tournee.setDistanceTotale(req.getDistanceTotale());
        if (req.getObservations()   != null) tournee.setObservations(req.getObservations());

        return toResponse(tourneeRepo.save(tournee));
    }

    // ═══════════════════════════════════════════════════════════════
    // DELETE
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void supprimer(String id) {
        Tournee tournee = findOrThrow(id);
        if (tournee.getStatut() == StatutTournee.EN_COURS
                || tournee.getStatut() == StatutTournee.TERMINEE)
            throw new IllegalStateException("Seules les tournées PLANIFIÉE ou ANNULÉE peuvent être supprimées.");
        tourneeRepo.delete(tournee);
    }

    // ═══════════════════════════════════════════════════════════════
    // AGGREGATES
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Double getTotalCollecteParVerger(String vergerId) {
        return tourneeRepo.findTermineesByVergerId(vergerId).stream()
                .mapToDouble(t -> t.getQuantiteCollecteeKg() != null ? t.getQuantiteCollecteeKg() : 0.0)
                .sum();
    }

    @Override
    public int calculerNbTourneesNecessaires(String vergerId) {
        Verger verger = vergerRepo.findById(vergerId)
                .orElseThrow(() -> new ResourceNotFoundException("Verger introuvable : " + vergerId));
        return (int) Math.ceil((double) verger.getNbArbre() / Tournee.NB_ARBRES_PAR_TOURNEE);
    }

    // ═══════════════════════════════════════════════════════════════
    // AVAILABILITY CHECKS
    // ═══════════════════════════════════════════════════════════════

    private void checkBenneDisponible(Ressource benne, Date debut, Date fin, String excludeId) {
        List<Tournee> conflicts = tourneeRepo.findConflictsByBenne(benne.getId(), debut, fin, excludeId);
        if (!conflicts.isEmpty()) {
            Tournee c = conflicts.get(0);
            throw new IllegalStateException(
                    "La benne « " + benne.getNom() + " » est déjà utilisée du "
                            + fmt(c.getDateDebut()) + " au " + fmt(c.getDateFin())
                            + " (tournée " + c.getCode() + ")."
            );
        }
    }

    private void checkTracteurDisponible(Ressource tracteur, Date debut, Date fin, String excludeId) {
        List<Tournee> conflicts = tourneeRepo.findConflictsByTracteur(tracteur.getId(), debut, fin, excludeId);
        if (!conflicts.isEmpty()) {
            Tournee c = conflicts.get(0);
            throw new IllegalStateException(
                    "Le tracteur « " + tracteur.getNom() + " » est déjà utilisé du "
                            + fmt(c.getDateDebut()) + " au " + fmt(c.getDateFin())
                            + " (tournée " + c.getCode() + ")."
            );
        }
    }

    private void checkTravailleurDisponible(Utilisateur travailleur, Date debut, Date fin, String excludeId) {
        List<Tournee> conflicts = tourneeRepo.findConflictsByTravailleur(travailleur.getId(), debut, fin, excludeId);
        if (!conflicts.isEmpty()) {
            Tournee c = conflicts.get(0);
            throw new IllegalStateException(
                    "Le travailleur « " + travailleur.getPrenom() + " " + travailleur.getNom()
                            + " » est déjà assigné du " + fmt(c.getDateDebut()) + " au " + fmt(c.getDateFin())
                            + " (tournée " + c.getCode() + ")."
            );
        }
    }

    private void validateDates(Date debut, Date fin) {
        if (debut == null || fin == null)
            throw new IllegalArgumentException("La date de début et la date de fin sont obligatoires.");
        if (!fin.after(debut))
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
    }

    private String fmt(Date d) {
        return d == null ? "?" : new SimpleDateFormat("dd/MM/yyyy HH:mm").format(d);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════

    private Tournee findOrThrow(String id) {
        return tourneeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournée introuvable : " + id));
    }

    private String genererCode() {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return "T-" + date + "-" + String.format("%03d", tourneeRepo.count() + 1);
    }

    private void checkAndCloseVerger(String vergerId) {
        Verger verger = vergerRepo.findById(vergerId).orElse(null);
        if (verger == null) return;
        int arbresCouverts = tourneeRepo.findTermineesByVergerId(vergerId).stream()
                .mapToInt(t -> t.getNbreArbre() != null ? t.getNbreArbre() : 0)
                .sum();
        if (arbresCouverts >= verger.getNbArbre()) {
            verger.setStatut(StatutVerger.RECOLTE);
            verger.setDateDerniereRecolte(new Date());
            vergerRepo.save(verger);
        }
    }

    private double calculerEfficacite(Tournee t) {
        if (t.getTempsTotal()          == null || t.getTempsTotal()          == 0) return 0.0;
        if (t.getDistanceTotale()      == null || t.getDistanceTotale()      == 0) return 0.0;
        if (t.getQuantiteCollecteeKg() == null || t.getQuantiteCollecteeKg() == 0) return 0.0;
        double heures = t.getTempsTotal() / 60.0;
        return Math.min((t.getQuantiteCollecteeKg() / (t.getDistanceTotale() * heures)) * 10.0, 100.0);
    }

    private TourneeResponse toResponse(Tournee t) {
        Verger v = t.getVerger();
        String vergerTypeOlive = null, vergerAgriculteurNom = null;
        Double vergerSuperficie = null;
        if (v != null) {
            vergerTypeOlive  = v.getTypeOlive();
            vergerSuperficie = v.getSuperficie();
            if (v.getAgriculteur() != null)
                vergerAgriculteurNom = v.getAgriculteur().getPrenom() + " " + v.getAgriculteur().getNom();
        }

        Ressource benne   = t.getBenne();
        Ressource tracteur = t.getTracteur();

        List<String> travailleurIds  = new ArrayList<>();
        List<String> travailleurNoms = new ArrayList<>();
        if (t.getTravailleurs() != null)
            for (Utilisateur u : t.getTravailleurs()) {
                travailleurIds.add(u.getId());
                travailleurNoms.add(u.getPrenom() + " " + u.getNom());
            }

        Double totalVerger = (v != null) ? getTotalCollecteParVerger(v.getId()) : null;

        return TourneeResponse.builder()
                .id(t.getId())
                .code(t.getCode())
                .statut(t.getStatut())
                .vergerId(v       != null ? v.getId()       : null)
                .vergerTypeOlive(vergerTypeOlive)
                .vergerAgriculteurNom(vergerAgriculteurNom)
                .vergerSuperficie(vergerSuperficie)
                .benneId(benne    != null ? benne.getId()   : null)
                .benneNom(benne   != null ? benne.getNom()  : null)
                .benneCapaciteKg(benne != null ? benne.getCapaciteKg() : null)
                .tracteurId(tracteur  != null ? tracteur.getId()           : null)
                .tracteurNom(tracteur != null ? tracteur.getNom()          : null)
                .tracteurImmatriculation(tracteur != null ? tracteur.getImmatriculation() : null)
                .travailleurIds(travailleurIds)
                .travailleurNoms(travailleurNoms)
                .nbreArbre(t.getNbreArbre())
                .distanceTotale(t.getDistanceTotale())
                .tempsTotal(t.getTempsTotal())
                .quantiteCollecteeKg(t.getQuantiteCollecteeKg())
                .collecteFinalisee(t.getCollecteFinalisee())
                .efficacite(calculerEfficacite(t))
                .observations(t.getObservations())
                .dateDebut(t.getDateDebut())
                .dateFin(t.getDateFin())
                .dateCreation(t.getDateCreation())
                .totalCollecteVergerKg(totalVerger)
                .collecteId(t.getCollecte() != null ? t.getCollecte().getId() : null)  // ✅ AJOUTÉ
                .collecteCode(t.getCollecte() != null ? t.getCollecte().getCode() : null)  // ✅ AJOUTÉ
                .build();
    }
}