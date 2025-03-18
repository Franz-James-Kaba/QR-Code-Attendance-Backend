package com.example.attendance_system.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private String token;
    private boolean passwordResetRequired;
    private String role;
}
