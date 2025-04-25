package com.attendance_system.exceptions;

public class AttendanceServiceException extends RuntimeException {
    public AttendanceServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

