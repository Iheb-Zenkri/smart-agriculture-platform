package com.smartagri.alert.scheduler;

import com.smartagri.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final AlertService alertService;

    /**
     * Expire old alerts every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void expireOldAlerts() {
        log.info("Running scheduled task: Expire old alerts");
        try {
            alertService.expireOldAlerts();
        } catch (Exception e) {
            log.error("Error in scheduled task expireOldAlerts: {}", e.getMessage());
        }
    }

    /**
     * Log unacknowledged alerts statistics every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void logAlertStatistics() {
        log.info("Running scheduled task: Log alert statistics");
        try {
            long unacknowledgedCount = alertService.countUnacknowledgedAlerts();
            log.info("Alert Statistics - Unacknowledged alerts: {}", unacknowledgedCount);
        } catch (Exception e) {
            log.error("Error in scheduled task logAlertStatistics: {}", e.getMessage());
        }
    }
}
