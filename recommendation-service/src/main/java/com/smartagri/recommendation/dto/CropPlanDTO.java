package com.smartagri.recommendation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropPlanDTO {
    private Long id;
    private Long parcelId;
    private String recommendedCrop;
    private String recommendedVariety;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private BigDecimal expectedYield;
    private BigDecimal confidenceScore;
    private String reasoning;
    private String soilSuitability;
    private String climateSuitability;
}