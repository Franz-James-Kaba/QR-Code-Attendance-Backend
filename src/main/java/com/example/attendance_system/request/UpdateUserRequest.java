package com.example.attendance_system.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    String middleName;
    private String email;
}
