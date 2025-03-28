package com.example.attendance_system.service;

import com.example.attendance_system.config.JWTService;
import com.example.attendance_system.email.EmailService;
import com.example.attendance_system.exceptions.InvalidTokenException;
import com.example.attendance_system.exceptions.ResourceNotFoundException;
import com.example.attendance_system.exceptions.TokenExpiredException;
import com.example.attendance_system.exceptions.UserNotFoundException;
import com.example.attendance_system.model.Token;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repository.TokenRepository;
import com.example.attendance_system.repository.UserRepository;
import com.example.attendance_system.request.AuthenticationRequest;
import com.example.attendance_system.request.RegisterRequest;
import com.example.attendance_system.request.ResetPasswordRequest;
import com.example.attendance_system.request.UpdateUserRequest;
import com.example.attendance_system.response.AuthenticationResponse;
import com.example.attendance_system.role.AdminRole;
import com.example.attendance_system.role.FacilitatorRole;
import com.example.attendance_system.role.NSPRole;
import com.example.attendance_system.role.Role;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.attendance_system.role.Role.NSP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private TokenService tokenService;

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
        String result = userService.createUser(request, new NSPRole());

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
        assertEquals(NSP, savedUser.getRole());
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

    @Test
    void testLogin_Success() {
        user.setRole(NSP);
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mock JWT service
        when(jwtService.generateToken(user.getUsername())).thenReturn("jwt-token-123");

        AuthenticationResponse response = userService.login(authRequest);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals(user.getRole().name(), response.getRole());
        assertEquals(user.isPasswordResetRequired(), response.isPasswordResetRequired());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        assertEquals(authRequest.getEmail(), authCaptor.getValue().getPrincipal());
        assertEquals(authRequest.getPassword(), authCaptor.getValue().getCredentials());
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("wrongPassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        Exception exception = assertThrows(BadCredentialsException.class, () ->
                userService.login(authRequest));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testResetPasswordRequest_Success() throws MessagingException {
        // Arrange
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenService.generateAndSaveToken(user)).thenReturn("generated-token-123");
        doNothing().when(emailService).sendPasswordResetEmail(
                eq(email),
                eq(user.getFirstName()),
                eq("generated-token-123")
        );

        String result = userService.resetPasswordRequest(email);

        assertEquals("Password reset code sent to your email address", result);
        verify(tokenService, times(1)).generateAndSaveToken(user);
        verify(emailService, times(1)).sendPasswordResetEmail(
                email,
                user.getFirstName(),
                "generated-token-123"
        );
    }

    @Test
    void testResetPasswordRequest_UserNotFound() throws MessagingException {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());


        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.resetPasswordRequest(email));

        assertEquals("User not found", exception.getMessage());
        verify(tokenService, never()).generateAndSaveToken(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testResetPassword_TokenExpired() {
        String tokenValue = "expired-token";
        String email = user.getEmail();

        Token expiredToken = new Token();
        expiredToken.setToken(tokenValue);
        expiredToken.setUser(user);
        expiredToken.setExpiresAt(LocalDateTime.now().minusMinutes(10)); // Expired 10 minutes ago

        when(tokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(expiredToken));

        Exception exception = assertThrows(TokenExpiredException.class, () ->
                userService.resetPassword(tokenValue, email, resetPasswordRequest));

        assertEquals("Token is expired.", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void testResetPassword_InvalidEmail() {
        String tokenValue = "valid-token";
        String wrongEmail = "wrong.email@example.com";

        when(tokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        Exception exception = assertThrows(InvalidTokenException.class, () ->
                userService.resetPassword(tokenValue, wrongEmail, resetPasswordRequest));

        assertEquals("Invalid token", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        Long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser(nonExistentUserId, updateUserRequest));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUserByEmail_Success() {
        // Arrange
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.getUserByEmail(email);

        // Assert
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByEmail(email);
        });
    }

    @Test
    void testGetAllNsps_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = Arrays.asList(
                createUserWithRole(Role.NSP),
                createUserWithRole(Role.NSP)
        );
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userRepository.findByRole(eq(Role.NSP), eq(pageable))).thenReturn(userPage);

        // Act
        Page<User> result = userService.getAllNsps(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(u -> u.getRole() == Role.NSP));
        verify(userRepository).findByRole(eq(Role.NSP), eq(pageable));
    }


    @Test
    void testGetAllNsps_NoUsersFound() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findByRole(eq(Role.NSP), eq(pageable))).thenReturn(emptyPage);

        // Act
        Page<User> result = userService.getAllNsps(pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(userRepository).findByRole(eq(Role.NSP), eq(pageable));
    }

    @Test
    void testGetAllFacilitators_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> facilitatorList = Arrays.asList(
                createUserWithRole(Role.FACILITATOR),
                createUserWithRole(Role.FACILITATOR)
        );
        Page<User> facilitatorPage = new PageImpl<>(facilitatorList, pageable, facilitatorList.size());

        when(userRepository.findByRole(eq(Role.FACILITATOR), eq(pageable))).thenReturn(facilitatorPage);

        // Act
        Page<User> result = userService.getAllFacilitators(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(u -> u.getRole() == Role.FACILITATOR));
        verify(userRepository).findByRole(eq(Role.FACILITATOR), eq(pageable));
    }

    private User createUserWithRole(Role role) {
        User user = new User();
        user.setRole(role);
        user.setEmail(role.name().toLowerCase() + "@example.com");
        return user;
    }
}
