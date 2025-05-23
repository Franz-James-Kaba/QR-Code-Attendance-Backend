package com.attendance_system.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomAttendance {
    private Long id;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private LocalDate date;
    private String firstName;
    private String lastName;

}
