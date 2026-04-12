package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class TerminerTourneeRequest {

    /** Actual kg of olives collected during this tournée. */
    @NotNull(message = "La quantité collectée est obligatoire")
    @PositiveOrZero(message = "La quantité doit être positive ou nulle")
    private Double quantiteCollecteeKg;

    /** Distance actually travelled (km). Can update the estimate. */
    @PositiveOrZero
    private Double distanceTotale;

    /** Any final observations / notes. */
    private String observations;
}