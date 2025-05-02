package com.attendance_system.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class MetricsResponse {
    private String message;
    private LocalTime data;
}
