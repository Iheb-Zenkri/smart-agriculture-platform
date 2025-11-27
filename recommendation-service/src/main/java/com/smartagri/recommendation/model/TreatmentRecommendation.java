package com.smartagri.recommendation.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parcel_id", nullable = false)
    private Long parcelId;

    @Column(name = "crop_id", nullable = false)
    private Long cropId;

    @Column(name = "recommendation_date", nullable = false)
    private LocalDate recommendationDate;

    @Column(name = "treatment_type", length = 100)
    private String treatmentType;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(length = 100)
    private String dosage;

    @Column(name = "target_pest", length = 255)
    private String targetPest;

    @Column(name = "application_timing", length = 100)
    private String applicationTiming;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "weather_conditions")
    private String weatherConditions;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}