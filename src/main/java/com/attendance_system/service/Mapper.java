package com.attendance_system.service;

import com.attendance_system.dto.CustomAttendance;
import com.attendance_system.model.Attendance;
import org.springframework.stereotype.Service;

@Service
public class Mapper {

    public CustomAttendance toAttendanceDTO(Attendance attendance) {
        return CustomAttendance.builder()
                .id(attendance.getId())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .date(attendance.getDate())
                .firstName(attendance.getUser().getFirstName())
                .lastName(attendance.getUser().getLastName())
                .build();
    }
}
