package com.smartagri.parcel.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelDTO {
    private Long id;
    private String name;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal surfaceArea;
    private String soilType;
    private String irrigationSystem;
    private List<CropDTO> crops;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

