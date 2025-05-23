package com.attendance_system.response;

import com.attendance_system.dto.CustomAttendance;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllSessionAttendanceResponse {
    private boolean success;
    private String message;
    private List<CustomAttendance> sessionAttendance;
}
