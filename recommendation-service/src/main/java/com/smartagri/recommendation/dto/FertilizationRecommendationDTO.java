package com.smartagri.recommendation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FertilizationRecommendationDTO {
    private Long id;
    private Long parcelId;
    private Long cropId;
    private LocalDate recommendationDate;
    private String fertilizerType;
    private String npkRatio;
    private BigDecimal quantity;
    private String applicationMethod;
    private String reasoning;
    private String growthStage;
    private String soilType;
}
