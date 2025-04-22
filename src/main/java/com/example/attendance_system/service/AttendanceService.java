package com.example.attendance_system.service;

import com.example.attendance_system.exceptions.InvalidSessionException;
import com.example.attendance_system.exceptions.ResourceNotFoundException;
import com.example.attendance_system.exceptions.UserNotFoundException;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repository.AttendanceRepository;
import com.example.attendance_system.repository.SessionRepository;
import com.example.attendance_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository  userRepository;

    public String checkIn(String sessionCode) {
        validateSession(sessionCode);

        var user = getAuthenticatedUser();
        var today = LocalDate.now();
        if (attendanceRepository.existsByDate(today))
            return "Attendance already recorded for today";

        var attendance = Attendance.builder()
                .checkInTime(LocalDateTime.now())
                .userId(user.getId())
                .date(LocalDate.now())
                .build();
        attendanceRepository.save(attendance);
        logAttendance(user.getEmail());

        return "Attendance recorded successfully";
    }

    public String checkOut(String sessionCode) {
        validateSession(sessionCode);

        var user = getAuthenticatedUser();
        var userId = user.getId();
        var today = LocalDate.now();

        var attendance = attendanceRepository.findByUserIdAndDate(userId, today)
                .orElseThrow(() -> new ResourceNotFoundException("No check-in record found for today"));

        LocalDateTime minCheckOutTime = attendance.getCheckInTime().plusHours(9);
        if (LocalDateTime.now().isBefore(minCheckOutTime))
            return "Cannot check out before minimum work period (8 hours)";


        attendance = Attendance.builder()
                .checkOutTime(LocalDateTime.now())
                .userId(userId)
                .build();
        attendanceRepository.save(attendance);
        logAttendance(user.getEmail());

        return "Checked out successfully";
    }

    private static void logAttendance(String email) {
        log.info("Attendance recorded for user with email address: {}", email);
    }

    private void validateSession(String sessionCode) {
        var session = sessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        if (!session.isActive())
            throw new InvalidSessionException("Session invalid or expired");
    }

    private User getAuthenticatedUser() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
