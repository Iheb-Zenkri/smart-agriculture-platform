package com.smartagri.alert.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Metrics collection for Alert Service
 * Integrates with Prometheus via Micrometer
 */
@Component
@Slf4j
public class AlertMetrics {

    private final MeterRegistry registry;
    private final AtomicInteger activeStreams = new AtomicInteger(0);
    private final AtomicInteger activeAlerts = new AtomicInteger(0);
    private final AtomicInteger unacknowledgedAlerts = new AtomicInteger(0);

    public AlertMetrics(MeterRegistry registry) {
        this.registry = registry;
        initGauges();
    }

    private void initGauges() {
        Gauge.builder("alerts.streams.active", activeStreams, AtomicInteger::get)
                .description("Number of active alert streams")
                .register(registry);

        Gauge.builder("alerts.active.count", activeAlerts, AtomicInteger::get)
                .description("Number of active alerts")
                .register(registry);

        Gauge.builder("alerts.unacknowledged.count", unacknowledgedAlerts, AtomicInteger::get)
                .description("Number of unacknowledged alerts")
                .register(registry);
    }

    // ==================== Alert Metrics ====================

    public void recordAlertCreated(String type, String severity) {
        Counter.builder("alerts.created")
                .tag("type", type)
                .tag("severity", severity)
                .description("Number of alerts created")
                .register(registry)
                .increment();

        log.debug("Alert created metric recorded: type={}, severity={}", type, severity);
    }

    public void recordAlertAcknowledged(String type, String severity) {
        Counter.builder("alerts.acknowledged")
                .tag("type", type)
                .tag("severity", severity)
                .description("Number of alerts acknowledged")
                .register(registry)
                .increment();
    }

    public void recordAlertDismissed(String type, String severity) {
        Counter.builder("alerts.dismissed")
                .tag("type", type)
                .tag("severity", severity)
                .description("Number of alerts dismissed")
                .register(registry)
                .increment();
    }

    public void recordAlertExpired() {
        Counter.builder("alerts.expired")
                .description("Number of alerts expired automatically")
                .register(registry)
                .increment();
    }

    public void updateActiveAlerts(int count) {
        activeAlerts.set(count);
    }

    public void updateUnacknowledgedAlerts(int count) {
        unacknowledgedAlerts.set(count);
    }

    // ==================== Stream Metrics ====================

    public void streamStarted() {
        activeStreams.incrementAndGet();
        Counter.builder("alerts.streams.started")
                .description("Number of streams started")
                .register(registry)
                .increment();
    }

    public void streamEnded(long durationSeconds, long messagesSent) {
        activeStreams.decrementAndGet();

        Counter.builder("alerts.streams.ended")
                .description("Number of streams ended")
                .register(registry)
                .increment();

        Counter.builder("alerts.stream.messages.sent")
                .description("Total messages sent in streams")
                .register(registry)
                .increment(messagesSent);

        Timer.builder("alerts.stream.duration")
                .description("Duration of alert streams")
                .register(registry)
                .record(java.time.Duration.ofSeconds(durationSeconds));
    }

    public void streamError(String errorType) {
        Counter.builder("alerts.streams.errors")
                .tag("error_type", errorType)
                .description("Number of stream errors")
                .register(registry)
                .increment();
    }

    // ==================== Notification Metrics ====================

    public void recordNotificationSent(String method, boolean success) {
        Counter.builder("alerts.notifications.sent")
                .tag("method", method)
                .tag("success", String.valueOf(success))
                .description("Number of notifications sent")
                .register(registry)
                .increment();
    }

    public void recordNotificationRetry(String method, int attemptNumber) {
        Counter.builder("alerts.notifications.retries")
                .tag("method", method)
                .tag("attempt", String.valueOf(attemptNumber))
                .description("Number of notification retry attempts")
                .register(registry)
                .increment();
    }

    public void recordNotificationFailed(String method) {
        Counter.builder("alerts.notifications.failed")
                .tag("method", method)
                .description("Number of failed notifications")
                .register(registry)
                .increment();
    }

    // ==================== Subscription Metrics ====================

    public void recordSubscriptionCreated(String notificationMethod) {
        Counter.builder("alerts.subscriptions.created")
                .tag("method", notificationMethod)
                .description("Number of subscriptions created")
                .register(registry)
                .increment();
    }

    public void recordSubscriptionDeleted() {
        Counter.builder("alerts.subscriptions.deleted")
                .description("Number of subscriptions deleted")
                .register(registry)
                .increment();
    }

    // ==================== Performance Metrics ====================

    public void recordGrpcRequestDuration(String method, long durationMillis, boolean success) {
        Timer.builder("alerts.grpc.request.duration")
                .tag("method", method)
                .tag("success", String.valueOf(success))
                .description("Duration of gRPC requests")
                .register(registry)
                .record(java.time.Duration.ofMillis(durationMillis));
    }

    public void recordDatabaseQueryDuration(String operation, long durationMillis) {
        Timer.builder("alerts.database.query.duration")
                .tag("operation", operation)
                .description("Duration of database queries")
                .register(registry)
                .record(java.time.Duration.ofMillis(durationMillis));
    }

    // ==================== Error Metrics ====================

    public void recordError(String errorType, String operation) {
        Counter.builder("alerts.errors")
                .tag("type", errorType)
                .tag("operation", operation)
                .description("Number of errors")
                .register(registry)
                .increment();
    }

    public void recordValidationError(String field) {
        Counter.builder("alerts.validation.errors")
                .tag("field", field)
                .description("Number of validation errors")
                .register(registry)
                .increment();
    }
}