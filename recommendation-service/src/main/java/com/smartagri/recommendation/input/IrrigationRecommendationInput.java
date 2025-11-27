package com.smartagri.recommendation.input;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IrrigationRecommendationInput {
    private Long parcelId;
    private LocalDate recommendationDate;
    private BigDecimal waterAmount;
    private String irrigationFrequency;
    private String optimalTime;
}