package com.smartagri.alert.repository;

import com.smartagri.alert.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByIsActiveTrueOrderByAlertTimeDesc();

    List<Alert> findByParcelIdAndIsActiveTrueOrderByAlertTimeDesc(Long parcelId);

    List<Alert> findByAlertTypeAndIsActiveTrueOrderByAlertTimeDesc(Alert.AlertType alertType);

    List<Alert> findBySeverityAndIsActiveTrueOrderByAlertTimeDesc(Alert.AlertSeverity severity);

    @Query("SELECT a FROM Alert a WHERE a.isActive = true AND a.alertTime >= :since ORDER BY a.alertTime DESC")
    List<Alert> findActiveAlertsSince(@Param("since") LocalDateTime since);

    @Query("SELECT a FROM Alert a WHERE a.parcelId = :parcelId AND a.isActive = true " +
            "AND a.alertTime >= :since ORDER BY a.alertTime DESC")
    List<Alert> findActiveAlertsByParcelSince(@Param("parcelId") Long parcelId,
                                              @Param("since") LocalDateTime since);

    @Query("SELECT a FROM Alert a WHERE a.isActive = true AND a.acknowledged = false " +
            "ORDER BY a.severity DESC, a.alertTime DESC")
    List<Alert> findUnacknowledgedAlerts();

    @Query("SELECT a FROM Alert a WHERE a.expiryTime < :now AND a.isActive = true")
    List<Alert> findExpiredAlerts(@Param("now") LocalDateTime now);

    long countByIsActiveTrueAndAcknowledgedFalse();

    long countByParcelIdAndIsActiveTrueAndAcknowledgedFalse(Long parcelId);
}
