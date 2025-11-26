package com.smartagri.weather.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "weather_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {"location_id", "date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "temperature_min", precision = 5, scale = 2)
    private BigDecimal temperatureMin;

    @Column(name = "temperature_max", precision = 5, scale = 2)
    private BigDecimal temperatureMax;

    @Column(name = "temperature_avg", precision = 5, scale = 2)
    private BigDecimal temperatureAvg;

    @Column(precision = 5, scale = 2)
    private BigDecimal humidity;

    @Column(precision = 6, scale = 2)
    private BigDecimal precipitation;

    @Column(name = "wind_speed", precision = 5, scale = 2)
    private BigDecimal windSpeed;

    @Column(precision = 6, scale = 2)
    private BigDecimal pressure;

    @Column(name = "weather_condition")
    private String weatherCondition;

    @Column(name = "cloud_cover")
    private Integer cloudCover;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
