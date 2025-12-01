package com.smartagri.alert.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Metrics Configuration for Prometheus
 */
@Configuration
@Slf4j
public class MetricsConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Customize meter registry with common tags
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            log.info("Configuring metrics with application name: {}", applicationName);
            registry.config().commonTags(
                    Arrays.asList(
                            Tag.of("application", applicationName),
                            Tag.of("service", "alert-service")
                    )
            );
        };
    }

    /**
     * Enable @Timed annotation support
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Register JVM metrics
     */
    @Bean
    public JvmMemoryMetrics jvmMemoryMetrics() {
        log.info("Registering JVM memory metrics");
        return new JvmMemoryMetrics();
    }

    @Bean
    public JvmGcMetrics jvmGcMetrics() {
        log.info("Registering JVM GC metrics");
        return new JvmGcMetrics();
    }

    @Bean
    public JvmThreadMetrics jvmThreadMetrics() {
        log.info("Registering JVM thread metrics");
        return new JvmThreadMetrics();
    }

    @Bean
    public ProcessorMetrics processorMetrics() {
        log.info("Registering processor metrics");
        return new ProcessorMetrics();
    }

    @Bean
    public UptimeMetrics uptimeMetrics() {
        log.info("Registering uptime metrics");
        return new UptimeMetrics();
    }
}