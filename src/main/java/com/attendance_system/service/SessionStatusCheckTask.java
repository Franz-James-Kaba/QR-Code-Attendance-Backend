package com.attendance_system.service;

import com.attendance_system.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Component
@Slf4j
public class SessionStatusCheckTask {
    private final int checkRate;
    private final int cleanupRate;
    private final SessionRepository repository;

    public SessionStatusCheckTask(
            @Value("${check.rate}") int checkRate,
            @Value("${cleanup.rate}") int cleanupRate,
            SessionRepository repository) {
        this.checkRate = checkRate;
        this.cleanupRate = cleanupRate;
        this.repository = repository;
    }

    @Scheduled(fixedDelayString = "${check.rate}")
    @Transactional
    public void updateSessionStatuses() {
        try {
            LocalDateTime now = LocalDateTime.now();
            repository.updateStatusForExpiredSessions(now, false);
            repository.updateStatusForEligibleSessions(now, true);
            log.info("Session statuses updated successfully at {}", now);
        } catch (Exception e) {
            log.error("Failed to update session statuses", e);
        }
    }

    @Scheduled(fixedDelayString = "${cleanup.rate}")
    @Transactional
    public void cleanupExpiredSessions() {
        try {
            LocalDateTime now = LocalDateTime.now();
            long expiredCount = repository.countExpiredSessions(now);
            repository.deleteExpiredSessions(now);
            log.info("Cleaned up {} expired sessions at {}", expiredCount, now);
        } catch (Exception e) {
            log.error("Failed to cleanup expired sessions", e);
        }
    }
}