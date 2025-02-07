package com.example.attendance_system.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.bridge.Message;

@Setter
@Getter
@Builder
public class RegisterRequest {
    @NotBlank(message="firstName can not be blank")
    @Size(min = 3, max = 50, message = "First name must be at least 3 characters long")
    private String firstName;
    private String middleName;
    @NotBlank(message = "lastName should not be blank")
    private String lastName;
    @Email(message = "Invalid email format")
    @NotEmpty(message = "Email should not be empty")
    @NotBlank(message = "Email should not be blank")
    private String email;
//    @NotBlank(message = "Password is mandatory")
//    @Size(min = 8, message = "Password should be at least 8 characters long")
//    private String password;
}
