package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bennes")
public class Bennes {
    
    @Id
    private String id;
    
    private String nom;
    
    private String prenom;
    
    private String telephone;
    
    private String adresse;
    
    private String email;
    
    private Date dateEmbauche;
    
    private String type;  
    
    private String specialite; 
    
    private String statut;  // "ACTIF", "SUSPENDU"
    
    private String collecteActuelleId;  // ID de la collecte assignée
    
    private Double salaireJournalier;
}