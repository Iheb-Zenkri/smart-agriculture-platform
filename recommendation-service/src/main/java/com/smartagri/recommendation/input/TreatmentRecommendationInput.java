package com.smartagri.recommendation.input;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TreatmentRecommendationInput {
    private Long parcelId;
    private Long cropId;
    private LocalDate recommendationDate;
    private String treatmentType;
    private String productName;
}