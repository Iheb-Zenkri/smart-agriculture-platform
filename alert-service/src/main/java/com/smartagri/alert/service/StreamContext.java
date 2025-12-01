package com.smartagri.alert.service;

import com.smartagri.alert.grpc.generated.AlertResponse;
import com.smartagri.alert.grpc.generated.StreamAlertsRequest;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Context holder for active alert streams
 * Manages lifecycle and state of streaming connections
 */
@Slf4j
@Getter
public class StreamContext {

    private final StreamObserver<AlertResponse> observer;
    private final StreamAlertsRequest request;
    private final ScheduledFuture<?> future;
    private final LocalDateTime startTime;
    private final AtomicLong messagesSent;
    private final AtomicBoolean isCancelled;
    private final String clientId;

    public StreamContext(StreamObserver<AlertResponse> observer,
                         StreamAlertsRequest request,
                         ScheduledFuture<?> future,
                         String clientId) {
        this.observer = observer;
        this.request = request;
        this.future = future;
        this.clientId = clientId;
        this.startTime = LocalDateTime.now();
        this.messagesSent = new AtomicLong(0);
        this.isCancelled = new AtomicBoolean(false);
    }

    public void incrementMessageCount() {
        messagesSent.incrementAndGet();
    }

    public boolean cancel() {
        if (isCancelled.compareAndSet(false, true)) {
            boolean cancelled = future.cancel(true);
            log.info("Stream cancelled for client: {} - Messages sent: {}, Duration: {} seconds",
                    clientId, messagesSent.get(),
                    java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds());
            return cancelled;
        }
        return false;
    }

    public boolean isActive() {
        return !isCancelled.get() && !future.isCancelled() && !future.isDone();
    }

    public Long getParcelId() {
        return request.hasParcelId() ? request.getParcelId() : null;
    }
}