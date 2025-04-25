package com.attendance_system.role;

import static com.attendance_system.role.Role.NSP;

public class NSPRole implements Roles {
    @Override
    public Role getRole() {
        return NSP;
    }
}
