package com.smartagri.recommendation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentRecommendationDTO {
    private Long id;
    private Long parcelId;
    private Long cropId;
    private LocalDate recommendationDate;
    private String treatmentType;
    private String productName;
    private String dosage;
    private String targetPest;
    private String applicationTiming;
    private String reasoning;
    private String weatherConditions;
}
