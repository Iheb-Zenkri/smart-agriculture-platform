package com.smartagri.alert.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_subscriptions", indexes = {
        @Index(name = "idx_subscription_user", columnList = "user_id"),
        @Index(name = "idx_subscription_parcel", columnList = "parcel_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "parcel_id")
    private Long parcelId;

    @Column(name = "alert_types", columnDefinition = "TEXT")
    private String alertTypes; // Comma-separated list

    @Column(name = "notification_method", length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationMethod notificationMethod;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum NotificationMethod {
        EMAIL,
        SMS,
        PUSH,
        IN_APP,
        ALL
    }
}