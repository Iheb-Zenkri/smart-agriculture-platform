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
@Table(name = "fertilization_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FertilizationRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parcel_id", nullable = false)
    private Long parcelId;

    @Column(name = "crop_id", nullable = false)
    private Long cropId;

    @Column(name = "recommendation_date", nullable = false)
    private LocalDate recommendationDate;

    @Column(name = "fertilizer_type", length = 100)
    private String fertilizerType;

    @Column(name = "npk_ratio", length = 50)
    private String npkRatio;

    @Column(precision = 6, scale = 2)
    private BigDecimal quantity; // in kg/ha

    @Column(name = "application_method", length = 100)
    private String applicationMethod;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "growth_stage")
    private String growthStage;

    @Column(name = "soil_type")
    private String soilType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
