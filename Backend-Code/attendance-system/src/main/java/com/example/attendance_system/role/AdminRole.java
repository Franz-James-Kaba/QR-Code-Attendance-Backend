package com.example.attendance_system.role;

import static com.example.attendance_system.role.Role.ADMIN;

public class AdminRole implements Roles {
    @Override
    public Role getRole() {
        return ADMIN;
    }
}
