package com.attendance_system.controller;

import com.attendance_system.response.MetricsResponse;
import com.attendance_system.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/nsp")
@PreAuthorize("hasAnyRole('FACILITATOR', 'NSP', 'ADMIN')")
@RequiredArgsConstructor
public class MetricsController {
    private final MetricsService metricsService;

    @GetMapping("/average-check-in-time")
    public ResponseEntity<MetricsResponse> getAverageCheckInTime() {
        return ResponseEntity.ok(metricsService.getAverageCheckInTime());
    }

    @GetMapping("/average-check-out-time")
    public ResponseEntity<MetricsResponse> getAverageCheckOutTime() {
        return ResponseEntity.ok(metricsService.getAverageCheckOutTime());
    }
}
