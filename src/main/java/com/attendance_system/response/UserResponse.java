package com.attendance_system.response;

import com.attendance_system.role.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private String fullName;
    private Role role;
}
