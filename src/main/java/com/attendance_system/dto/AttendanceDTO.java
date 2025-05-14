package com.attendance_system.dto;

import com.attendance_system.role.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Data
@Getter
@Setter
public class AttendanceDTO {
    private String firstName;
    private String lastName;
    private String role;
    private LocalTime checkInTime;


    public AttendanceDTO(String firstName, String lastName, Role role, LocalTime checkInTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = String.valueOf(role);
        this.checkInTime = checkInTime;
    }
}