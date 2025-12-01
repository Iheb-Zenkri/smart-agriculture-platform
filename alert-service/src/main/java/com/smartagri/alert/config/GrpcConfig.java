package com.smartagri.alert.config;

import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC Configuration
 */
@Configuration
@Slf4j
public class GrpcConfig {

    /**
     * Global gRPC interceptor for logging
     */
    @GrpcGlobalServerInterceptor
    public ServerInterceptor loggingInterceptor() {
        return new LoggingServerInterceptor();
    }

    /**
     * Custom logging interceptor
     */
    private static class LoggingServerInterceptor implements ServerInterceptor {

        @Override
        public <ReqT, RespT> io.grpc.ServerCall.Listener<ReqT> interceptCall(
                io.grpc.ServerCall<ReqT, RespT> call,
                io.grpc.Metadata headers,
                io.grpc.ServerCallHandler<ReqT, RespT> next) {

            String methodName = call.getMethodDescriptor().getFullMethodName();
            log.info("gRPC Request: {}", methodName);

            return next.startCall(new io.grpc.ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                @Override
                public void close(io.grpc.Status status, io.grpc.Metadata trailers) {
                    log.info("gRPC Response: {} - Status: {}", methodName, status.getCode());
                    super.close(status, trailers);
                }
            }, headers);
        }
    }
}