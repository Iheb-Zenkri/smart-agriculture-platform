package com.smartagri.recommendation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationRecommendationDTO {
    private Long id;
    private Long parcelId;
    private LocalDate recommendationDate;
    private BigDecimal waterAmount;
    private String irrigationFrequency;
    private String optimalTime;
    private String reasoning;
    private BigDecimal confidenceScore;
    private WeatherFactors weatherFactors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherFactors {
        private BigDecimal temperature;
        private BigDecimal humidity;
        private BigDecimal precipitation;
    }
}