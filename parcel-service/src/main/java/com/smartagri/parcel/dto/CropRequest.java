package com.smartagri.parcel.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropRequest {

    @NotNull(message = "Parcel ID is required")
    private Long parcelId;

    @NotBlank(message = "Crop type is required")
    private String cropType;

    private String variety;

    @NotNull(message = "Planting date is required")
    private LocalDate plantingDate;

    private LocalDate expectedHarvestDate;

    private String growthStage;

    private String status;
}
