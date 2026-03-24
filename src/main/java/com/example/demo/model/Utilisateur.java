package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "utilisateurs")
public class Utilisateur {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String motDePasse;
    
    private String prenom;
    
    private String nom;
    
    private String telephone;
    
    private String role; // responsable, agriculteur, equipe_recolte, transporteur, pressoir
    
    private String adresse;
    
    // Pour agriculteur - liste des vergers (objets complets)
    private List<Verger> vergers;
    
    // Pour équipe de récolte
    private EquipeInfo equipeInfo;
    
    // Pour transporteur
    private Ressource vehicule;
    
    // Pour pressoir
    private PressoirInfo pressoirInfo;
    
    private Boolean estActif;
    
    private Date dateCreation;
}