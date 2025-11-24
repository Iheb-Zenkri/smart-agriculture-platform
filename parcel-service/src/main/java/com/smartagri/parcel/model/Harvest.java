package com.smartagri.parcel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "harvests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Harvest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    @JsonIgnore
    private Crop crop;

    @Column(name = "harvest_date", nullable = false)
    private LocalDate harvestDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantity; // in tons

    @Column(name = "quality_grade", length = 50)
    private String qualityGrade;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
