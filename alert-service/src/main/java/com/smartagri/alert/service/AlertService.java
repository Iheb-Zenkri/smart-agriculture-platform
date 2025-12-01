package com.smartagri.alert.service;

import com.smartagri.alert.dto.AlertSearchCriteria;
import com.smartagri.alert.model.Alert;
import com.smartagri.alert.model.AlertSubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AlertService {

    // Alert CRUD operations
    Alert createAlert(Alert.AlertType alertType, Alert.AlertSeverity severity, Long parcelId,
                      String location, String title, String message, Long expirySeconds, String metadata);

    Alert getAlertById(Long alertId);

    List<Alert> getActiveAlerts();

    List<Alert> getActiveAlertsByParcel(Long parcelId);

    List<Alert> getActiveAlertsByType(Alert.AlertType alertType);

    List<Alert> getActiveAlertsBySeverity(Alert.AlertSeverity severity);

    List<Alert> getActiveAlertsSince(LocalDateTime since);

    @Transactional(readOnly = true)
    Map<String, Long> getAlertStatistics(Long parcelId);

    @Transactional(readOnly = true)
    Page<Alert> searchAlerts(AlertSearchCriteria criteria, Pageable pageable);

    List<Alert> getUnacknowledgedAlerts();

    Alert acknowledgeAlert(Long alertId, String acknowledgedBy);

    @Transactional
    List<Alert> acknowledgeMultipleAlerts(List<Long> alertIds, String acknowledgedBy);

    void dismissAlert(Long alertId, String dismissedBy);

    void expireOldAlerts();

    // Subscription operations
    Long createSubscription(String userId, Long parcelId, List<String> alertTypes,
                            String notificationMethod, String email, String phoneNumber);

    AlertSubscription getSubscription(String userId, Long parcelId);

    List<AlertSubscription> getSubscriptionsForParcel(Long parcelId);

    void notifySubscribers(Alert alert);

    // Statistics
    long countUnacknowledgedAlerts();

    long countUnacknowledgedAlertsByParcel(Long parcelId);

    // 7. Add health check method
    Map<String, Object> getServiceHealth();

    @Transactional(readOnly = true)
    Map<String, Object> getAlertTrends(Long parcelId, int days);
}