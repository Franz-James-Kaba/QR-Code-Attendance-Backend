package com.attendance_system.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SuccessResponse {
    private String message;
    private boolean success;
}
