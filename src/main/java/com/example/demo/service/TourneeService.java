package com.example.demo.service;

import com.example.demo.model.Tournee;
import com.example.demo.repository.TourneeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TourneeService {

    @Autowired
    private TourneeRepository tourneeRepository;

    /**
     * Créer une nouvelle tournée
     */
    public Tournee creerTournee(Tournee tournee) {
        System.out.println("🗺️ Création d'une tournée: " + tournee.getCode());

        if (tournee.getCode() == null || tournee.getCode().trim().isEmpty()) {
            throw new RuntimeException("Le code de la tournée est requis");
        }

        return tourneeRepository.save(tournee);
    }

    /**
     * Récupérer une tournée par ID
     */
    public Tournee getTourneeById(String id) {
        return tourneeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournée non trouvée: " + id));
    }

    /**
     * Lister toutes les tournées
     */
    public List<Tournee> listerTournees() {
        return tourneeRepository.findAll();
    }

    /**
     * Mettre à jour une tournée
     */
    public Tournee mettreAJourTournee(String id, Tournee tourneeUpdate) {
        Tournee tournee = getTourneeById(id);

        tournee.setCode(tourneeUpdate.getCode());
        tournee.setStatut(tourneeUpdate.getStatut());
        tournee.setDistanceTotale(tourneeUpdate.getDistanceTotale());
        tournee.setObservations(tourneeUpdate.getObservations());

        return tourneeRepository.save(tournee);
    }

    /**
     * Supprimer une tournée
     */
    public void supprimerTournee(String id) {
        Tournee tournee = getTourneeById(id);
        tourneeRepository.delete(tournee);
    }
}
