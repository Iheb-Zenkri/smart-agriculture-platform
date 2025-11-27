package com.smartagri.alert.service;

import com.smartagri.alert.exception.AlertNotFoundException;
import com.smartagri.alert.model.Alert;
import com.smartagri.alert.model.AlertHistory;
import com.smartagri.alert.model.AlertSubscription;
import com.smartagri.alert.repository.AlertHistoryRepository;
import com.smartagri.alert.repository.AlertRepository;
import com.smartagri.alert.repository.AlertSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final AlertSubscriptionRepository subscriptionRepository;
    private final AlertHistoryRepository historyRepository;

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

        // Send notifications
        for (AlertSubscription subscription : subscriptions) {
            sendNotification(subscription, alert);
        }

        log.info("Notified {} subscribers", subscriptions.size());
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
        // In production, this would send actual notifications
        log.info("Sending {} notification to user {} for alert {}",
                subscription.getNotificationMethod(),
                subscription.getUserId(),
                alert.getId());

        switch (subscription.getNotificationMethod()) {
            case EMAIL -> sendEmailNotification(subscription.getEmail(), alert);
            case SMS -> sendSmsNotification(subscription.getPhoneNumber(), alert);
            case PUSH -> sendPushNotification(subscription.getUserId(), alert);
            case IN_APP -> sendInAppNotification(subscription.getUserId(), alert);
            case ALL -> {
                sendEmailNotification(subscription.getEmail(), alert);
                sendPushNotification(subscription.getUserId(), alert);
            }
        }
    }

    private void sendEmailNotification(String email, Alert alert) {
        // Email sending implementation
        log.info("Email notification sent to: {} for alert: {}", email, alert.getId());
    }

    private void sendSmsNotification(String phoneNumber, Alert alert) {
        // SMS sending implementation
        log.info("SMS notification sent to: {} for alert: {}", phoneNumber, alert.getId());
    }

    private void sendPushNotification(String userId, Alert alert) {
        // Push notification implementation
        log.info("Push notification sent to user: {} for alert: {}", userId, alert.getId());
    }

    private void sendInAppNotification(String userId, Alert alert) {
        // In-app notification implementation
        log.info("In-app notification sent to user: {} for alert: {}", userId, alert.getId());
    }
}
