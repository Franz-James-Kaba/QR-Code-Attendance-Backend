package com.attendance_system.dto;

import com.attendance_system.role.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class AttendanceDTO {
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime checkInTime;


    public AttendanceDTO(String firstName, String lastName, Role role, LocalDateTime checkInTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = String.valueOf(role);
        this.checkInTime = checkInTime;
    }
}