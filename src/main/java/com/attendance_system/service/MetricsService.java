package com.attendance_system.service;

import com.attendance_system.repository.AttendanceRepository;
import com.attendance_system.repository.UserRepository;
import com.attendance_system.response.MetricsResponse;
import com.attendance_system.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;


    public MetricsResponse getAverageCheckInTime(LocalDate startDate, LocalDate endDate) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return MetricsResponse.builder()
                .message("Average check-in time retrieved for user: " + user.getEmail())
                .data(getAverageCheckInTime(user.getId(), startDate, endDate))
                .build();

    }

    public MetricsResponse getAverageCheckOutTime(LocalDate startDate, LocalDate endDate) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return MetricsResponse.builder()
                .message("Average check-out time retrieved for user: " + user.getEmail())
                .data(getAverageCheckOutTime(user.getId(), startDate, endDate))
                .build();
    }

    private LocalTime getAverageCheckInTime(Long userId, LocalDate startDate, LocalDate endDate) {
        Double avgSeconds = attendanceRepository.findAverageCheckInTimeInSecondsByUserIdAndDateRange(userId, startDate, endDate);
        return convertToLocalTime(avgSeconds);
    }

    private LocalTime getAverageCheckOutTime(Long userId, LocalDate startDate, LocalDate endDate) {
        Double avgSeconds = attendanceRepository.findAverageCheckOutTimeInSecondsByUserIdAndDateRange(userId, startDate, endDate);
        return convertToLocalTime(avgSeconds);
    }

    public LocalTime getAverageCheckInTimeForNSPS(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both startDate and endDate must be provided");
        }
        validateDateRange(startDate, endDate);

        Instant avgCheckInTime = attendanceRepository
                .findAverageCheckInTimeByUserRoleAndDateRange("NSP", startDate, endDate);

        return avgCheckInTime != null ?
                avgCheckInTime.atZone(ZoneId.systemDefault()).toLocalTime() :
                null;
    }
    public LocalTime getAverageCheckOutTimeForNSPS(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both startDate and endDate must be provided");
        }
        validateDateRange(startDate, endDate);

        Instant avgCheckInTime = attendanceRepository
                .findAverageCheckOutTimeByUserRoleAndDateRange("NSP", startDate, endDate);

        return avgCheckInTime != null ?
                avgCheckInTime.atZone(ZoneId.systemDefault()).toLocalTime() :
                null;
    }


    public LocalTime getAverageCheckInTimeForFacilitators(LocalDate startDate, LocalDate endDate){
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both startDate and endDate must be provided");
        }
        validateDateRange(startDate, endDate);

        Instant avgCheckInTime = attendanceRepository
                .findAverageCheckInTimeByUserRoleAndDateRange("FACILITATOR", startDate, endDate);

        return avgCheckInTime != null ?
                avgCheckInTime.atZone(ZoneId.systemDefault()).toLocalTime() :
                null;
    }

    public LocalTime getAverageCheckOutTimeForFacilitators(LocalDate startDate, LocalDate endDate){
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both startDate and endDate must be provided");
        }
        validateDateRange(startDate, endDate);

        Instant avgCheckInTime = attendanceRepository
                .findAverageCheckOutTimeByUserRoleAndDateRange("FACILITATOR", startDate, endDate);

        return avgCheckInTime != null ?
                avgCheckInTime.atZone(ZoneId.systemDefault()).toLocalTime() :
                null;
    }

    public UserResponse getUserProfile() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var fullName = user.getMiddleName().isEmpty() ?
                user.getFirstName() + " " + user.getLastName() :
                user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName();

        return UserResponse.builder()
                .fullName(fullName)
                .role(user.getRole())
                .build();
    }


    private LocalTime convertToLocalTime(Double avgSeconds) {
        if (avgSeconds == null) return null;

        int hours = (int) (avgSeconds / 3600);
        int minutes = (int) ((avgSeconds % 3600) / 60);
        int seconds = (int) (avgSeconds % 60);

        return LocalTime.of(hours, minutes, seconds);
    }


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
