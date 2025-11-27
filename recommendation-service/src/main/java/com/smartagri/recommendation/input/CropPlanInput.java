package com.smartagri.recommendation.input;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CropPlanInput {
    private Long parcelId;
    private String recommendedCrop;
    private String recommendedVariety;
    private LocalDate plantingDate;
}