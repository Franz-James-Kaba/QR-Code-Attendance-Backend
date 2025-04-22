package com.example.attendance_system.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    ADMIN_CREATE("admin:create"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_READ("admin:read"),
    ADMIN_DELETE("admin:delete"),
    USER_UPDATE("user:update"),
    GENERATE_QRCODE("qrcode:generate");

    private final String permission;
}
