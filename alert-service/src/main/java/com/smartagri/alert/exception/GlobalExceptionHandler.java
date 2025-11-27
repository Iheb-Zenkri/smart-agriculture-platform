package com.smartagri.alert.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
@Slf4j
public class GlobalExceptionHandler {

    @GrpcExceptionHandler(AlertNotFoundException.class)
    public StatusRuntimeException handleAlertNotFoundException(AlertNotFoundException e) {
        log.error("Alert not found: {}", e.getMessage());
        return Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(AlertServiceException.class)
    public StatusRuntimeException handleAlertServiceException(AlertServiceException e) {
        log.error("Alert service error: {}", e.getMessage());
        return Status.INTERNAL
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return Status.INTERNAL
                .withDescription("An unexpected error occurred")
                .asRuntimeException();
    }
}