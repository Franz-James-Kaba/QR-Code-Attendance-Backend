package com.example.attendance_system.exceptions;

public class UnauthorizedUserException extends RuntimeException
{
    public UnauthorizedUserException(String message){
        super(message);
    }
}
