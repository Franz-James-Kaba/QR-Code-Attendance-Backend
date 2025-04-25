package com.example.attendance_system.exceptions;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ValidationErrorResponse {
    private String message;
    private int code;
    private Map<String, String> fieldErrors;
}
