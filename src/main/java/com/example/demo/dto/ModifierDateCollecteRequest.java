package com.example.demo.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModifierDateCollecteRequest {
 private Date nouvelleDate;
 private String raison;
 private boolean mettreAJourTournee;
}