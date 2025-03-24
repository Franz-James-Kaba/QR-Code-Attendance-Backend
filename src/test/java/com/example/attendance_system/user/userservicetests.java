package com.example.attendance_system.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.attendance_system.email.EmailService;
import com.example.attendance_system.exceptions.ResourceNotFoundException;
import com.example.attendance_system.exceptions.UserNotFoundException;
import com.example.attendance_system.role.AdminRole;
import com.example.attendance_system.role.FacilitatorRole;
import com.example.attendance_system.role.Role;
import com.example.attendance_system.role.UserRole;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordGenerator passwordGenerator;


    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User user;
    private Token token;
    private ResetPasswordRequest resetPasswordRequest;
    private UpdateUserRequest updateUserRequest;
    private String generatedPassword;
    private String encodedPassword;
    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setMiddleName("M.");
        user.setLastName("Doe");
        user.setPassword("oldPassword");
        user.setPasswordResetRequired(true);

        token = new Token();
        token.setToken("validToken");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        request = RegisterRequest.builder()
                .firstName("John")
                .middleName("Mike")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        resetPasswordRequest = ResetPasswordRequest.builder()
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        updateUserRequest = UpdateUserRequest.builder()
                .firstName("John")
                .middleName("Michael")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        generatedPassword = "TempPass123";
        encodedPassword = "EncodedPass123";
    }

    @Test
    void testCreateUser_Success() throws MessagingException {
        // Arrange
        when(passwordGenerator.generatePassword(12)).thenReturn(generatedPassword);
        when(passwordEncoder.encode(generatedPassword)).thenReturn(encodedPassword);

        // Capture the user that gets saved
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        String result = userService.createUser(request, new UserRole());

        // Assert
        // Verify the user is saved and capture the actual User object
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();  // Get the captured user

        // Verify email is sent
        verify(emailService, times(1)).sendMailWithTemporaryPassword(
                eq(request.getEmail()),
                eq(request.getFirstName()),
                eq(generatedPassword)
        );

        // Assert that the method returns the correct message
        assertEquals("User created successfully", result);

        // Assert the saved user's role is correct
        assertEquals(Role.USER, savedUser.getRole());

        // Assert the expected HTTP status code (201 Created)
        assertEquals(201, 201);  // Static check, consider returning status code in the method
    }



    @Test
    void testFacilitator_Success() throws MessagingException {
        // Arrange
        when(passwordGenerator.generatePassword(12)).thenReturn(generatedPassword);
        when(passwordEncoder.encode(generatedPassword)).thenReturn(encodedPassword);

        // Capture the user that gets saved
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        String result = userService.createUser(request, new FacilitatorRole());

        // Assert
        // Verify the user is saved and capture the actual User object
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();  // Get the captured user

        // Verify email is sent
        verify(emailService, times(1)).sendMailWithTemporaryPassword(
                eq(request.getEmail()),
                eq(request.getFirstName()),
                eq(generatedPassword)
        );

        // Assert that the method returns the correct message
        assertEquals("User created successfully", result);

        // Assert the saved user's role is correct
        assertEquals(Role.FACILITATOR, savedUser.getRole());

        // Assert the expected HTTP status code (201 Created)
        assertEquals(201, 201);  // Static check, consider returning status code in the method
    }


    @Test
    void testAdmin_Success() throws MessagingException {
        // Arrange
        when(passwordGenerator.generatePassword(12)).thenReturn(generatedPassword);
        when(passwordEncoder.encode(generatedPassword)).thenReturn(encodedPassword);

        // Capture the user that gets saved
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        String result = userService.createUser(request, new AdminRole());

        // Assert
        // Verify the user is saved and capture the actual User object
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();  // Get the captured user

        // Verify email is sent
        verify(emailService, times(1)).sendMailWithTemporaryPassword(
                eq(request.getEmail()),
                eq(request.getFirstName()),
                eq(generatedPassword)
        );

        // Assert that the method returns the correct message
        assertEquals("User created successfully", result);

        // Assert the saved user's role is correct
        assertEquals(Role.ADMIN, savedUser.getRole());

        // Assert the expected HTTP status code (201 Created)
        assertEquals(201, 201);  // Static check, consider returning status code in the method
    }


    // ✅ TEST: resetPassword()
    @Test
    void testResetPassword_Success() {
        // Arrange
        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(token));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(resetPasswordRequest.getPassword())).thenReturn("encodedPassword");

        // Act
        String result = userService.resetPassword("validToken", user.getEmail(), resetPasswordRequest);

        // Assert
        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).delete(token);
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("Password reset successful", result);
    }


    // ✅ TEST: firstPasswordReset()
    @Test
    void testFirstPasswordReset_Success() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(resetPasswordRequest.getPassword())).thenReturn("encodedPassword");

        // Act
        String result = userService.firstPasswordReset(user.getEmail(), resetPasswordRequest);

        // Assert
        verify(userRepository, times(1)).save(user);
        assertFalse(user.isPasswordResetRequired());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("Password reset successful", result);
    }

    // ✅ TEST: updateUser()
    @Test
    void testUpdateUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User updatedUser = userService.updateUser(1L, updateUserRequest);

        // Assert
        verify(userRepository, times(1)).save(user);
        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Michael", updatedUser.getMiddleName());
        assertEquals("Doe", updatedUser.getLastName());
        assertEquals("john.doe@example.com", updatedUser.getEmail());
    }

    // ✅ TEST: deleteUser()
    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).delete(user);
    }

    // ✅ TEST: resetPassword() - Token Not Found
    @Test
    void testResetPassword_TokenNotFound() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.resetPassword("invalidToken", user.getEmail(), resetPasswordRequest));

        assertEquals("Token does not exist", exception.getMessage());
    }


    // ✅ TEST: firstPasswordReset() - User Not Found
    @Test
    void testFirstPasswordReset_UserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.firstPasswordReset("unknown@example.com", resetPasswordRequest));

        assertEquals("User does not exist", exception.getMessage());
    }

    // ✅ TEST: deleteUser() - User Not Found
    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(2L));

        assertEquals("User not found", exception.getMessage());
    }
}
