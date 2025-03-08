package com.example.attendance_system.role;

import static com.example.attendance_system.role.Role.FACILITATOR;

public class FacilitatorRole implements Roles {
    @Override
    public Role getRole() {
        return FACILITATOR;
    }
}
