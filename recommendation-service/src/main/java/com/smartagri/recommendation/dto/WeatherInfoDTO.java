package com.smartagri.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInfoDTO {
    private String location;
    private LocalDate date;
    private BigDecimal temperatureMin;
    private BigDecimal temperatureMax;
    private BigDecimal temperatureAvg;
    private BigDecimal humidity;
    private BigDecimal precipitation;
    private BigDecimal windSpeed;
    private BigDecimal pressure;
    private String weatherCondition;
    private Integer cloudCover;
}
