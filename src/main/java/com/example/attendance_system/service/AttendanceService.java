package com.example.attendance_system.service;

import com.example.attendance_system.exceptions.InvalidSessionException;
import com.example.attendance_system.exceptions.ResourceNotFoundException;
import com.example.attendance_system.exceptions.UserNotFoundException;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.repository.AttendanceRepository;
import com.example.attendance_system.repository.SessionRepository;
import com.example.attendance_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository  userRepository;

    public String createAttendance(String sessionCode) {
        var session = sessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!session.isActive())
            throw new InvalidSessionException("Session invalid or expired");

        var attendance = Attendance.builder()
                .checkInTime(LocalDateTime.now())
                .userId(user.getId())
                .build();
        attendanceRepository.save(attendance);
        log.info("Attendance logged for user with mail: {}", user.getEmail());

        return "Attendance recorded successfully";
    }
}
