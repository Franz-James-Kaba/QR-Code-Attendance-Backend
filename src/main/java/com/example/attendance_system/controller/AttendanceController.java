package com.example.attendance_system.controller;

import com.example.attendance_system.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('NSP', 'FACILITATOR')")
public class AttendanceController {
    private final AttendanceService service;

    @PostMapping
    public ResponseEntity<String> createAttendance(@RequestParam("session-code") String sessionCode) {
        return ResponseEntity.ok(service.createAttendance(sessionCode));
    }
}
