package com.smartagri.recommendation.input;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FertilizationRecommendationInput {
    private Long parcelId;
    private Long cropId;
    private LocalDate recommendationDate;
    private String fertilizerType;
    private String npkRatio;
    private BigDecimal quantity;
}
