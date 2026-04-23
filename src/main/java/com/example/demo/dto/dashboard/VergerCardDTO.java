package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VergerCardDTO {
    
    private String id;
    private String agriculteurNom;
    private String agriculteurPrenom;
    private String responsableNom;
    private Double superficie;
    private String typeOlive;
    private Double rendementEstime;
    private Integer maturiteActuelle;
    private String statut;
    private Integer nbArbre;
    private Double totalCollectionKg;
    private String lastCollectionDate;
}
