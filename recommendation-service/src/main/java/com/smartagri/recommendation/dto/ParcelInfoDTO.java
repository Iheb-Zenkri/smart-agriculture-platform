package com.smartagri.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelInfoDTO {
    private Long id;
    private String name;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal surfaceArea;
    private String soilType;
    private String irrigationSystem;
}