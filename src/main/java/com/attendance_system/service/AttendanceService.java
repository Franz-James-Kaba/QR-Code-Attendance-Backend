package com.attendance_system.service;

import com.attendance_system.dto.AttendanceDTO;
import com.attendance_system.dto.UserPointsDTO;
import com.attendance_system.exceptions.*;
import com.attendance_system.model.Attendance;
import com.attendance_system.model.User;
import com.attendance_system.repository.AttendanceRepository;
import com.attendance_system.repository.SessionRepository;
import com.attendance_system.repository.UserRepository;
import com.attendance_system.response.AttendanceListResponse;
import com.attendance_system.response.PositionResponse;
import com.attendance_system.response.SuccessResponse;
import com.attendance_system.response.WorkingDaysResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
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
            throw new IllegalStateException("User already checked in today");

        var userRole = user.getRole();
        int position = attendanceRepository.countByDateAndUserRole(today, userRole) + 1;

        int points = switch (position) {
            case 1 -> 5;
            case 2 -> 4;
            case 3 -> 3;
            case 4 -> 2;
            default -> 1;
        };

        var attendance = Attendance.builder()
                .checkInTime(LocalTime.now())
                .user(user)
                .date(LocalDate.now())
                .position(position)
                .point(points)
                .build();
        attendanceRepository.save(attendance);
        logAttendance(user.getEmail());

        return SuccessResponse.builder()
                .success(true)
                .message("Attendance recorded successfully")
                .build();
    }

    public PositionResponse getUserPosition() {
        var user = getAuthenticatedUser();
        var today = LocalDate.now();

        var attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));

        return PositionResponse.builder()
                .success(true)
                .message("Position retrieved successfully")
                .position(attendance.getPosition())
                .build();
    }

    public SuccessResponse checkOut(String sessionCode) {
        validateSession(sessionCode);

        var user = getAuthenticatedUser();
        var today = LocalDate.now();

        var attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new ResourceNotFoundException("No check-in record found for today"));

        if (attendance.getCheckOutTime() != null)
            throw new IllegalStateException("User already checked out today");

        LocalTime minCheckOutTime = LocalTime.of(16, 30);
        LocalDateTime minCheckOutDateTime = LocalDateTime.of(today, minCheckOutTime);

        if (LocalDateTime.now().isBefore(minCheckOutDateTime))
            throw new IllegalStateException("Cannot check out before 4:30 PM");


        attendance.setCheckOutTime(LocalTime.now());
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

    public WorkingDaysResponse getWorkingDays() {
        var user = getAuthenticatedUser();
        var now = LocalDate.now();
        var month = now.getMonthValue();
        var year = now .getYear();

        var workingDays = attendanceRepository.countWeekdayAttendancesByUserAndMonth(user.getId(), month, year);
        var totalWorkingDays = getTotalWorkingDaysInMonth(year, month);

        return WorkingDaysResponse.builder()
                .success(true)
                .message("Working days retrieved successfully")
                .workingDays(workingDays + "/" + totalWorkingDays)
                .build();
    }

    public int getTotalWorkingDaysInMonth(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        int workingDays = 0;
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                workingDays++;
            }
        }
        return workingDays;
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

            // Use the original repository method, but limit to 5 results
            Pageable pageable = PageRequest.of(0, 10, Sort.by("checkInTime").ascending());
            Page<Attendance> attendancePage = attendanceRepository.findAttendeesByDate(
                    date, pageable);

            // Filter early attendees in Java and map to userPointsDTO
            return attendancePage.getContent().stream()
                    .filter(attendance -> attendance.getCheckInTime().isBefore(DEFAULT_EARLY_TIME))
                    .limit(5)
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving early attendees", e);
            throw new AttendanceServiceException("Failed to retrieve early attendees", e);
        }
    }

    public Integer getUserPointById(Long userId, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(!currentUser.getId().equals(userId)){
            throw new UnauthorizedUserException("You are not authorized to view others points");
        }


        Integer points = attendanceRepository.getTotalPointsByUserId(userId);
        return points != null ? points : 0;
    }


    public List<UserPointsDTO> getUsersLeaderboard() {
        try {
            List<Map<String, Object>> results = attendanceRepository.findAllNspUsersWithTotalPointsAndRank();
            return results.stream()
                    .map(this::mapToUserPointsDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to retrieve users leaderboard data", e);
            throw new ServiceException("Failed to retrieve leaderboard data", e);
        }
    }

    /**
     * Maps a database result row to a UserPointsDTO object
     */
    private UserPointsDTO mapToUserPointsDTO(Map<String, Object> row) {
        try {
            UserPointsDTO dto = new UserPointsDTO();
            dto.setUserId(((Number) row.getOrDefault("user_id", 0)).longValue());
            dto.setFirstName((String) row.getOrDefault("first_name", ""));
            dto.setLastName((String) row.getOrDefault("last_name", ""));
            dto.setTotalPoints(((Number) row.getOrDefault("total_points", 0)).intValue());
            dto.setPosition(((Number) row.getOrDefault("position", 0)).intValue());
            return dto;
        } catch (ClassCastException e) {
            log.warn("Data type mismatch when mapping user data", e);
            throw new DataMappingException("Error mapping user data: " + e.getMessage(), e);
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