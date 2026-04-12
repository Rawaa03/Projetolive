package com.example.demo.service.impl;

import com.example.demo.dto.TerminerTourneeRequest;
import com.example.demo.dto.TourneeRequest;
import com.example.demo.dto.TourneeResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.model.enums.StatutVerger;
import com.example.demo.repository.*;
import com.example.demo.service.TourneeService;
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

    // ═══════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════

    @Override
    public TourneeResponse creer(TourneeRequest req) {

        // Resolve verger
        Verger verger = vergerRepo.findById(req.getVergerId())
                .orElseThrow(() -> new ResourceNotFoundException("Verger introuvable : " + req.getVergerId()));
        if (Boolean.TRUE.equals(verger.getEstSupprimer()))
            throw new IllegalStateException("Le verger est supprimé.");

        // Resolve benne (as full object)
        Ressource benne = ressourceRepo.findById(req.getBenneId())
                .orElseThrow(() -> new ResourceNotFoundException("Benne introuvable : " + req.getBenneId()));
        if (benne.getType() != TypeRessource.BENNE)
            throw new IllegalArgumentException(req.getBenneId() + " n'est pas une benne.");
        if (!"DISPONIBLE".equals(benne.getStatut()))
            throw new IllegalStateException("La benne " + benne.getNom() + " n'est pas disponible.");

        // Resolve tracteur (as full object)
        Ressource tracteur = ressourceRepo.findById(req.getTracteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracteur introuvable : " + req.getTracteurId()));
        if (tracteur.getType() != TypeRessource.TRACTEUR)
            throw new IllegalArgumentException(req.getTracteurId() + " n'est pas un tracteur.");
        if (!"DISPONIBLE".equals(tracteur.getStatut()))
            throw new IllegalStateException("Le tracteur " + tracteur.getNom() + " n'est pas disponible.");

        // Resolve travailleurs (as full objects)
        if (req.getTravailleurIds() == null || req.getTravailleurIds().isEmpty())
            throw new IllegalArgumentException("Au moins un travailleur doit être assigné.");
        List<Utilisateur> travailleurs = new ArrayList<>();
        for (String tid : req.getTravailleurIds()) {
            Utilisateur t = utilisateurRepo.findById(tid)
                    .orElseThrow(() -> new ResourceNotFoundException("Travailleur introuvable : " + tid));
            travailleurs.add(t);
        }

        int nbreArbre = (req.getNbreArbre() != null && req.getNbreArbre() > 0)
                ? req.getNbreArbre() : Tournee.NB_ARBRES_PAR_TOURNEE;

        Tournee tournee = Tournee.builder()
                .code(genererCode())
                .statut(StatutTournee.PLANIFIEE)
                .verger(verger)
                .benne(benne)
                .tracteur(tracteur)
                .travailleurs(travailleurs)
                .nbreArbre(nbreArbre)
                .distanceTotale(req.getDistanceTotale())
                .observations(req.getObservations())
                .dateDebut(req.getDateDebut())
                .collecteFinalisee(false)
                .dateCreation(new Date())
                .build();

        // Lock resources
        benne.setStatut("OCCUPE");
        tracteur.setStatut("OCCUPE");
        ressourceRepo.save(benne);
        ressourceRepo.save(tracteur);

        // Move verger to EN_COURS if still idle
        if (verger.getStatut() == StatutVerger.NON_RECOLTE) {
            verger.setStatut(StatutVerger.EN_COURS);
            vergerRepo.save(verger);
        }

        return toResponse(tourneeRepo.save(tournee));
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

        tournee.setStatut(StatutTournee.EN_COURS);
        tournee.setDateDebut(new Date());

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
        if (req.getObservations()   != null) tournee.setObservations(req.getObservations());

        // Compute duration in minutes
        if (tournee.getDateDebut() != null) {
            long diffMs = now.getTime() - tournee.getDateDebut().getTime();
            tournee.setTempsTotal((int) (diffMs / (1000 * 60)));
        }

        // Update benne charge then free it
        Ressource benne = tournee.getBenne();
        if (benne != null) {
            try {
                benne.ajouterCharge(req.getQuantiteCollecteeKg());
            } catch (IllegalArgumentException e) {
                benne.setQuantiteChargeeActuelle(benne.getCapaciteKg());
                benne.setEstPleine(true);
                benne.setTauxRemplissage(100.0);
            }
            benne.setStatut("DISPONIBLE");
            ressourceRepo.save(benne);
        }

        // Free tracteur
        Ressource tracteur = tournee.getTracteur();
        if (tracteur != null) {
            tracteur.setStatut("DISPONIBLE");
            ressourceRepo.save(tracteur);
        }

        tourneeRepo.save(tournee);
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

        freeResources(tournee);

        return toResponse(tourneeRepo.save(tournee));
    }

    // ═══════════════════════════════════════════════════════════════
    // UPDATE / DELETE
    // ═══════════════════════════════════════════════════════════════

    @Override
    public TourneeResponse mettreAJour(String id, TourneeRequest req) {
        Tournee tournee = findOrThrow(id);

        if (tournee.getStatut() != StatutTournee.PLANIFIEE)
            throw new IllegalStateException("Seule une tournée PLANIFIÉE peut être modifiée.");

        // Swap benne if changed
        if (!tournee.getBenne().getId().equals(req.getBenneId())) {
            tournee.getBenne().setStatut("DISPONIBLE");
            ressourceRepo.save(tournee.getBenne());

            Ressource newBenne = ressourceRepo.findById(req.getBenneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Benne introuvable : " + req.getBenneId()));
            if (newBenne.getType() != TypeRessource.BENNE)
                throw new IllegalArgumentException(req.getBenneId() + " n'est pas une benne.");
            if (!"DISPONIBLE".equals(newBenne.getStatut()))
                throw new IllegalStateException("Benne " + newBenne.getNom() + " non disponible.");
            newBenne.setStatut("OCCUPE");
            ressourceRepo.save(newBenne);
            tournee.setBenne(newBenne);
        }

        // Swap tracteur if changed
        if (!tournee.getTracteur().getId().equals(req.getTracteurId())) {
            tournee.getTracteur().setStatut("DISPONIBLE");
            ressourceRepo.save(tournee.getTracteur());

            Ressource newTracteur = ressourceRepo.findById(req.getTracteurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tracteur introuvable : " + req.getTracteurId()));
            if (newTracteur.getType() != TypeRessource.TRACTEUR)
                throw new IllegalArgumentException(req.getTracteurId() + " n'est pas un tracteur.");
            if (!"DISPONIBLE".equals(newTracteur.getStatut()))
                throw new IllegalStateException("Tracteur " + newTracteur.getNom() + " non disponible.");
            newTracteur.setStatut("OCCUPE");
            ressourceRepo.save(newTracteur);
            tournee.setTracteur(newTracteur);
        }

        // Update travailleurs if provided
        if (req.getTravailleurIds() != null && !req.getTravailleurIds().isEmpty()) {
            List<Utilisateur> travailleurs = new ArrayList<>();
            for (String tid : req.getTravailleurIds()) {
                Utilisateur t = utilisateurRepo.findById(tid)
                        .orElseThrow(() -> new ResourceNotFoundException("Travailleur introuvable : " + tid));
                travailleurs.add(t);
            }
            tournee.setTravailleurs(travailleurs);
        }

        if (req.getNbreArbre()      != null && req.getNbreArbre() > 0) tournee.setNbreArbre(req.getNbreArbre());
        if (req.getDistanceTotale() != null) tournee.setDistanceTotale(req.getDistanceTotale());
        if (req.getObservations()   != null) tournee.setObservations(req.getObservations());
        if (req.getDateDebut()      != null) tournee.setDateDebut(req.getDateDebut());

        return toResponse(tourneeRepo.save(tournee));
    }

    @Override
    public void supprimer(String id) {
        Tournee tournee = findOrThrow(id);
        if (tournee.getStatut() == StatutTournee.EN_COURS
                || tournee.getStatut() == StatutTournee.TERMINEE)
            throw new IllegalStateException("Seules les tournées PLANIFIÉE ou ANNULÉE peuvent être supprimées.");
        freeResources(tournee);
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

    private void freeResources(Tournee tournee) {
        if (tournee.getBenne() != null && "OCCUPE".equals(tournee.getBenne().getStatut())) {
            tournee.getBenne().setStatut("DISPONIBLE");
            ressourceRepo.save(tournee.getBenne());
        }
        if (tournee.getTracteur() != null && "OCCUPE".equals(tournee.getTracteur().getStatut())) {
            tournee.getTracteur().setStatut("DISPONIBLE");
            ressourceRepo.save(tournee.getTracteur());
        }
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

        Ressource benne = t.getBenne();
        String benneNom = benne != null ? benne.getNom() : null;
        Double benneCapaciteKg = benne != null ? benne.getCapaciteKg() : null;
        String benneId = benne != null ? benne.getId() : null;

        Ressource tracteur = t.getTracteur();
        String tracteurNom   = tracteur != null ? tracteur.getNom()            : null;
        String tracteurImmat = tracteur != null ? tracteur.getImmatriculation() : null;
        String tracteurId    = tracteur != null ? tracteur.getId()              : null;

        List<String> travailleurIds  = new ArrayList<>();
        List<String> travailleurNoms = new ArrayList<>();
        if (t.getTravailleurs() != null) {
            for (Utilisateur u : t.getTravailleurs()) {
                travailleurIds.add(u.getId());
                travailleurNoms.add(u.getPrenom() + " " + u.getNom());
            }
        }

        Double totalVerger = (v != null) ? getTotalCollecteParVerger(v.getId()) : null;

        return TourneeResponse.builder()
                .id(t.getId())
                .code(t.getCode())
                .statut(t.getStatut())
                .vergerId(v != null ? v.getId() : null)
                .vergerTypeOlive(vergerTypeOlive)
                .vergerAgriculteurNom(vergerAgriculteurNom)
                .vergerSuperficie(vergerSuperficie)
                .benneId(benneId)
                .benneNom(benneNom)
                .benneCapaciteKg(benneCapaciteKg)
                .tracteurId(tracteurId)
                .tracteurNom(tracteurNom)
                .tracteurImmatriculation(tracteurImmat)
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
                .build();
    }
}