package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourneeCardDTO {
    
    private String id;
    private String code;
    private String vergerNom;
    private String statut;
    private Date dateDebut;
    private Date dateFin;
    private Integer nbreArbre;
    private Double quantiteCollecteeKg;
    private Boolean collecteFinalisee;
    private List<String> travailleurs;
    private String benne;
    private String tracteur;
}
