package com.example.attendance_system.user;

import com.example.attendance_system.role.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private String token;
    private boolean passwordResetRequired;
    private String role;
}
