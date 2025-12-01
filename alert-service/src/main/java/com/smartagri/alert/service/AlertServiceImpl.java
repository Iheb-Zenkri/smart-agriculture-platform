package com.smartagri.alert.service;

import com.smartagri.alert.dto.AlertSearchCriteria;
import com.smartagri.alert.exception.AlertNotFoundException;
import com.smartagri.alert.model.Alert;
import com.smartagri.alert.model.AlertHistory;
import com.smartagri.alert.model.AlertSubscription;
import com.smartagri.alert.repository.AlertHistoryRepository;
import com.smartagri.alert.repository.AlertRepository;
import com.smartagri.alert.repository.AlertSubscriptionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final AlertSubscriptionRepository subscriptionRepository;
    private final AlertHistoryRepository historyRepository;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 5000;

    @Override
    public Alert createAlert(Alert.AlertType alertType, Alert.AlertSeverity severity, Long parcelId,
                             String location, String title, String message, Long expirySeconds, String metadata) {
        log.info("Creating alert: type={}, severity={}, parcelId={}", alertType, severity, parcelId);

        Alert alert = new Alert();
        alert.setAlertType(alertType);
        alert.setSeverity(severity);
        alert.setParcelId(parcelId);
        alert.setLocation(location);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setAlertTime(LocalDateTime.now());
        alert.setIsActive(true);
        alert.setAcknowledged(false);
        alert.setMetadata(metadata);

        if (expirySeconds != null && expirySeconds > 0) {
            alert.setExpiryTime(LocalDateTime.now().plusSeconds(expirySeconds));
        }

        Alert savedAlert = alertRepository.save(alert);

        // Record in history
        recordHistory(savedAlert.getId(), "CREATED", null, "Alert created");

        // Notify subscribers
        notifySubscribers(savedAlert);

        log.info("Alert created successfully with ID: {}", savedAlert.getId());
        return savedAlert;
    }

    @Override
    @Transactional(readOnly = true)
    public Alert getAlertById(Long alertId) {
        log.info("Getting alert by ID: {}", alertId);
        return alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found with ID: " + alertId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alert> getActiveAlerts() {
        log.info("Getting all active alerts");
        return alertRepository.findByIsActiveTrueOrderByAlertTimeDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alert> getActiveAlertsByParcel(Long parcelId) {
        log.info("Getting active alerts for parcel: {}", parcelId);
        return alertRepository.findByParcelIdAndIsActiveTrueOrderByAlertTimeDesc(parcelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alert> getActiveAlertsByType(Alert.AlertType alertType) {
        log.info("Getting active alerts by type: {}", alertType);
        return alertRepository.findByAlertTypeAndIsActiveTrueOrderByAlertTimeDesc(alertType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alert> getActiveAlertsBySeverity(Alert.AlertSeverity severity) {
        log.info("Getting active alerts by severity: {}", severity);
        return alertRepository.findBySeverityAndIsActiveTrueOrderByAlertTimeDesc(severity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alert> getActiveAlertsSince(LocalDateTime since) {
        log.info("Getting active alerts since: {}", since);
        return alertRepository.findActiveAlertsSince(since);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getAlertStatistics(Long parcelId) {
        Map<String, Long> stats = new HashMap<>();

        if (parcelId != null) {
            stats.put("total", alertRepository.countByParcelId(parcelId));
            stats.put("active", alertRepository.countByParcelIdAndIsActiveTrue(parcelId));
            stats.put("unacknowledged", alertRepository.countByParcelIdAndIsActiveTrueAndAcknowledgedFalse(parcelId));
        } else {
            stats.put("total", alertRepository.count());
            stats.put("active", alertRepository.countByIsActiveTrue());
            stats.put("unacknowledged", alertRepository.countByIsActiveTrueAndAcknowledgedFalse());
        }

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Alert> searchAlerts(AlertSearchCriteria criteria, Pageable pageable) {
        log.info("Searching alerts with criteria: {}", criteria);

        return alertRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getParcelId() != null) {
                predicates.add(cb.equal(root.get("parcelId"), criteria.getParcelId()));
            }
            if (criteria.getAlertType() != null) {
                predicates.add(cb.equal(root.get("alertType"), criteria.getAlertType()));
            }
            if (criteria.getSeverity() != null) {
                predicates.add(cb.equal(root.get("severity"), criteria.getSeverity()));
            }
            if (criteria.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), criteria.getIsActive()));
            }
            if (criteria.getAcknowledged() != null) {
                predicates.add(cb.equal(root.get("acknowledged"), criteria.getAcknowledged()));
            }
            if (criteria.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("alertTime"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("alertTime"), criteria.getEndDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    @Override
    @Transactional(readOnly = true)
    public List<Alert> getUnacknowledgedAlerts() {
        log.info("Getting unacknowledged alerts");
        return alertRepository.findUnacknowledgedAlerts();
    }

    @Override
    public Alert acknowledgeAlert(Long alertId, String acknowledgedBy) {
        log.info("Acknowledging alert: {} by: {}", alertId, acknowledgedBy);

        Alert alert = getAlertById(alertId);

        if (alert.getAcknowledged()) {
            log.warn("Alert {} is already acknowledged", alertId);
            return alert;
        }

        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(acknowledgedBy);
        alert.setAcknowledgedAt(LocalDateTime.now());

        Alert updatedAlert = alertRepository.save(alert);

        // Record in history
        recordHistory(alertId, "ACKNOWLEDGED", acknowledgedBy, "Alert acknowledged");

        log.info("Alert {} acknowledged successfully", alertId);
        return updatedAlert;
    }

    @Override
    @Transactional
    public List<Alert> acknowledgeMultipleAlerts(List<Long> alertIds, String acknowledgedBy) {
        log.info("Bulk acknowledging {} alerts by: {}", alertIds.size(), acknowledgedBy);

        List<Alert> acknowledgedAlerts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Long alertId : alertIds) {
            try {
                Alert alert = alertRepository.findById(alertId).orElse(null);
                if (alert != null && !alert.getAcknowledged()) {
                    alert.setAcknowledged(true);
                    alert.setAcknowledgedBy(acknowledgedBy);
                    alert.setAcknowledgedAt(now);
                    acknowledgedAlerts.add(alertRepository.save(alert));
                    recordHistory(alertId, "ACKNOWLEDGED", acknowledgedBy, "Bulk acknowledgement");
                }
            } catch (Exception e) {
                log.error("Failed to acknowledge alert {}: {}", alertId, e.getMessage());
            }
        }

        log.info("Successfully acknowledged {} out of {} alerts", acknowledgedAlerts.size(), alertIds.size());
        return acknowledgedAlerts;
    }

    @Override
    public void dismissAlert(Long alertId, String dismissedBy) {
        log.info("Dismissing alert: {} by: {}", alertId, dismissedBy);

        Alert alert = getAlertById(alertId);
        alert.setIsActive(false);
        alertRepository.save(alert);

        // Record in history
        recordHistory(alertId, "DISMISSED", dismissedBy, "Alert dismissed");

        log.info("Alert {} dismissed successfully", alertId);
    }

    @Override
    public void expireOldAlerts() {
        log.info("Expiring old alerts");

        List<Alert> expiredAlerts = alertRepository.findExpiredAlerts(LocalDateTime.now());

        for (Alert alert : expiredAlerts) {
            alert.setIsActive(false);
            alertRepository.save(alert);
            recordHistory(alert.getId(), "EXPIRED", "SYSTEM", "Alert expired automatically");
        }

        log.info("Expired {} alerts", expiredAlerts.size());
    }

    @Override
    public Long createSubscription(String userId, Long parcelId, List<String> alertTypes,
                                   String notificationMethod, String email, String phoneNumber) {
        log.info("Creating subscription for user: {}, parcel: {}", userId, parcelId);

        AlertSubscription subscription = new AlertSubscription();
        subscription.setUserId(userId);
        subscription.setParcelId(parcelId);
        subscription.setAlertTypes(String.join(",", alertTypes));
        subscription.setNotificationMethod(AlertSubscription.NotificationMethod.valueOf(notificationMethod));
        subscription.setEmail(email);
        subscription.setPhoneNumber(phoneNumber);
        subscription.setIsEnabled(true);

        AlertSubscription saved = subscriptionRepository.save(subscription);

        log.info("Subscription created with ID: {}", saved.getId());
        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public AlertSubscription getSubscription(String userId, Long parcelId) {
        log.info("Getting subscription for user: {}, parcel: {}", userId, parcelId);

        if (parcelId != null) {
            return subscriptionRepository.findByUserIdAndParcelId(userId, parcelId)
                    .orElse(null);
        } else {
            List<AlertSubscription> subscriptions = subscriptionRepository.findByUserId(userId);
            return subscriptions.isEmpty() ? null : subscriptions.get(0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertSubscription> getSubscriptionsForParcel(Long parcelId) {
        log.info("Getting subscriptions for parcel: {}", parcelId);
        return subscriptionRepository.findByParcelIdAndIsEnabledTrue(parcelId);
    }

    @Override
    public void notifySubscribers(Alert alert) {
        log.info("Notifying subscribers for alert: {}", alert.getId());

        List<AlertSubscription> subscriptions;

        if (alert.getParcelId() != null) {
            subscriptions = subscriptionRepository.findEnabledSubscriptionsForParcel(alert.getParcelId());
        } else {
            subscriptions = subscriptionRepository.findByIsEnabledTrue();
        }

        // Filter subscriptions by alert type
        String alertTypeName = alert.getAlertType().name();
        subscriptions = subscriptions.stream()
                .filter(sub -> sub.getAlertTypes() == null ||
                        sub.getAlertTypes().isEmpty() ||
                        sub.getAlertTypes().contains(alertTypeName))
                .collect(Collectors.toList());

        // Send notifications asynchronously with retry
        subscriptions.forEach(subscription ->
                CompletableFuture.runAsync(() ->
                        sendNotification(subscription, alert)
                )
        );

        log.info("Queued notifications for {} subscribers", subscriptions.size());
    }
    @Override
    @Transactional(readOnly = true)
    public long countUnacknowledgedAlerts() {
        return alertRepository.countByIsActiveTrueAndAcknowledgedFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnacknowledgedAlertsByParcel(Long parcelId) {
        return alertRepository.countByParcelIdAndIsActiveTrueAndAcknowledgedFalse(parcelId);
    }

    @Override
    public Map<String, Object> getServiceHealth() {
        Map<String, Object> health = new HashMap<>();

        try {
            long activeAlerts = alertRepository.countByIsActiveTrue();
            long unacknowledged = alertRepository.countByIsActiveTrueAndAcknowledgedFalse();
            long subscriptions = subscriptionRepository.countByIsEnabledTrue();

            health.put("status", "UP");
            health.put("activeAlerts", activeAlerts);
            health.put("unacknowledgedAlerts", unacknowledged);
            health.put("activeSubscriptions", subscriptions);
            health.put("timestamp", LocalDateTime.now());
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
        }

        return health;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAlertTrends(Long parcelId, int days) {
        log.info("Getting alert trends for parcel: {}, days: {}", parcelId, days);

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Alert> alerts;

        if (parcelId != null) {
            alerts = alertRepository.findByParcelIdAndAlertTimeAfter(parcelId, startDate);
        } else {
            alerts = alertRepository.findByAlertTimeAfter(startDate);
        }

        Map<String, Object> trends = new HashMap<>();
        trends.put("totalAlerts", alerts.size());
        trends.put("byType", alerts.stream()
                .collect(Collectors.groupingBy(Alert::getAlertType, Collectors.counting())));
        trends.put("bySeverity", alerts.stream()
                .collect(Collectors.groupingBy(Alert::getSeverity, Collectors.counting())));
        trends.put("acknowledgedRate", calculateAcknowledgementRate(alerts));
        trends.put("avgResponseTime", calculateAverageResponseTime(alerts));

        return trends;
    }

    // Helper methods

    private void recordHistory(Long alertId, String action, String performedBy, String notes) {
        AlertHistory history = new AlertHistory();
        history.setAlertId(alertId);
        history.setAction(action);
        history.setPerformedBy(performedBy);
        history.setNotes(notes);
        historyRepository.save(history);
    }

    private void sendNotification(AlertSubscription subscription, Alert alert) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                sendNotification(subscription, alert);
                log.info("Notification sent successfully on attempt {}", attempts + 1);
                return;
            } catch (Exception e) {
                lastException = e;
                attempts++;
                log.warn("Notification attempt {} failed: {}", attempts, e.getMessage());

                if (attempts < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        log.error("Failed to send notification after {} attempts", MAX_RETRY_ATTEMPTS, lastException);
    }

    private double calculateAcknowledgementRate(List<Alert> alerts) {
        if (alerts.isEmpty()) return 0.0;
        long acknowledged = alerts.stream().filter(Alert::getAcknowledged).count();
        return (double) acknowledged / alerts.size() * 100;
    }

    private long calculateAverageResponseTime(List<Alert> alerts) {
        List<Long> responseTimes = alerts.stream()
                .filter(a -> a.getAcknowledged() && a.getAcknowledgedAt() != null)
                .map(a -> java.time.Duration.between(a.getAlertTime(), a.getAcknowledgedAt()).toMinutes())
                .toList();

        if (responseTimes.isEmpty()) return 0;
        return (long) responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
    }
}
