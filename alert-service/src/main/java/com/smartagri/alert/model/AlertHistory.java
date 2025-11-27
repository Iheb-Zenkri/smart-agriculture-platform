package com.smartagri.alert.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_id", nullable = false)
    private Long alertId;

    @Column(name = "action", nullable = false, length = 50)
    private String action; // CREATED, ACKNOWLEDGED, DISMISSED, EXPIRED

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "action_time", updatable = false)
    private LocalDateTime actionTime;
}
