package com.example.attendance_system.role;

import static com.example.attendance_system.role.Role.USER;

public class UserRole implements Roles {
    @Override
    public Role getRole() {
        return USER;
    }
}
