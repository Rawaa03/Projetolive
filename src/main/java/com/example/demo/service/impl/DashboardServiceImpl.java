package com.example.demo.service.impl;

import com.example.demo.dto.dashboard.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private VergerRepository vergerRepository;

    @Autowired
    private CollecteRepository collecteRepository;

    @Autowired
    private TourneeRepository tourneeRepository;

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    @Override
    public AgriculteurDashboardDTO getAgriculteurDashboard(String userId) {
        Utilisateur agriculteur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Agriculteur not found"));

        List<Verger> vergers = vergerRepository.findByAgriculteurId(userId);
        List<VergerCardDTO> vergerCards = vergers.stream()
                .map(this::convertVergerToCard)
                .collect(Collectors.toList());

        // Calculate total production
        double totalProduction = 0.0;
        double targetProduction = 0.0;
        for (Verger verger : vergers) {
            List<Collecte> collectes = collecteRepository.findByVergerId(verger.getId());
            for (Collecte collecte : collectes) {
                totalProduction += collecte.getQuantiteTotaleKg() != null ? collecte.getQuantiteTotaleKg() : 0;
                targetProduction += verger.getRendementEstime() != null ? verger.getRendementEstime() : 0;
            }
        }

        // Get active tours
        List<TourneeCardDTO> activeTournees = getActiveTournees(vergers);

        // Get verger progress
        List<VergerProgressDTO> vergerProgress = vergers.stream()
                .map(this::convertVergerToProgress)
                .collect(Collectors.toList());

        // Get alerts (placeholder)
        List<AlertDTO> alerts = new ArrayList<>();

        // Calculate stats
        int totalArbreCollecte = vergers.stream().mapToInt(v -> v.getNbArbre()).sum();
        int activeVergers = (int) vergers.stream()
                .filter(v -> v.getStatut() != null && v.getStatut().name().equals("ACTIF"))
                .count();

        double productionPercentage = targetProduction > 0 ? (totalProduction / targetProduction) * 100 : 0;

        return AgriculteurDashboardDTO.builder()
                .utilisateurId(userId)
                .nom(agriculteur.getNom())
                .prenom(agriculteur.getPrenom())
                .vergers(vergerCards)
                .totalVergers(vergers.size())
                .vergerActifs(activeVergers)
                .totalProductionKg(totalProduction)
                .targetProductionKg(targetProduction)
                .productionPercentage(productionPercentage)
                .activeTournees(activeTournees)
                .totalActiveTournees(activeTournees.size())
                .vergerProgressData(vergerProgress)
                .alerts(alerts)
                .totalAlerts(alerts.size())
                .totalArbreCollecte(totalArbreCollecte)
                .build();
    }

    @Override
    public ResponsableDashboardDTO getResponsableDashboard(String userId) {
        Utilisateur responsable = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Responsable not found"));

        // Get assigned vergers
        List<Verger> assignedVergers = vergerRepository.findByResponsableId(userId);
        List<VergerCardDTO> vergerCards = assignedVergers.stream()
                .map(this::convertVergerToCard)
                .collect(Collectors.toList());

        // Calculate production stats
        double totalCollection = 0.0;
        double targetCollection = 0.0;
        for (Verger verger : assignedVergers) {
            List<Collecte> collectes = collecteRepository.findByVergerId(verger.getId());
            for (Collecte collecte : collectes) {
                totalCollection += collecte.getQuantiteTotaleKg() != null ? collecte.getQuantiteTotaleKg() : 0;
                targetCollection += verger.getRendementEstime() != null ? verger.getRendementEstime() : 0;
            }
        }

        double collectionPercentage = targetCollection > 0 ? (totalCollection / targetCollection) * 100 : 0;

        // Get workers (from Utilisateur with role TRAVAILLEUR)
        List<Utilisateur> workers = utilisateurRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equals("TRAVAILLEUR"))
                .collect(Collectors.toList());
        
        List<WorkerCardDTO> workerCards = workers.stream()
                .limit(10)
                .map(this::convertWorkerToCard)
                .collect(Collectors.toList());

        // Get tournees stats
        List<Tournee> allTournees = tourneeRepository.findAll();
        List<Tournee> responsableTournees = allTournees.stream()
                .filter(t -> assignedVergers.stream().anyMatch(v -> v.getId().equals(t.getVerger().getId())))
                .collect(Collectors.toList());

        int planified = (int) responsableTournees.stream().filter(t -> t.getStatut() != null && t.getStatut().name().equals("PLANIFIEE")).count();
        int ongoing = (int) responsableTournees.stream().filter(t -> t.getStatut() != null && t.getStatut().name().equals("EN_COURS")).count();
        int completed = (int) responsableTournees.stream().filter(t -> t.getStatut() != null && t.getStatut().name().equals("TERMINEE")).count();

        List<TourneeCardDTO> recentTournees = responsableTournees.stream()
                .sorted(Comparator.comparing(Tournee::getDateCreation, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::convertTourneeToCard)
                .collect(Collectors.toList());

        // Get production by verger
        List<VergerProductionDTO> vergerProduction = assignedVergers.stream()
                .map(v -> {
                    List<Collecte> collectes = collecteRepository.findByVergerId(v.getId());
                    double actual = collectes.stream().mapToDouble(c -> c.getQuantiteTotaleKg() != null ? c.getQuantiteTotaleKg() : 0).sum();
                    double target = v.getRendementEstime() != null ? v.getRendementEstime() : 0;
                    return VergerProductionDTO.builder()
                            .vergerId(v.getId())
                            .vergerName(v.getTypeOlive())
                            .actualProduction(actual)
                            .targetProduction(target)
                            .percentageOfTarget(target > 0 ? (actual / target) * 100 : 0)
                            .status(v.getStatut() != null ? v.getStatut().name() : "UNKNOWN")
                            .build();
                })
                .collect(Collectors.toList());

        // Calculate efficiency metrics
        double averageKgPerWorker = workers.size() > 0 ? totalCollection / workers.size() : 0;
        List<EfficiencyMetricDTO> metrics = new ArrayList<>();
        metrics.add(EfficiencyMetricDTO.builder()
                .metricName("Kg per Worker")
                .value(averageKgPerWorker)
                .unit("kg")
                .benchmark(5000.0)
                .status(averageKgPerWorker > 5000 ? "GOOD" : "WARNING")
                .build());

        return ResponsableDashboardDTO.builder()
                .utilisateurId(userId)
                .nom(responsable.getNom())
                .prenom(responsable.getPrenom())
                .assignedVergers(vergerCards)
                .totalAssignedVergers(assignedVergers.size())
                .workers(workerCards)
                .totalWorkers(workers.size())
                .activeWorkers((int) workers.stream().filter(w -> w.isCompteActif()).count())
                .totalCollectionKg(totalCollection)
                .targetCollectionKg(targetCollection)
                .collectionPercentage(collectionPercentage)
                .planifiedTournees(planified)
                .ongoingTournees(ongoing)
                .completedTournees(completed)
                .recentTournees(recentTournees)
                .vergerProduction(vergerProduction)
                .averageKgPerWorker(averageKgPerWorker)
                .efficiencyRating(collectionPercentage / 100)
                .efficiencyMetrics(metrics)
                .alerts(new ArrayList<>())
                .totalAlerts(0)
                .build();
    }

    @Override
    public AdminDashboardDTO getAdminDashboard() {
        // Total users by role
        List<Utilisateur> allUsers = utilisateurRepository.findAll();
        Map<String, Integer> usersByRole = new HashMap<>();
        allUsers.forEach(u -> {
            String role = u.getRole() != null ? u.getRole().name() : "UNKNOWN";
            usersByRole.put(role, usersByRole.getOrDefault(role, 0) + 1);
        });

        int activeUsers = (int) allUsers.stream().filter(Utilisateur::isCompteActif).count();

        // All vergers
        List<Verger> allVergers = vergerRepository.findAll();
        List<VergerCardDTO> vergerCards = allVergers.stream()
                .map(this::convertVergerToCard)
                .collect(Collectors.toList());

        Map<String, Integer> vergersByStatus = new HashMap<>();
        allVergers.forEach(v -> {
            String status = v.getStatut() != null ? v.getStatut().name() : "UNKNOWN";
            vergersByStatus.put(status, vergersByStatus.getOrDefault(status, 0) + 1);
        });

        // System-wide production
        List<Collecte> allCollectes = collecteRepository.findAll();
        double totalSystemProduction = allCollectes.stream()
                .mapToDouble(c -> c.getQuantiteTotaleKg() != null ? c.getQuantiteTotaleKg() : 0)
                .sum();

        double averagePerVerger = allVergers.size() > 0 ? totalSystemProduction / allVergers.size() : 0;

        // Resource utilization
        List<Ressource> allRessources = ressourceRepository.findAll();
        long activeBennes = allRessources.stream().filter(r -> "BENNE".equals(r.getType())).count();
        long activeTracteurs = allRessources.stream().filter(r -> "TRACTEUR".equals(r.getType())).count();

        ResourceUtilizationDTO utilization = ResourceUtilizationDTO.builder()
                .totalBennes((int) allRessources.stream().filter(r -> "BENNE".equals(r.getType())).count())
                .activeBennes((int) activeBennes)
                .benneUtilizationPercent(activeBennes > 0 ? 75.0 : 0)
                .totalTracteurs((int) allRessources.stream().filter(r -> "TRACTEUR".equals(r.getType())).count())
                .activeTracteurs((int) activeTracteurs)
                .tracteurUtilizationPercent(activeTracteurs > 0 ? 80.0 : 0)
                .costPerKg(totalSystemProduction > 0 ? 100000 / totalSystemProduction : 0)
                .build();

        // Top performers
        List<TopPerformerDTO> topVergers = allVergers.stream()
                .sorted((v1, v2) -> {
                    List<Collecte> c1 = collecteRepository.findByVergerId(v1.getId());
                    List<Collecte> c2 = collecteRepository.findByVergerId(v2.getId());
                    double p1 = c1.stream().mapToDouble(c -> c.getQuantiteTotaleKg() != null ? c.getQuantiteTotaleKg() : 0).sum();
                    double p2 = c2.stream().mapToDouble(c -> c.getQuantiteTotaleKg() != null ? c.getQuantiteTotaleKg() : 0).sum();
                    return Double.compare(p2, p1);
                })
                .limit(5)
                .map(v -> {
                    List<Collecte> collectes = collecteRepository.findByVergerId(v.getId());
                    double production = collectes.stream().mapToDouble(c -> c.getQuantiteTotaleKg() != null ? c.getQuantiteTotaleKg() : 0).sum();
                    return TopPerformerDTO.builder()
                            .id(v.getId())
                            .name(v.getTypeOlive())
                            .productionKg(production)
                            .unit("kg")
                            .build();
                })
                .collect(Collectors.toList());

        return AdminDashboardDTO.builder()
                .totalUsers(allUsers.size())
                .usersByRole(usersByRole)
                .activeUsers(activeUsers)
                .inactiveUsers(allUsers.size() - activeUsers)
                .totalVergers(allVergers.size())
                .allVergers(vergerCards)
                .vergersByStatus(vergersByStatus)
                .resourceUtilization(utilization)
                .totalSystemProductionKg(totalSystemProduction)
                .totalSystemCollectionTours(allCollectes.size())
                .systemAverageProductionPerVerger(averagePerVerger)
                .systemAlerts(new ArrayList<>())
                .alertsByLevel(new HashMap<>())
                .totalAlerts(0)
                .topPerformingVergers(topVergers)
                .totalWorkers((int) allUsers.stream().filter(u -> u.getRole() != null && u.getRole().name().equals("TRAVAILLEUR")).count())
                .build();
    }

    // Helper methods
    private VergerCardDTO convertVergerToCard(Verger verger) {
        List<Collecte> collectes = collecteRepository.findByVergerId(verger.getId());
        double totalKg = collectes.stream().mapToDouble(c -> c.getQuantiteTotaleKg() != null ? c.getQuantiteTotaleKg() : 0).sum();

        return VergerCardDTO.builder()
                .id(verger.getId())
                .agriculteurNom(verger.getAgriculteur() != null ? verger.getAgriculteur().getNom() : "N/A")
                .agriculteurPrenom(verger.getAgriculteur() != null ? verger.getAgriculteur().getPrenom() : "N/A")
                .responsableNom(verger.getResponsable() != null ? verger.getResponsable().getNom() : "N/A")
                .superficie(verger.getSuperficie())
                .typeOlive(verger.getTypeOlive())
                .rendementEstime(verger.getRendementEstime())
                .maturiteActuelle(verger.getMaturiteActuelle())
                .statut(verger.getStatut() != null ? verger.getStatut().name() : "UNKNOWN")
                .nbArbre(verger.getNbArbre())
                .totalCollectionKg(totalKg)
                .build();
    }

    private VergerProgressDTO convertVergerToProgress(Verger verger) {
        List<Collecte> collectes = collecteRepository.findByVergerId(verger.getId());
        double collectedKg = collectes.stream().mapToDouble(c -> c.getQuantiteTotaleKg() != null ? c.getQuantiteTotaleKg() : 0).sum();
        double estimatedKg = verger.getRendementEstime() != null ? verger.getRendementEstime() : 0;
        double progressPercentage = estimatedKg > 0 ? (collectedKg / estimatedKg) * 100 : 0;

        return VergerProgressDTO.builder()
                .vergerId(verger.getId())
                .vergerName(verger.getTypeOlive())
                .collectedKg(collectedKg)
                .estimatedKg(estimatedKg)
                .progressPercentage(progressPercentage)
                .statut(verger.getStatut() != null ? verger.getStatut().name() : "UNKNOWN")
                .maturite(verger.getMaturiteActuelle())
                .build();
    }

    private TourneeCardDTO convertTourneeToCard(Tournee tournee) {
        List<String> travailleurs = tournee.getTravailleurs() != null 
                ? tournee.getTravailleurs().stream().map(t -> t.getNom() + " " + t.getPrenom()).collect(Collectors.toList())
                : new ArrayList<>();

        return TourneeCardDTO.builder()
                .id(tournee.getId())
                .code(tournee.getCode())
                .vergerNom(tournee.getVerger() != null ? tournee.getVerger().getTypeOlive() : "N/A")
                .statut(tournee.getStatut() != null ? tournee.getStatut().name() : "UNKNOWN")
                .dateDebut(tournee.getDateDebut())
                .dateFin(tournee.getDateFin())
                .nbreArbre(tournee.getNbreArbre())
                .quantiteCollecteeKg(tournee.getQuantiteCollecteeKg())
                .collecteFinalisee(tournee.getCollecteFinalisee())
                .travailleurs(travailleurs)
                .benne(tournee.getBenne() != null ? tournee.getBenne().getCode() : "N/A")
                .tracteur(tournee.getTracteur() != null ? tournee.getTracteur().getCode() : "N/A")
                .build();
    }

    private WorkerCardDTO convertWorkerToCard(Utilisateur worker) {
        return WorkerCardDTO.builder()
                .id(worker.getId())
                .nom(worker.getNom())
                .prenom(worker.getPrenom())
                .type(worker.getRole() != null ? worker.getRole().name() : "TRAVAILLEUR")
                .specialite(worker.getSpecialites() != null && !worker.getSpecialites().isEmpty() ? worker.getSpecialites().get(0) : "General")
                .statut(worker.isCompteActif() ? "ACTIF" : "INACTIF")
                .dateEmbauche(worker.getDateEmbauche())
                .salaireJournalier(worker.getSalaire())
                .build();
    }

    private List<TourneeCardDTO> getActiveTournees(List<Verger> vergers) {
        List<String> vergerIds = vergers.stream().map(Verger::getId).collect(Collectors.toList());
        return tourneeRepository.findAll().stream()
                .filter(t -> vergerIds.contains(t.getVerger().getId()) && 
                       t.getStatut() != null && (t.getStatut().name().equals("EN_COURS") || t.getStatut().name().equals("PLANIFIEE")))
                .map(this::convertTourneeToCard)
                .collect(Collectors.toList());
    }
}
