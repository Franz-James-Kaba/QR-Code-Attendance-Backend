package com.example.attendance_system.user;

import com.example.attendance_system.role.AdminRole;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody @Valid RegisterRequest request) throws MessagingException {
        return ResponseEntity.ok(userService.createUser(request, new AdminRole()));

    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPassword(@RequestParam @Email String email) throws MessagingException {
        return ResponseEntity.ok(userService.resetPasswordRequest(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam @Email String email, @RequestParam String token, @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(token, email, request));
    }

    @PostMapping("/first-password-reset")
    public ResponseEntity<String> firstPasswordReset(
            @RequestParam @Email String email,
            @Valid @RequestBody ResetPasswordRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        return ResponseEntity.ok(userService.firstPasswordReset(email, request));
    }
}
