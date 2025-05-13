package com.attendance_system.response;

import com.attendance_system.model.Attendance;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AttendanceListResponse {
    private boolean success;
    private String message;
    private List<Attendance> attendanceList;
}
