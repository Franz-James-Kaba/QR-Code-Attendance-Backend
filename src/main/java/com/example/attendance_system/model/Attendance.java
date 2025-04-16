package com.example.attendance_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Attendance {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Long userId;

}
