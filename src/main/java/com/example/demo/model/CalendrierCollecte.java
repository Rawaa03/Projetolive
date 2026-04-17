package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "calendrier_collectes")
public class CalendrierCollecte {
    
    @Id
    private String id;
    
    @DocumentReference(lazy = true)
    private Tournee tournee;
    
    @DocumentReference(lazy = true)
    private Verger verger;
    
    private Date datePlanifiee;
    private Date dateReelle;
    private String creneau; // MATIN, APRES_MIDI, JOURNEE
    
    private String equipeId;
    private String responsableId;
    
    private String statut; // PLANIFIEE, EN_COURS, TERMINEE, ANNULEE
    
    private String notes;
    private Date dateCreation;
    private Date dateModification;
}