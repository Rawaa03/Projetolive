package com.example.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BottleneckDTO {
    
    private String id;
    private String issue;
    private String description;
    private String affectedArea;
    private String severity; // LOW, MEDIUM, HIGH
    private String recommendation;
}
