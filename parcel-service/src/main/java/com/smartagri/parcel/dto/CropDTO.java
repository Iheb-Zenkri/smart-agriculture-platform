package com.smartagri.parcel.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropDTO {
    private Long id;
    private Long parcelId;
    private String cropType;
    private String variety;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private String growthStage;
    private String status;
    private List<HarvestDTO> harvests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

