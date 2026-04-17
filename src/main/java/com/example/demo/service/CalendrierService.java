package com.example.demo.service;

import com.example.demo.dto.EvenementCalendrierDTO;
import java.util.Date;
import java.util.List;

public interface CalendrierService {
    
    // H.2 - Consulter le planning
    List<EvenementCalendrierDTO> getEvenements(Date debut, Date fin);
    List<EvenementCalendrierDTO> getEvenementsByVerger(String vergerId, Date debut, Date fin);
    List<EvenementCalendrierDTO> getEvenementsByTravailleur(String travailleurId, Date debut, Date fin);
    
    // H.3 - Modifier une date
    EvenementCalendrierDTO reprogrammerEvenement(String tourneeId, Date nouvelleDate, String raison);
	List<EvenementCalendrierDTO> getEvenementsByUserConnected(Date debut, Date fin);
	List<EvenementCalendrierDTO> getEvenementsByUserEmail(String email, Date debut, Date fin);
	List<EvenementCalendrierDTO> getEvenementsByVergerAndTravailleur(String vergerId, String travailleurId, Date debut, Date fin);
}