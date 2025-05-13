package com.attendance_system.service;

import com.attendance_system.dto.AttendanceDTO;
import com.attendance_system.exceptions.AttendanceServiceException;
import com.attendance_system.exceptions.InvalidSessionException;
import com.attendance_system.exceptions.ResourceNotFoundException;
import com.attendance_system.exceptions.UserNotFoundException;
import com.attendance_system.model.Attendance;
import com.attendance_system.model.User;
import com.attendance_system.repository.AttendanceRepository;
import com.attendance_system.repository.SessionRepository;
import com.attendance_system.repository.UserRepository;
import com.attendance_system.response.AttendanceListResponse;
import com.attendance_system.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private static final LocalTime DEFAULT_EARLY_TIME = LocalTime.of(7, 30);

    public SuccessResponse checkIn(String sessionCode) {
        validateSession(sessionCode);

        var user = getAuthenticatedUser();
        var today = LocalDate.now();
        if (attendanceRepository.existsByDateAndUser(today, user))
            return SuccessResponse.builder()
                    .message("Attendance already recorded for today")
                    .success(false)
                    .build();

        var attendance = Attendance.builder()
                .checkInTime(LocalDateTime.now())
                .user(user)
                .date(LocalDate.now())
                .build();
        attendanceRepository.save(attendance);
        logAttendance(user.getEmail());

        return SuccessResponse.builder()
                .success(true)
                .message("Attendance recorded successfully")
                .build();
    }

    public SuccessResponse checkOut(String sessionCode) {
        validateSession(sessionCode);

        var user = getAuthenticatedUser();
        var today = LocalDate.now();

        var attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new ResourceNotFoundException("No check-in record found for today"));

        LocalTime minCheckOutTime = LocalTime.of(16, 30);
        LocalDateTime minCheckOutDateTime = LocalDateTime.of(today, minCheckOutTime);

        if (LocalDateTime.now().isBefore(minCheckOutDateTime)) {
            return SuccessResponse.builder()
                    .message("Cannot check out before 4:30 PM")
                    .success(false)
                    .build();
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendanceRepository.save(attendance);
        logAttendance(user.getEmail());

        return SuccessResponse.builder()
                .message("Checked out successfully")
                .success(true)
                .build();
    }

    public AttendanceListResponse getUserAttendanceHistory() {
        var user = getAuthenticatedUser();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);

        var attendanceList = attendanceRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
        return AttendanceListResponse.builder()
                .success(true)
                .message("User attendance history retrieved successfully")
                .attendanceList(attendanceList)
                .build();
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


    public List<AttendanceDTO> getEarlyAttendees(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        try {
            LocalTime earlyTime = DEFAULT_EARLY_TIME; // e.g., LocalTime.of(9, 0); // 9:00 AM

            // Use the original repository method, but limit to 5 results
            Pageable pageable = PageRequest.of(0, 10, Sort.by("checkInTime").ascending());
            Page<Attendance> attendancePage = attendanceRepository.findAttendeesByDate(
                    date, pageable);

            // Filter early attendees in Java and map to DTOs
            return attendancePage.getContent().stream()
                    .filter(attendance -> attendance.getCheckInTime().toLocalTime().isBefore(earlyTime))
                    .limit(5)
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving early attendees", e);
            throw new AttendanceServiceException("Failed to retrieve early attendees", e);
        }
    }

    // Helper method to convert Attendance to AttendanceDTO
    private AttendanceDTO convertToDTO(Attendance attendance) {
        User user = attendance.getUser();
        return new AttendanceDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                attendance.getCheckInTime()
        );
    }

}