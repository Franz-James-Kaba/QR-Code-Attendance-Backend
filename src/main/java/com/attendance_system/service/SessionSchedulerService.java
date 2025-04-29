package com.attendance_system.service;

import com.attendance_system.model.Session;
import com.attendance_system.repository.SessionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionSchedulerService {
    private final TaskScheduler taskScheduler;
    private final SessionRepository sessionRepository;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void scheduleSessionInvalidation(Session session) {
        Date endTime = Date.from(session.getEndTime()
                .atZone(ZoneId.systemDefault())
                .toInstant());

        Runnable task = () -> invalidateSession(session.getId());
        String taskKey = "invalidate-" + session.getId();

        // Cancel existing task if present
        cancelExistingTask(taskKey);

        // Schedule new task
        scheduledTasks.put(taskKey, taskScheduler.schedule(task, endTime));
        log.info("Scheduled invalidation for session {} at {}", session.getId(), session.getEndTime());
    }

    public void scheduleSessionActivation(Session session) {
        Date startTime = Date.from(session.getStartTime()
                .atZone(ZoneId.systemDefault())
                .toInstant());

        Runnable task = () -> activateSession(session.getId());
        String taskKey = "activate-" + session.getId();

        // Cancel existing task if present
        cancelExistingTask(taskKey);

        // Schedule new task
        scheduledTasks.put(taskKey, taskScheduler.schedule(task, startTime));
        log.info("Scheduled activation for session {} at {}", session.getId(), session.getStartTime());
    }

    private void cancelExistingTask(String taskKey) {
        ScheduledFuture<?> existingTask = scheduledTasks.get(taskKey);
        if (existingTask != null && !existingTask.isDone() && !existingTask.isCancelled()) {
            existingTask.cancel(false);
            log.info("Cancelled existing task: {}", taskKey);
        }
    }

    private void invalidateSession(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null && session.isActive()) {
            session.setActive(false);
            sessionRepository.save(session);
            log.info("Session {} invalidated at endTime.", session.getId());

            // Remove the task from the map
            scheduledTasks.remove("invalidate-" + sessionId);
        }
    }

    private void activateSession(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session != null && !session.isActive()) {
            session.setActive(true);
            sessionRepository.save(session);
            log.info("Session {} activated at startTime.", session.getId());

            // Remove the task from the map
            scheduledTasks.remove("activate-" + sessionId);

            // Schedule invalidation after activation
            scheduleSessionInvalidation(session);
        }
    }

    @PostConstruct
    public void rescheduleAllSessions() {
        List<Session> allSessions = sessionRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        log.info("Rescheduling {} sessions at startup", allSessions.size());

        for (Session session : allSessions) {
            // For sessions that haven't started yet
            if (!session.isActive() && session.getStartTime().isAfter(now)) {
                scheduleSessionActivation(session);
            }

            // For active sessions that haven't ended yet
            if (session.isActive() && session.getEndTime().isAfter(now)) {
                scheduleSessionInvalidation(session);
            }

            // For sessions that should be active now (started but not ended)
            if (!session.isActive() && session.getStartTime().isBefore(now) && session.getEndTime().isAfter(now)) {
                log.info("Session {} should be active now, activating", session.getId());
                session.setActive(true);
                sessionRepository.save(session);
                scheduleSessionInvalidation(session);
            }

            // For sessions that should be inactive now (ended)
            if (session.isActive() && session.getEndTime().isBefore(now)) {
                log.info("Session {} has ended, deactivating", session.getId());
                session.setActive(false);
                sessionRepository.save(session);
            }
        }
    }
}
