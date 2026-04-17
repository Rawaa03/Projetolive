package com.example.demo.dto;

import com.example.demo.model.Collecte;
import com.example.demo.model.Tournee;
import com.example.demo.model.Verger;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CollecteDetailDTO {
    private Collecte collecte;
    private List<Tournee> tournees;
    private Verger verger;
    private Integer nbreTournees;
    private Double quantiteTotaleKg;

    // Helper method to compute totals
    public Integer getNbreTournees() {
        return tournees != null ? tournees.size() : 0;
    }

    public Double getQuantiteTotaleKg() {
        if (tournees == null) return 0.0;
        return tournees.stream()
                .filter(t -> t.getQuantiteCollecteeKg() != null)
                .mapToDouble(Tournee::getQuantiteCollecteeKg)
                .sum();
    }
}