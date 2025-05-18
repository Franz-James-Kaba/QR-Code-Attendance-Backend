package com.attendance_system.controller;

import com.attendance_system.exceptions.ErrorResponse;
import com.attendance_system.exceptions.UnauthorizedUserException;
import com.attendance_system.response.AttendanceListResponse;
import com.attendance_system.response.SuccessResponse;
import com.attendance_system.service.AttendanceService;
import com.attendance_system.response.PositionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('NSP', 'FACILITATOR')")
public class AttendanceController {
    private final AttendanceService service;

    @PostMapping("/check-in")
    public ResponseEntity<SuccessResponse> checkIn(@RequestParam("session-code") String sessionCode) {
        return ResponseEntity.ok(service.checkIn(sessionCode));
    }

    @PutMapping("/check-out")
    public ResponseEntity<SuccessResponse> checkOut(@RequestParam("session-code") String sessionCode) {
        return ResponseEntity.ok(service.checkOut(sessionCode));
    }

    @GetMapping
    public ResponseEntity<AttendanceListResponse> getUserAttendanceHistory() {
        return ResponseEntity.ok(service.getUserAttendanceHistory());
    }

    @GetMapping("/position")
    public ResponseEntity<PositionResponse> getUserPosition() {
        return ResponseEntity.ok(service.getUserPosition());
    }

    @GetMapping("/points/{userId}")
    public ResponseEntity<?> getUserPoints(@PathVariable Long userId, Authentication authentication) {
        try {
            Integer points = service.getUserPointById(userId, authentication);
            return ResponseEntity.ok(points);
        } catch (UnauthorizedUserException ex) {
            var error = ErrorResponse.builder()
                    .message(ex.getMessage())
                    .code(FORBIDDEN.value())
                    .build();
            return new ResponseEntity<>(error, FORBIDDEN);
        }
    }
}
