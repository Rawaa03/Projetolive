package com.example.demo.dto;

import com.example.demo.model.enums.TypeAlerte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlerteRequest {

    @NotBlank
    private String agriculteurId;
    @NotBlank(message = "Le verger est obligatoire")
    private String vergerId;

    @NotNull
    private TypeAlerte type;

    @NotBlank
    private String description;

    // Note: Location is now taken from the selected Verger
    // These fields are kept for backward compatibility but are not used
    private Double latitude;
    private Double longitude;
    private String adresseIndicative;
}