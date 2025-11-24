package com.smartagri.parcel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HarvestDTO {
    private Long id;
    private Long cropId;
    private LocalDate harvestDate;
    private BigDecimal quantity;
    private String qualityGrade;
    private String notes;
    private LocalDateTime createdAt;
}