package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TourneeRequest {

    /** ID of the verger being harvested. Required. */
    @NotBlank(message = "L'identifiant du verger est obligatoire")
    private String vergerId;

    /** ID of the benne assigned to this tournée. Required. */
    @NotBlank(message = "L'identifiant de la benne est obligatoire")
    private String benneId;

    /** ID of the tracteur assigned to this tournée. Required. */
    @NotBlank(message = "L'identifiant du tracteur est obligatoire")
    private String tracteurId;

    /**
     * IDs of workers assigned to this tournée.
     * At least 1 travailleur is mandatory.
     */
    @NotEmpty(message = "Au moins un travailleur doit être assigné")
    private List<String> travailleurIds;

    /**
     * Number of olive trees to harvest in this tournée.
     * Defaults to 200 in the service if not provided.
     */
    @Positive(message = "Le nombre d'arbres doit être positif")
    private Integer nbreArbre;

    /** Planned start date (optional — can be set when demarrer() is called). */
    private Date dateDebut;

    /** Total distance planned in km. */
    @PositiveOrZero(message = "La distance doit être positive ou nulle")
    private Double distanceTotale;

    /** Free-text observations. */
    private String observations;
}