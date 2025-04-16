package com.example.attendance_system.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
    @Index(name = "idx_session_status_time",
           columnList = "active,startTime,endTime")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Session {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true, nullable = false)
    private String sessionCode;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private boolean active;

    @PrePersist
    @PreUpdate
    private void validateTimeRange() {
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalStateException("End time cannot be before start time");
        }
    }
}

