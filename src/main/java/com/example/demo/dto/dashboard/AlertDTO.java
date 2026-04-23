package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {
    
    private String id;
    private String titre;
    private String description;
    private String level; // INFO, WARNING, CRITICAL
    private String category; // PRODUCTION, WORKER, RESOURCE, SYSTEM
    private Date dateCreation;
    private Boolean lue;
    private String relatedEntityId;
    private String relatedEntityType;
}
