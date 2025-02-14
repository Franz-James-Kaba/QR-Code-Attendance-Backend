package com.example.attendance_system.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_UPDATE("admin:update"),
    ADMIN_READ("admin:read"),
    ADMIN_DELETE("admin:delete"),
    USER_UPDATE("user:update");

    @Getter
    private final String permission;
}
