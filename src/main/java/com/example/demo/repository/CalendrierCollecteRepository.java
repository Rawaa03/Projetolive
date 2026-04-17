package com.example.demo.repository;

import com.example.demo.model.CalendrierCollecte;
import com.example.demo.model.Verger;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface CalendrierCollecteRepository extends MongoRepository<CalendrierCollecte, String> {
    
    List<CalendrierCollecte> findByDatePlanifieeBetween(Date debut, Date fin);
    
    List<CalendrierCollecte> findByVergerId(String vergerId);
    
    List<CalendrierCollecte> findByStatut(String statut);
    
    @Query("{ 'datePlanifiee': { $gte: ?0, $lte: ?1 }, 'statut': { $ne: 'ANNULEE' } }")
    List<CalendrierCollecte> findActiveBetweenDates(Date debut, Date fin);
    
    List<CalendrierCollecte> findByResponsableId(String responsableId);
}