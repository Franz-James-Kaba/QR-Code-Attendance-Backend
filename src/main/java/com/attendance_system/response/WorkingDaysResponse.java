package com.attendance_system.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WorkingDaysResponse {

    private boolean success;
    private String message;
    private String workingDays;
}
