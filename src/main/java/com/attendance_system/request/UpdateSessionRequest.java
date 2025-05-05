package com.attendance_system.request;

import java.time.LocalDateTime;

public record UpdateSessionRequest (
        String name,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
