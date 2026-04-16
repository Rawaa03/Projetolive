package com.example.demo.model;

import com.example.demo.model.enums.StatutCollecte;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

//Embedded statistics object
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollecteStats {
 @Builder.Default
 private Integer nbreTournees = 0;
 
 @Builder.Default
 private Double quantiteTotaleKg = 0.0;
 
 @Builder.Default
 private Integer totalArbresRecoltes = 0;
 
 private Double rendementMoyenParArbre;
 private Double efficaciteMoyenne;
}