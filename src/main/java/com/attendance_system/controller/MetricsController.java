package com.attendance_system.controller;

import com.attendance_system.response.MetricsResponse;
import com.attendance_system.response.UserResponse;
import com.attendance_system.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/metrics")
@PreAuthorize("hasAnyRole('FACILITATOR', 'NSP', 'ADMIN')")
@RequiredArgsConstructor
public class MetricsController {
    private final MetricsService metricsService;

    @GetMapping("/average-check-in-time")
    public ResponseEntity<MetricsResponse> getAverageCheckInTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(metricsService.getAverageCheckInTime(startDate, endDate));
    }

    @GetMapping("/average-check-out-time")
    public ResponseEntity<MetricsResponse> getAverageCheckOutTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(metricsService.getAverageCheckOutTime(startDate, endDate));
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserResponse> getUserProfile() {
        return ResponseEntity.ok(metricsService.getUserProfile());
    }

}
