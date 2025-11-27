package com.smartagri.alert.service;

import com.smartagri.alert.grpc.generated.*;
import com.smartagri.alert.model.Alert;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class AlertGrpcService extends AlertServiceGrpc.AlertServiceImplBase {

    private final AlertService alertService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void streamAlerts(StreamAlertsRequest request, StreamObserver<AlertResponse> responseObserver) {
        log.info("gRPC: StreamAlerts called for parcelId: {}", request.hasParcelId() ? request.getParcelId() : "ALL");

        // Stream alerts every 5 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<Alert> alerts;
                if (request.hasParcelId()) {
                    alerts = alertService.getActiveAlertsByParcel(request.getParcelId());
                } else {
                    alerts = alertService.getActiveAlerts();
                }

                for (Alert alert : alerts) {
                    AlertResponse response = convertToGrpcResponse(alert);
                    responseObserver.onNext(response);
                }
            } catch (Exception e) {
                log.error("Error streaming alerts: {}", e.getMessage());
                responseObserver.onError(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void createAlert(CreateAlertRequest request, StreamObserver<AlertResponse> responseObserver) {
        log.info("gRPC: CreateAlert called - type: {}, severity: {}", request.getAlertType(), request.getSeverity());

        try {
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

        } catch (Exception e) {
            log.error("Error creating alert: {}", e.getMessage());
            responseObserver.onError(e);
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

        } catch (Exception e) {
            log.error("Error getting alert: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void getActiveAlerts(GetActiveAlertsRequest request, StreamObserver<AlertListResponse> responseObserver) {
        log.info("gRPC: GetActiveAlerts called");

        try {
            List<Alert> alerts;

            if (request.hasParcelId()) {
                alerts = alertService.getActiveAlertsByParcel(request.getParcelId());
            } else if (request.hasAlertType()) {
                alerts = alertService.getActiveAlertsByType(Alert.AlertType.valueOf(request.getAlertType()));
            } else if (request.hasSeverity()) {
                alerts = alertService.getActiveAlertsBySeverity(Alert.AlertSeverity.valueOf(request.getSeverity()));
            } else {
                alerts = alertService.getActiveAlerts();
            }

            AlertListResponse.Builder builder = AlertListResponse.newBuilder()
                    .setTotalCount(alerts.size());

            for (Alert alert : alerts) {
                builder.addAlerts(convertToGrpcResponse(alert));
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error getting active alerts: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void acknowledgeAlert(AcknowledgeAlertRequest request, StreamObserver<AlertResponse> responseObserver) {
        log.info("gRPC: AcknowledgeAlert called for ID: {} by: {}",
                request.getAlertId(), request.getAcknowledgedBy());

        try {
            Alert alert = alertService.acknowledgeAlert(request.getAlertId(), request.getAcknowledgedBy());
            AlertResponse response = convertToGrpcResponse(alert);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error acknowledging alert: {}", e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void dismissAlert(DismissAlertRequest request, StreamObserver<DismissAlertResponse> responseObserver) {
        log.info("gRPC: DismissAlert called for ID: {} by: {}",
                request.getAlertId(), request.getDismissedBy());

        try {
            alertService.dismissAlert(request.getAlertId(), request.getDismissedBy());

            DismissAlertResponse response = DismissAlertResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Alert dismissed successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error dismissing alert: {}", e.getMessage());

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

        } catch (Exception e) {
            log.error("Error creating subscription: {}", e.getMessage());

            SubscribeResponse response = SubscribeResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
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
}