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

    private final TourneeRepository    tourneeRepo;
    private final VergerRepository     vergerRepo;
    private final RessourceRepository  ressourceRepo;
    private final UtilisateurRepository utilisateurRepo;

    // ═══════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════

    @Override
    public TourneeResponse creer(TourneeRequest req) {

        Verger verger = vergerRepo.findById(req.getVergerId())
                .orElseThrow(() -> new ResourceNotFoundException("Verger introuvable : " + req.getVergerId()));
        if (Boolean.TRUE.equals(verger.getEstSupprimer()))
            throw new IllegalStateException("Le verger est supprimé.");

        Ressource benne = ressourceRepo.findById(req.getBenneId())
                .orElseThrow(() -> new ResourceNotFoundException("Benne introuvable : " + req.getBenneId()));
        if (benne.getType() != TypeRessource.BENNE)
            throw new IllegalArgumentException(req.getBenneId() + " n'est pas une benne.");
        if (!"DISPONIBLE".equals(benne.getStatut()))
            throw new IllegalStateException("La benne " + benne.getNom() + " n'est pas disponible.");

        Ressource tracteur = ressourceRepo.findById(req.getTracteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracteur introuvable : " + req.getTracteurId()));
        if (tracteur.getType() != TypeRessource.TRACTEUR)
            throw new IllegalArgumentException(req.getTracteurId() + " n'est pas un tracteur.");
        if (!"DISPONIBLE".equals(tracteur.getStatut()))
            throw new IllegalStateException("Le tracteur " + tracteur.getNom() + " n'est pas disponible.");

        if (req.getTravailleurIds() == null || req.getTravailleurIds().isEmpty())
            throw new IllegalArgumentException("Au moins un travailleur doit être assigné.");
        for (String tid : req.getTravailleurIds())
            utilisateurRepo.findById(tid)
                    .orElseThrow(() -> new ResourceNotFoundException("Travailleur introuvable : " + tid));

        int nbreArbre = (req.getNbreArbre() != null && req.getNbreArbre() > 0)
                ? req.getNbreArbre() : Tournee.NB_ARBRES_PAR_TOURNEE;

        Tournee tournee = Tournee.builder()
                .code(genererCode())
                .statut(StatutTournee.PLANIFIEE)
                .verger(verger)
                .benneId(req.getBenneId())
                .tracteurId(req.getTracteurId())
                .travailleurIds(new ArrayList<>(req.getTravailleurIds()))
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

        // Move verger to EN_COURS if it was idle
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
    // STATE TRANSITIONS  (logic lives here, not in the model)
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

        // Add collected kg to benne, then free it
        Ressource benne = ressourceRepo.findById(tournee.getBenneId()).orElse(null);
        if (benne != null) {
            try {
                benne.ajouterCharge(req.getQuantiteCollecteeKg());
            } catch (IllegalArgumentException e) {
                // benne overflow — cap at full
                benne.setQuantiteChargeeActuelle(benne.getCapaciteKg());
                benne.setEstPleine(true);
                benne.setTauxRemplissage(100.0);
            }
            benne.setStatut("DISPONIBLE");
            ressourceRepo.save(benne);
        }

        // Free tracteur
        Ressource tracteur = ressourceRepo.findById(tournee.getTracteurId()).orElse(null);
        if (tracteur != null) {
            tracteur.setStatut("DISPONIBLE");
            ressourceRepo.save(tracteur);
        }

        tourneeRepo.save(tournee);

        // Auto-close verger if all trees are covered
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
        if (!tournee.getBenneId().equals(req.getBenneId())) {
            Ressource old = ressourceRepo.findById(tournee.getBenneId()).orElse(null);
            if (old != null) { old.setStatut("DISPONIBLE"); ressourceRepo.save(old); }

            Ressource nb = ressourceRepo.findById(req.getBenneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Benne introuvable : " + req.getBenneId()));
            if (nb.getType() != TypeRessource.BENNE)
                throw new IllegalArgumentException(req.getBenneId() + " n'est pas une benne.");
            if (!"DISPONIBLE".equals(nb.getStatut()))
                throw new IllegalStateException("Benne " + nb.getNom() + " non disponible.");
            nb.setStatut("OCCUPE");
            ressourceRepo.save(nb);
            tournee.setBenneId(req.getBenneId());
        }

        // Swap tracteur if changed
        if (!tournee.getTracteurId().equals(req.getTracteurId())) {
            Ressource old = ressourceRepo.findById(tournee.getTracteurId()).orElse(null);
            if (old != null) { old.setStatut("DISPONIBLE"); ressourceRepo.save(old); }

            Ressource nt = ressourceRepo.findById(req.getTracteurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tracteur introuvable : " + req.getTracteurId()));
            if (nt.getType() != TypeRessource.TRACTEUR)
                throw new IllegalArgumentException(req.getTracteurId() + " n'est pas un tracteur.");
            if (!"DISPONIBLE".equals(nt.getStatut()))
                throw new IllegalStateException("Tracteur " + nt.getNom() + " non disponible.");
            nt.setStatut("OCCUPE");
            ressourceRepo.save(nt);
            tournee.setTracteurId(req.getTracteurId());
        }

        if (req.getTravailleurIds() != null && !req.getTravailleurIds().isEmpty())
            tournee.setTravailleurIds(new ArrayList<>(req.getTravailleurIds()));
        if (req.getNbreArbre()     != null && req.getNbreArbre() > 0)
            tournee.setNbreArbre(req.getNbreArbre());
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
        Ressource benne = ressourceRepo.findById(tournee.getBenneId()).orElse(null);
        if (benne != null && "OCCUPE".equals(benne.getStatut())) {
            benne.setStatut("DISPONIBLE");
            ressourceRepo.save(benne);
        }
        Ressource tracteur = ressourceRepo.findById(tournee.getTracteurId()).orElse(null);
        if (tracteur != null && "OCCUPE".equals(tracteur.getStatut())) {
            tracteur.setStatut("DISPONIBLE");
            ressourceRepo.save(tracteur);
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

    // ── efficacite computed here, not in the model ────────────────

    private double calculerEfficacite(Tournee t) {
        if (t.getTempsTotal()          == null || t.getTempsTotal()          == 0) return 0.0;
        if (t.getDistanceTotale()      == null || t.getDistanceTotale()      == 0) return 0.0;
        if (t.getQuantiteCollecteeKg() == null || t.getQuantiteCollecteeKg() == 0) return 0.0;
        double heures = t.getTempsTotal() / 60.0;
        return Math.min((t.getQuantiteCollecteeKg() / (t.getDistanceTotale() * heures)) * 10.0, 100.0);
    }

    // ── Response builder ──────────────────────────────────────────

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

        String benneNom = null; Double benneCapaciteKg = null;
        if (t.getBenneId() != null) {
            Ressource b = ressourceRepo.findById(t.getBenneId()).orElse(null);
            if (b != null) { benneNom = b.getNom(); benneCapaciteKg = b.getCapaciteKg(); }
        }

        String tracteurNom = null, tracteurImmat = null;
        if (t.getTracteurId() != null) {
            Ressource tr = ressourceRepo.findById(t.getTracteurId()).orElse(null);
            if (tr != null) { tracteurNom = tr.getNom(); tracteurImmat = tr.getImmatriculation(); }
        }

        List<String> travailleurNoms = new ArrayList<>();
        if (t.getTravailleurIds() != null)
            for (String tid : t.getTravailleurIds())
                utilisateurRepo.findById(tid).ifPresent(u ->
                        travailleurNoms.add(u.getPrenom() + " " + u.getNom()));

        Double totalVerger = (v != null) ? getTotalCollecteParVerger(v.getId()) : null;

        return TourneeResponse.builder()
                .id(t.getId())
                .code(t.getCode())
                .statut(t.getStatut())
                .vergerId(v != null ? v.getId() : null)
                .vergerTypeOlive(vergerTypeOlive)
                .vergerAgriculteurNom(vergerAgriculteurNom)
                .vergerSuperficie(vergerSuperficie)
                .benneId(t.getBenneId())
                .benneNom(benneNom)
                .benneCapaciteKg(benneCapaciteKg)
                .tracteurId(t.getTracteurId())
                .tracteurNom(tracteurNom)
                .tracteurImmatriculation(tracteurImmat)
                .travailleurIds(t.getTravailleurIds())
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