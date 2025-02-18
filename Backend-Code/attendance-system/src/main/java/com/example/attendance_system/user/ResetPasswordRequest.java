package com.example.attendance_system.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResetPasswordRequest {
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "Password must contain at least one special character.")
    private String password;
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @AssertTrue(message = "Passwords must match")
    private boolean isPasswordsMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
