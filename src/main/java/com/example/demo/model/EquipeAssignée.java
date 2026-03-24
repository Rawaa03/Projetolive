package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipeAssignée {
    private String equipeId; // EQUIPE_NORD, EQUIPE_SUD, etc.
    private String nomEquipe;
    private String chefId;
    private String chefNom;
}