package com.smartagri.alert.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts", indexes = {
        @Index(name = "idx_alert_parcel", columnList = "parcel_id"),
        @Index(name = "idx_alert_active", columnList = "is_active, alert_time"),
        @Index(name = "idx_alert_type", columnList = "alert_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AlertType alertType;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    @Column(name = "parcel_id")
    private Long parcelId;

    @Column(length = 255)
    private String location;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "alert_time", nullable = false)
    private LocalDateTime alertTime;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "acknowledged")
    private Boolean acknowledged = false;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "acknowledged_by")
    private String acknowledgedBy;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum AlertType {
        WEATHER,
        PEST,
        DISEASE,
        THRESHOLD,
        IRRIGATION,
        FERTILIZATION,
        HARVEST,
        SYSTEM
    }

    public enum AlertSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}