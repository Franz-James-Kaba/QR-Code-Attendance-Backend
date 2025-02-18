package com.example.attendance_system.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String message;
    private final int code;

}
