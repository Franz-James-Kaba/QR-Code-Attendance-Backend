package com.example.attendance_system.controller;

import com.example.attendance_system.role.Role;
import com.example.attendance_system.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('NSP', 'FACILITATOR')")
public class AttendanceController {
    private final AttendanceService service;

    @PostMapping("/check-in")
    public ResponseEntity<String> checkIn(@RequestParam("session-code") String sessionCode) {
        return ResponseEntity.ok(service.checkIn(sessionCode));
    }

    @PutMapping("/check-out")
    public ResponseEntity<String> checkOut(@RequestParam("session-code") String sessionCode) {
        return ResponseEntity.ok(service.checkOut(sessionCode));
    }
}
