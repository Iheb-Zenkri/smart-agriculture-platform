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
@Table(name = "climate_indices",
        uniqueConstraints = @UniqueConstraint(columnNames = {"location_id", "date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClimateIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "growing_degree_days", precision = 6, scale = 2)
    private BigDecimal growingDegreeDays;

    @Column(precision = 6, scale = 2)
    private BigDecimal evapotranspiration;

    @Column(name = "drought_index", precision = 5, scale = 2)
    private BigDecimal droughtIndex;

    @Column(name = "heat_stress_index", precision = 5, scale = 2)
    private BigDecimal heatStressIndex;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
