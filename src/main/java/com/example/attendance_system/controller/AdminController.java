package com.example.attendance_system.controller;

import com.example.attendance_system.dto.UserDTO;
import com.example.attendance_system.exceptions.AttendanceServiceException;
import com.example.attendance_system.exceptions.UnauthorizedUserException;
import com.example.attendance_system.exceptions.UserNotFoundException;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.model.User;
import com.example.attendance_system.request.RegisterRequest;
import com.example.attendance_system.request.UpdateUserRequest;
import com.example.attendance_system.role.FacilitatorRole;
import com.example.attendance_system.role.NSPRole;
import com.example.attendance_system.service.AttendanceService;
import com.example.attendance_system.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;
    private final AttendanceService attendanceService;

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


    @GetMapping("/users")
    public ResponseEntity<?> getUser(@RequestParam("email") @Valid String email) {
        try {
            User user = userService.getUserByEmail(email);
            UserDTO userDTO = UserDTO.fromUser(user);
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            return buildErrorResponse("User not found", HttpStatus.NOT_FOUND);
        } catch (UnauthorizedUserException e) {
            return buildErrorResponse("You are not authorized to perform this action", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/users/nsps")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("email").ascending());
            Page<User> users = userService.getAllNsps(pageable);

            // Map User to UserDTO
            Page<UserDTO> userDTOs = users.map(UserDTO::fromUser);

            return ResponseEntity.ok(userDTOs);
        } catch (UserNotFoundException e) {
            return buildErrorResponse("User not found", HttpStatus.NOT_FOUND);
        } catch (UnauthorizedUserException e) {
            return buildErrorResponse("You are not authorized to perform this action", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/users/facilitators")
    public ResponseEntity<?> getAllFacilitators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("email").ascending());
            Page<User> users = userService.getAllFacilitators(pageable);

            // Map User to UserDTO
            Page<UserDTO> userDTOs = users.map(UserDTO::fromUser);

            return ResponseEntity.ok(userDTOs);
        } catch (UserNotFoundException e) {
            return buildErrorResponse("There are no facilitators", HttpStatus.NOT_FOUND);
        } catch (UnauthorizedUserException e) {
            return buildErrorResponse("You are not authorized to perform this action", HttpStatus.FORBIDDEN);
        }
    }

        //get early attendee

    @GetMapping("/early-attendees")
    public ResponseEntity<?> getEarlyAttendees(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "100") int size) {

        try {
            Page<Attendance> earlyAttendees = attendanceService.getEarlyAttendees(startDate, endDate, page, size);
            return ResponseEntity.ok(earlyAttendees);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (AttendanceServiceException e) {
            log.error("Service error while getting early attendees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve early attendees"));
        }
    }

    // Simple error response class
    @Data
    private static class ErrorResponse {
        private final String message;
        private final LocalDateTime timestamp = LocalDateTime.now();
    }

    @PostMapping("/grant-reception-privilege/{email}")
    public ResponseEntity<String> grantReceptionPrivilege(@PathVariable("email") String email) {
        return ResponseEntity.ok(userService.grantReceptionPrivilege(email));
    }

    @PostMapping("/revoke-reception-privilege/{email}")
    public ResponseEntity<String> revokeReceptionPrivilege(@PathVariable("email") String email) {
        return ResponseEntity.ok(userService.revokeReceptionPrivilege(email));
    }


    //Helper method to handle error messages
    private ResponseEntity<Map<String, String>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}

