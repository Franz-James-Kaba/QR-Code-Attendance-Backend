package com.example.attendance_system.controller;

import com.example.attendance_system.exceptions.UnauthorizedUserException;
import com.example.attendance_system.exceptions.UserNotFoundException;
import com.example.attendance_system.role.FacilitatorRole;
import com.example.attendance_system.role.NSPRole;
import com.example.attendance_system.request.RegisterRequest;
import com.example.attendance_system.request.UpdateUserRequest;
import com.example.attendance_system.model.User;
import com.example.attendance_system.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('admin:create', 'admin:update', 'admin:read', 'admin:delete')")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PostMapping("/create-nsp")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) throws MessagingException {
        userService.createUser(request, new NSPRole());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/create-facilitator")
    public ResponseEntity<String> createFacilitator(@Valid @RequestBody RegisterRequest request) throws MessagingException {
        return ResponseEntity.ok(userService.createUser(request, new FacilitatorRole()));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"); // Return 404 if user not found
        } catch (UnauthorizedUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action");
        }
    }
}
