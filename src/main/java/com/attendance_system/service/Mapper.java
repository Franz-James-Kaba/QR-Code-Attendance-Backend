package com.attendance_system.service;

import com.attendance_system.dto.CustomAttendance;
import com.attendance_system.model.Attendance;
import com.attendance_system.model.Session;
import org.springframework.stereotype.Service;

@Service
public class Mapper {

    public SessionDTO toSessionDTO(Session session) {
        return SessionDTO.builder()
                .id(session.getId())
                .name(session.getName())
                .sessionCode(session.getSessionCode())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .active(session.isActive())
                .build();
    }

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
