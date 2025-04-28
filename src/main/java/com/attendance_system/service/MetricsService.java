package com.attendance_system.service;

import com.attendance_system.repository.AttendanceRepository;
import com.attendance_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;


    public LocalTime getAverageCheckInTime() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getAverageCheckInTime(user.getId());

    }

    public LocalTime getAverageCheckOutTime() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getAverageCheckOutTime(user.getId());
    }

    private LocalTime getAverageCheckInTime(Long userId) {
        Double avgSeconds = attendanceRepository.findAverageCheckInTimeInSecondsByUserId(userId);
        return convertToLocalTime(avgSeconds);
    }

    private LocalTime getAverageCheckOutTime(Long userId) {
        Double avgSeconds = attendanceRepository.findAverageCheckOutTimeInSecondsByUserId(userId);
        return convertToLocalTime(avgSeconds);
    }

    private LocalTime convertToLocalTime(Double avgSeconds) {
        if (avgSeconds == null) return null;

        int hours = (int) (avgSeconds / 3600);
        int minutes = (int) ((avgSeconds % 3600) / 60);
        int seconds = (int) (avgSeconds % 60);

        return LocalTime.of(hours, minutes, seconds);
    }




}
