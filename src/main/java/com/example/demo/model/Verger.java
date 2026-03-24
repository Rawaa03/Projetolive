package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vergers")
public class Verger {
    
    @Id
    private String id;
    
    private String nom;
    
    private String proprietaireId; // Référence à l'utilisateur (agriculteur)
    
    private Localisation localisation;
    
    private Double superficie; // en hectares
    
    private String typeOlive; // Chemlali, Chétoui, etc.
    
    private Integer nombreArbres;
    
    private Double rendementEstime; // en kg
    
    private Integer maturiteActuelle; // pourcentage
    
    private String statut; // non_recolte, en_cours, recolte
    
    private Date dateDerniereRecolte;
    
    private Boolean estActif;
    
    private Date dateCreation;
}