package com.example.attendance_system.user;

import com.example.attendance_system.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('admin:update', 'admin:read', 'admin:delete')")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }


    @DeleteMapping("users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build(); // Return 404 if user not found
        }
    }
}
