package com.smartagri.alert.service;

import com.smartagri.alert.grpc.generated.*;
import com.smartagri.alert.model.Alert;
import com.smartagri.alert.model.AlertSubscription;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@GrpcService
@Slf4j
public class AlertGrpcService extends AlertServiceGrpc.AlertServiceImplBase {

    private final AlertService alertService;
    private final ScheduledExecutorService scheduler;
    private final Map<StreamObserver<AlertResponse>, ScheduledFuture<?>> activeStreams;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final int STREAM_INTERVAL_SECONDS = 5;
    private static final int SCHEDULER_POOL_SIZE = 10;

    @Autowired
    public AlertGrpcService(AlertService alertService) {
        this.alertService = alertService;
        this.scheduler = Executors.newScheduledThreadPool(SCHEDULER_POOL_SIZE);
        this.activeStreams = new ConcurrentHashMap<>();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down AlertGrpcService scheduler");

        // Cancel all active streams
        activeStreams.values().forEach(future -> future.cancel(true));
        activeStreams.clear();

        // Shutdown scheduler
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void streamAlerts(StreamAlertsRequest request, StreamObserver<AlertResponse> responseObserver) {
        log.info("gRPC: StreamAlerts called for parcelId: {}",
                request.hasParcelId() ? request.getParcelId() : "ALL");

        // Schedule periodic alert streaming
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                List<Alert> alerts = fetchAlerts(request);

                if (alerts.isEmpty()) {
                    log.debug("No alerts to stream");
                    return;
                }

                for (Alert alert : alerts) {
                    if (Thread.currentThread().isInterrupted()) {
                        log.info("Stream interrupted, stopping alert streaming");
                        return;
                    }

                    AlertResponse response = convertToGrpcResponse(alert);
                    responseObserver.onNext(response);
                }

                log.debug("Streamed {} alerts", alerts.size());

            } catch (Exception e) {
                log.error("Error streaming alerts: {}", e.getMessage(), e);
                cleanupStream(responseObserver);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Error streaming alerts: " + e.getMessage())
                        .asException());
            }
        }, 0, STREAM_INTERVAL_SECONDS, TimeUnit.SECONDS);

        // Track the stream for cleanup
        activeStreams.put(responseObserver, future);

        log.info("Alert streaming started. Active streams: {}", activeStreams.size());
    }

    @Override
    public void createAlert(CreateAlertRequest request, StreamObserver<AlertResponse> responseObserver) {
        log.info("gRPC: CreateAlert called - type: {}, severity: {}",
                request.getAlertType(), request.getSeverity());

        try {
            // Validate request
            validateCreateAlertRequest(request);

            Alert alert = alertService.createAlert(
                    Alert.AlertType.valueOf(request.getAlertType()),
                    Alert.AlertSeverity.valueOf(request.getSeverity()),
                    request.hasParcelId() ? request.getParcelId() : null,
                    request.hasLocation() ? request.getLocation() : null,
                    request.getTitle(),
                    request.getMessage(),
                    request.hasExpirySeconds() ? request.getExpirySeconds() : null,
                    request.hasMetadata() ? request.getMetadata() : null
            );

            AlertResponse response = convertToGrpcResponse(alert);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Alert created successfully with ID: {}", alert.getId());

        } catch (IllegalArgumentException e) {
            log.error("Invalid alert data: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asException());
        } catch (Exception e) {
            log.error("Error creating alert: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to create alert: " + e.getMessage())
                    .asException());
        }
    }

    @Override
    public void getAlert(GetAlertRequest request, StreamObserver<AlertResponse> responseObserver) {
        log.info("gRPC: GetAlert called for ID: {}", request.getAlertId());

        try {
            Alert alert = alertService.getAlertById(request.getAlertId());
            AlertResponse response = convertToGrpcResponse(alert);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (com.smartagri.alert.exception.AlertNotFoundException e) {
            log.warn("Alert not found: {}", request.getAlertId());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Alert not found with ID: " + request.getAlertId())
                    .asException());
        } catch (Exception e) {
            log.error("Error getting alert: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to retrieve alert: " + e.getMessage())
                    .asException());
        }
    }

    @Override
    public void getActiveAlerts(GetActiveAlertsRequest request, StreamObserver<AlertListResponse> responseObserver) {
        log.info("gRPC: GetActiveAlerts called with filters - parcelId: {}, type: {}, severity: {}",
                request.hasParcelId() ? request.getParcelId() : "N/A",
                request.hasAlertType() ? request.getAlertType() : "N/A",
                request.hasSeverity() ? request.getSeverity() : "N/A");

        try {
            List<Alert> alerts = fetchActiveAlerts(request);

            // Apply limit if specified
            if (request.hasLimit() && request.getLimit() > 0) {
                alerts = alerts.stream()
                        .limit(request.getLimit())
                        .toList();
            }

            AlertListResponse.Builder builder = AlertListResponse.newBuilder()
                    .setTotalCount(alerts.size());

            for (Alert alert : alerts) {
                builder.addAlerts(convertToGrpcResponse(alert));
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

            log.info("Returned {} active alerts", alerts.size());

        } catch (IllegalArgumentException e) {
            log.error("Invalid request parameters: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asException());
        } catch (Exception e) {
            log.error("Error getting active alerts: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to retrieve alerts: " + e.getMessage())
                    .asException());
        }
    }

    @Override
    public void acknowledgeAlert(AcknowledgeAlertRequest request, StreamObserver<AlertResponse> responseObserver) {
        log.info("gRPC: AcknowledgeAlert called for ID: {} by: {}",
                request.getAlertId(), request.getAcknowledgedBy());

        try {
            validateAcknowledgeRequest(request);

            Alert alert = alertService.acknowledgeAlert(request.getAlertId(), request.getAcknowledgedBy());
            AlertResponse response = convertToGrpcResponse(alert);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Alert {} acknowledged successfully", request.getAlertId());

        } catch (com.smartagri.alert.exception.AlertNotFoundException e) {
            log.warn("Alert not found for acknowledgement: {}", request.getAlertId());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Alert not found with ID: " + request.getAlertId())
                    .asException());
        } catch (IllegalArgumentException e) {
            log.error("Invalid acknowledgement request: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asException());
        } catch (Exception e) {
            log.error("Error acknowledging alert: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to acknowledge alert: " + e.getMessage())
                    .asException());
        }
    }

    @Override
    public void dismissAlert(DismissAlertRequest request, StreamObserver<DismissAlertResponse> responseObserver) {
        log.info("gRPC: DismissAlert called for ID: {} by: {}",
                request.getAlertId(), request.getDismissedBy());

        try {
            validateDismissRequest(request);

            alertService.dismissAlert(request.getAlertId(), request.getDismissedBy());

            DismissAlertResponse response = DismissAlertResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Alert dismissed successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Alert {} dismissed successfully", request.getAlertId());

        } catch (com.smartagri.alert.exception.AlertNotFoundException e) {
            log.warn("Alert not found for dismissal: {}", request.getAlertId());

            DismissAlertResponse response = DismissAlertResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Alert not found with ID: " + request.getAlertId())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Invalid dismissal request: {}", e.getMessage());

            DismissAlertResponse response = DismissAlertResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid request: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error dismissing alert: {}", e.getMessage(), e);

            DismissAlertResponse response = DismissAlertResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void subscribeToAlerts(SubscribeRequest request, StreamObserver<SubscribeResponse> responseObserver) {
        log.info("gRPC: SubscribeToAlerts called for user: {}", request.getUserId());

        try {
            validateSubscribeRequest(request);

            Long subscriptionId = alertService.createSubscription(
                    request.getUserId(),
                    request.hasParcelId() ? request.getParcelId() : null,
                    request.getAlertTypesList(),
                    request.getNotificationMethod(),
                    request.hasEmail() ? request.getEmail() : null,
                    request.hasPhoneNumber() ? request.getPhoneNumber() : null
            );

            SubscribeResponse response = SubscribeResponse.newBuilder()
                    .setSubscriptionId(subscriptionId)
                    .setSuccess(true)
                    .setMessage("Subscription created successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Subscription created with ID: {}", subscriptionId);

        } catch (IllegalArgumentException e) {
            log.error("Invalid subscription request: {}", e.getMessage());

            SubscribeResponse response = SubscribeResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid request: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error creating subscription: {}", e.getMessage(), e);

            SubscribeResponse response = SubscribeResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getSubscription(GetSubscriptionRequest request, StreamObserver<SubscriptionResponse> responseObserver) {
        log.info("gRPC: GetSubscription called for user: {}, parcel: {}",
                request.getUserId(), request.hasParcelId() ? request.getParcelId() : "N/A");

        try {
            AlertSubscription subscription = alertService.getSubscription(
                    request.getUserId(),
                    request.hasParcelId() ? request.getParcelId() : null
            );

            if (subscription == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Subscription not found for user: " + request.getUserId())
                        .asException());
                return;
            }

            SubscriptionResponse response = convertToSubscriptionResponse(subscription);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Subscription retrieved for user: {}", request.getUserId());

        } catch (Exception e) {
            log.error("Error getting subscription: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to retrieve subscription: " + e.getMessage())
                    .asException());
        }
    }

    // ==================== Helper Methods ====================

    private List<Alert> fetchAlerts(StreamAlertsRequest request) {
        if (request.hasParcelId()) {
            return alertService.getActiveAlertsByParcel(request.getParcelId());
        }
        // Add support for filtering by alert types and severities if needed
        return alertService.getActiveAlerts();
    }

    private List<Alert> fetchActiveAlerts(GetActiveAlertsRequest request) {
        if (request.hasParcelId()) {
            return alertService.getActiveAlertsByParcel(request.getParcelId());
        } else if (request.hasAlertType()) {
            return alertService.getActiveAlertsByType(Alert.AlertType.valueOf(request.getAlertType()));
        } else if (request.hasSeverity()) {
            return alertService.getActiveAlertsBySeverity(Alert.AlertSeverity.valueOf(request.getSeverity()));
        } else {
            return alertService.getActiveAlerts();
        }
    }

    private void cleanupStream(StreamObserver<AlertResponse> responseObserver) {
        ScheduledFuture<?> future = activeStreams.remove(responseObserver);
        if (future != null) {
            future.cancel(true);
            log.info("Stream cleaned up. Active streams: {}", activeStreams.size());
        }
    }

    private AlertResponse convertToGrpcResponse(Alert alert) {
        AlertResponse.Builder builder = AlertResponse.newBuilder()
                .setId(alert.getId())
                .setAlertType(alert.getAlertType().name())
                .setSeverity(alert.getSeverity().name())
                .setTitle(alert.getTitle())
                .setMessage(alert.getMessage())
                .setAlertTime(alert.getAlertTime().format(FORMATTER))
                .setIsActive(alert.getIsActive())
                .setAcknowledged(alert.getAcknowledged());

        if (alert.getParcelId() != null) {
            builder.setParcelId(alert.getParcelId());
        }
        if (alert.getLocation() != null) {
            builder.setLocation(alert.getLocation());
        }
        if (alert.getExpiryTime() != null) {
            builder.setExpiryTime(alert.getExpiryTime().format(FORMATTER));
        }
        if (alert.getAcknowledgedAt() != null) {
            builder.setAcknowledgedAt(alert.getAcknowledgedAt().format(FORMATTER));
        }
        if (alert.getAcknowledgedBy() != null) {
            builder.setAcknowledgedBy(alert.getAcknowledgedBy());
        }
        if (alert.getMetadata() != null) {
            builder.setMetadata(alert.getMetadata());
        }

        return builder.build();
    }

    private SubscriptionResponse convertToSubscriptionResponse(AlertSubscription subscription) {
        SubscriptionResponse.Builder builder = SubscriptionResponse.newBuilder()
                .setId(subscription.getId())
                .setUserId(subscription.getUserId())
                .setNotificationMethod(subscription.getNotificationMethod().name())
                .setIsEnabled(subscription.getIsEnabled());

        if (subscription.getParcelId() != null) {
            builder.setParcelId(subscription.getParcelId());
        }
        if (subscription.getEmail() != null) {
            builder.setEmail(subscription.getEmail());
        }
        if (subscription.getPhoneNumber() != null) {
            builder.setPhoneNumber(subscription.getPhoneNumber());
        }
        if (subscription.getAlertTypes() != null && !subscription.getAlertTypes().isEmpty()) {
            builder.addAllAlertTypes(List.of(subscription.getAlertTypes().split(",")));
        }

        return builder.build();
    }

    // ==================== Validation Methods ====================

    private void validateCreateAlertRequest(CreateAlertRequest request) {
        if (request.getAlertType() == null || request.getAlertType().isEmpty()) {
            throw new IllegalArgumentException("Alert type is required");
        }
        if (request.getSeverity() == null || request.getSeverity().isEmpty()) {
            throw new IllegalArgumentException("Severity is required");
        }
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Message is required");
        }
    }

    private void validateAcknowledgeRequest(AcknowledgeAlertRequest request) {
        if (request.getAlertId() <= 0) {
            throw new IllegalArgumentException("Valid alert ID is required");
        }
        if (request.getAcknowledgedBy() == null || request.getAcknowledgedBy().isEmpty()) {
            throw new IllegalArgumentException("Acknowledged by field is required");
        }
    }

    private void validateDismissRequest(DismissAlertRequest request) {
        if (request.getAlertId() <= 0) {
            throw new IllegalArgumentException("Valid alert ID is required");
        }
        if (request.getDismissedBy() == null || request.getDismissedBy().isEmpty()) {
            throw new IllegalArgumentException("Dismissed by field is required");
        }
    }

    private void validateSubscribeRequest(SubscribeRequest request) {
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getNotificationMethod() == null || request.getNotificationMethod().isEmpty()) {
            throw new IllegalArgumentException("Notification method is required");
        }

        // Validate notification method-specific requirements
        String method = request.getNotificationMethod();
        if ("EMAIL".equals(method) && (!request.hasEmail() || request.getEmail().isEmpty())) {
            throw new IllegalArgumentException("Email is required for EMAIL notification method");
        }
        if ("SMS".equals(method) && (!request.hasPhoneNumber() || request.getPhoneNumber().isEmpty())) {
            throw new IllegalArgumentException("Phone number is required for SMS notification method");
        }
    }
}