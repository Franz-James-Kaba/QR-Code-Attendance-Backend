package com.example.attendance_system.service;

import com.example.attendance_system.exceptions.AttendanceServiceException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository  userRepository;
    private static final LocalTime DEFAULT_EARLY_TIME = LocalTime.of(7, 30);
    private static final int DEFAULT_PAGE_SIZE = 100;

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

        LocalTime minCheckOutTime = LocalTime.of(16, 30);
        LocalDateTime minCheckOutDateTime = LocalDateTime.of(today, minCheckOutTime);

        if (LocalDateTime.now().isBefore(minCheckOutDateTime)) {
            return "Cannot check out before 4:30 PM";
        }

        attendance.setCheckOutTime(LocalDateTime.now());
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



    /**
     * Retrieves attendees who checked in early within the specified date range.
     *
     * @param startDate The start date (inclusive) for the search range
     * @param endDate The end date (inclusive) for the search range
     * @param page The page number (zero-based)
     * @param size The page size
     * @return A page of early attendees
     * @throws IllegalArgumentException if date parameters are invalid
     */
    public Page<Attendance> getEarlyAttendees(LocalDate startDate, LocalDate endDate, int page, int size) {
        // Validate inputs
        validateDateRange(startDate, endDate);

        // Set up pagination - use default values if invalid parameters are provided
        page = Math.max(0, page);
        size = (size <= 0) ? DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(page, size);

        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);

            Page<Attendance> allAttendees = attendanceRepository.findAttendeesBetweenDates(
                    startDateTime, endDateTime, pageable);

            // Filter in Java instead of in the database
            List<Attendance> earlyAttendees = allAttendees.getContent().stream()
                    .filter(a -> a.getCheckInTime().toLocalTime().isBefore(DEFAULT_EARLY_TIME))
                    .collect(Collectors.toList());

            return new PageImpl<>(earlyAttendees, pageable, earlyAttendees.size());
        } catch (Exception e) {
            log.error("Error retrieving early attendees", e);
            throw new AttendanceServiceException("Failed to retrieve early attendees", e);
        }
    }

    /**
     * Validates that the provided date range is valid.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @throws IllegalArgumentException if dates are null or startDate is after endDate
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }

        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }
}
