package com.smartagri.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropInfoDTO {
    private Long id;
    private Long parcelId;
    private String cropType;
    private String variety;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private String growthStage;
    private String status;
}
