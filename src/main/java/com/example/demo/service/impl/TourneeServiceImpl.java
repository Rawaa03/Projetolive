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

    private final TourneeRepository tourneeRepo;
    private final VergerRepository vergerRepo;
    private final RessourceRepository ressourceRepo;
    private final UtilisateurRepository utilisateurRepo;

    // ═══════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════

    @Override
    public TourneeResponse creer(TourneeRequest req) {

        Verger verger = vergerRepo.findById(req.getVergerId())
                .orElseThrow(() -> new ResourceNotFoundException("Verger introuvable : " + req.getVergerId()));

        if (Boolean.TRUE.equals(verger.getEstSupprimer())) {
            throw new IllegalStateException("Le verger est supprimé.");
        }

        Ressource benne = ressourceRepo.findById(req.getBenneId())
                .orElseThrow(() -> new ResourceNotFoundException("Benne introuvable : " + req.getBenneId()));
        if (benne.getType() != TypeRessource.BENNE) {
            throw new IllegalArgumentException("La ressource " + req.getBenneId() + " n'est pas une benne.");
        }
        if (!"DISPONIBLE".equals(benne.getStatut())) {
            throw new IllegalStateException("La benne " + benne.getNom() + " n'est pas disponible (statut : " + benne.getStatut() + ").");
        }

        Ressource tracteur = ressourceRepo.findById(req.getTracteurId())
                .orElseThrow(() -> new ResourceNotFoundException("Tracteur introuvable : " + req.getTracteurId()));
        if (tracteur.getType() != TypeRessource.TRACTEUR) {
            throw new IllegalArgumentException("La ressource " + req.getTracteurId() + " n'est pas un tracteur.");
        }
        if (!"DISPONIBLE".equals(tracteur.getStatut())) {
            throw new IllegalStateException("Le tracteur " + tracteur.getNom() + " n'est pas disponible.");
        }

        if (req.getTravailleurIds() == null || req.getTravailleurIds().isEmpty()) {
            throw new IllegalArgumentException("Au moins un travailleur doit être assigné.");
        }
        for (String tid : req.getTravailleurIds()) {
            utilisateurRepo.findById(tid)
                    .orElseThrow(() -> new ResourceNotFoundException("Travailleur introuvable : " + tid));
        }

        String code = genererCode();

        int nbreArbre = (req.getNbreArbre() != null && req.getNbreArbre() > 0)
                ? req.getNbreArbre()
                : Tournee.NB_ARBRES_PAR_TOURNEE;

        Tournee tournee = Tournee.builder()
                .code(code)
                .statut(StatutTournee.PLANIFIEE)
                .verger(verger)
                .benneId(req.getBenneId())
                .tracteurId(req.getTracteurId())
                .travailleurIds(new ArrayList<>(req.getTravailleurIds()))
                .nbreArbre(nbreArbre)
                .distanceTotale(req.getDistanceTotale())
                .observations(req.getObservations())
                .dateCreation(new Date())
                .collecteFinalisee(false)
                .build();

        if (req.getDateDebut() != null) {
            tournee.setDateDebut(req.getDateDebut());
        }

        benne.setStatut("OCCUPE");
        tracteur.setStatut("OCCUPE");
        ressourceRepo.save(benne);
        ressourceRepo.save(tracteur);

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
        tournee.demarrer();
        return toResponse(tourneeRepo.save(tournee));
    }

    @Override
    public TourneeResponse terminer(String id, TerminerTourneeRequest req) {
        Tournee tournee = findOrThrow(id);

        if (req.getDistanceTotale() != null) {
            tournee.setDistanceTotale(req.getDistanceTotale());
        }
        if (req.getObservations() != null) {
            tournee.setObservations(req.getObservations());
        }

        tournee.terminer(req.getQuantiteCollecteeKg());

        // Update benne charge then free it
        Ressource benne = ressourceRepo.findById(tournee.getBenneId()).orElse(null);
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
        Ressource tracteur = ressourceRepo.findById(tournee.getTracteurId()).orElse(null);
        if (tracteur != null) {
            tracteur.setStatut("DISPONIBLE");
            ressourceRepo.save(tracteur);
        }

        // Check if verger is fully harvested
        checkAndCloseVerger(tournee.getVerger().getId());

        return toResponse(tourneeRepo.save(tournee));
    }

    @Override
    public TourneeResponse annuler(String id) {
        Tournee tournee = findOrThrow(id);
        tournee.annuler();
        freeResources(tournee);
        return toResponse(tourneeRepo.save(tournee));
    }

    // ═══════════════════════════════════════════════════════════════
    // UPDATE / DELETE
    // ═══════════════════════════════════════════════════════════════

    @Override
    public TourneeResponse mettreAJour(String id, TourneeRequest req) {
        Tournee tournee = findOrThrow(id);

        if (tournee.getStatut() != StatutTournee.PLANIFIEE) {
            throw new IllegalStateException("Seule une tournée PLANIFIÉE peut être modifiée.");
        }

        if (!tournee.getBenneId().equals(req.getBenneId())) {
            Ressource oldBenne = ressourceRepo.findById(tournee.getBenneId()).orElse(null);
            if (oldBenne != null) { oldBenne.setStatut("DISPONIBLE"); ressourceRepo.save(oldBenne); }

            Ressource newBenne = ressourceRepo.findById(req.getBenneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Benne introuvable : " + req.getBenneId()));
            if (newBenne.getType() != TypeRessource.BENNE)
                throw new IllegalArgumentException("Ressource " + req.getBenneId() + " n'est pas une benne.");
            if (!"DISPONIBLE".equals(newBenne.getStatut()))
                throw new IllegalStateException("Benne " + newBenne.getNom() + " non disponible.");
            newBenne.setStatut("OCCUPE");
            ressourceRepo.save(newBenne);
            tournee.setBenneId(req.getBenneId());
        }

        if (!tournee.getTracteurId().equals(req.getTracteurId())) {
            Ressource oldT = ressourceRepo.findById(tournee.getTracteurId()).orElse(null);
            if (oldT != null) { oldT.setStatut("DISPONIBLE"); ressourceRepo.save(oldT); }

            Ressource newT = ressourceRepo.findById(req.getTracteurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tracteur introuvable : " + req.getTracteurId()));
            if (newT.getType() != TypeRessource.TRACTEUR)
                throw new IllegalArgumentException("Ressource " + req.getTracteurId() + " n'est pas un tracteur.");
            if (!"DISPONIBLE".equals(newT.getStatut()))
                throw new IllegalStateException("Tracteur " + newT.getNom() + " non disponible.");
            newT.setStatut("OCCUPE");
            ressourceRepo.save(newT);
            tournee.setTracteurId(req.getTracteurId());
        }

        if (req.getTravailleurIds() != null && !req.getTravailleurIds().isEmpty()) {
            tournee.setTravailleurIds(new ArrayList<>(req.getTravailleurIds()));
        }
        if (req.getNbreArbre() != null && req.getNbreArbre() > 0) {
            tournee.setNbreArbre(req.getNbreArbre());
        }
        if (req.getDistanceTotale() != null) {
            tournee.setDistanceTotale(req.getDistanceTotale());
        }
        if (req.getObservations() != null) {
            tournee.setObservations(req.getObservations());
        }
        if (req.getDateDebut() != null) {
            tournee.setDateDebut(req.getDateDebut());
        }

        return toResponse(tourneeRepo.save(tournee));
    }

    @Override
    public void supprimer(String id) {
        Tournee tournee = findOrThrow(id);
        if (tournee.getStatut() == StatutTournee.EN_COURS || tournee.getStatut() == StatutTournee.TERMINEE) {
            throw new IllegalStateException("Seules les tournées PLANIFIÉE ou ANNULÉE peuvent être supprimées.");
        }
        freeResources(tournee);
        tourneeRepo.delete(tournee);
    }

    // ═══════════════════════════════════════════════════════════════
    // AGGREGATE
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Double getTotalCollecteParVerger(String vergerId) {
        List<Tournee> terminated = tourneeRepo.findTermineesByVergerId(vergerId);
        return terminated.stream()
                .mapToDouble(t -> t.getQuantiteCollecteeKg() != null ? t.getQuantiteCollecteeKg() : 0.0)
                .sum();
    }

    @Override
    public int calculerNbTourneesNecessaires(String vergerId) {
        Verger verger = vergerRepo.findById(vergerId)
                .orElseThrow(() -> new ResourceNotFoundException("Verger introuvable : " + vergerId));
        int total = verger.getNbArbre();
        return (int) Math.ceil((double) total / Tournee.NB_ARBRES_PAR_TOURNEE);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════

    private Tournee findOrThrow(String id) {
        return tourneeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournée introuvable : " + id));
    }

    private String genererCode() {
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String base = "T-" + datePart + "-";
        long count = tourneeRepo.count() + 1;
        return base + String.format("%03d", count);
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

        List<Tournee> terminees = tourneeRepo.findTermineesByVergerId(vergerId);
        int arbresCouverts = terminees.stream()
                .mapToInt(t -> t.getNbreArbre() != null ? t.getNbreArbre() : 0)
                .sum();

        if (arbresCouverts >= verger.getNbArbre()) {
            verger.setStatut(StatutVerger.RECOLTE);
            verger.setDateDerniereRecolte(new Date());
            vergerRepo.save(verger);
        }
    }

    private TourneeResponse toResponse(Tournee t) {
        Verger v = t.getVerger();
        String vergerTypeOlive = null;
        String vergerAgriculteurNom = null;
        Double vergerSuperficie = null;
        if (v != null) {
            vergerTypeOlive = v.getTypeOlive();
            vergerSuperficie = v.getSuperficie();
            if (v.getAgriculteur() != null) {
                vergerAgriculteurNom = v.getAgriculteur().getPrenom() + " " + v.getAgriculteur().getNom();
            }
        }

        String benneNom = null;
        Double benneCapaciteKg = null;
        if (t.getBenneId() != null) {
            Ressource benne = ressourceRepo.findById(t.getBenneId()).orElse(null);
            if (benne != null) { benneNom = benne.getNom(); benneCapaciteKg = benne.getCapaciteKg(); }
        }

        String tracteurNom = null;
        String tracteurImmat = null;
        if (t.getTracteurId() != null) {
            Ressource tracteur = ressourceRepo.findById(t.getTracteurId()).orElse(null);
            if (tracteur != null) { tracteurNom = tracteur.getNom(); tracteurImmat = tracteur.getImmatriculation(); }
        }

        List<String> travailleurNoms = new ArrayList<>();
        if (t.getTravailleurIds() != null) {
            for (String tid : t.getTravailleurIds()) {
                utilisateurRepo.findById(tid).ifPresent(u ->
                        travailleurNoms.add(u.getPrenom() + " " + u.getNom()));
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
                .efficacite(t.calculerEfficacite())
                .observations(t.getObservations())
                .dateDebut(t.getDateDebut())
                .dateFin(t.getDateFin())
                .dateCreation(t.getDateCreation())
                .totalCollecteVergerKg(totalVerger)
                .build();
    }
}