package com.example.attendance_system.user;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody @Valid RegisterRequest request) throws MessagingException {
        return ResponseEntity.ok(userService.createAdmin(request));

    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPassword(@RequestParam String email) throws MessagingException {
        return ResponseEntity.ok(userService.resetPasswordRequest(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String token, @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(token, email, request));
    }

    @PostMapping("/first-password-reset")
    public ResponseEntity<String> firstPasswordReset(@RequestParam String email, @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.firstPasswordReset(email, request));
    }
}
