package com.smartagri.recommendation.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "irrigation_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parcel_id", nullable = false)
    private Long parcelId;

    @Column(name = "recommendation_date", nullable = false)
    private LocalDate recommendationDate;

    @Column(name = "water_amount", precision = 6, scale = 2)
    private BigDecimal waterAmount; // in mm or liters/m²

    @Column(name = "irrigation_frequency", length = 50)
    private String irrigationFrequency;

    @Column(name = "optimal_time", length = 50)
    private String optimalTime;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "confidence_score", precision = 3, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "based_on_temperature")
    private BigDecimal basedOnTemperature;

    @Column(name = "based_on_humidity")
    private BigDecimal basedOnHumidity;

    @Column(name = "based_on_precipitation")
    private BigDecimal basedOnPrecipitation;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}