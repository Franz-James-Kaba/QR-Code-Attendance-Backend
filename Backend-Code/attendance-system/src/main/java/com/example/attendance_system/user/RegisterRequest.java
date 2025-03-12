package com.example.attendance_system.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RegisterRequest {
    @NotBlank(message="firstName is required")
    @Size(min = 3, max = 50, message = "First name must be at least 3 characters long")
    private String firstName;
    private String middleName;
    @NotBlank(message = "lastName is required")
    @Size(min = 3, max = 50, message = "Last name must be at least 3 characters long")
    private String lastName;
    @Email(message = "Invalid email format")
    @NotEmpty(message = "Email should not be empty")
    @NotBlank(message = "Email is required")
    private String email;

}
