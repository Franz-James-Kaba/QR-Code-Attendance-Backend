package com.example.attendance_system.user;

import com.example.attendance_system.config.JWTService;
import com.example.attendance_system.email.EmailService;
import com.example.attendance_system.exceptions.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.attendance_system.role.Role.ADMIN;
import static com.example.attendance_system.role.Role.USER;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;



    public void createUser(RegisterRequest request) throws MessagingException {
        String password = passwordGenerator.generatePassword(12);

        var user = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .passwordResetRequired(true)
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .role(USER)
                .build();
         userRepository.save(user);
         emailService.sendMailWithTemporaryPassword(
                 user.getEmail(),
                 user.getFirstName(),
                 password
         );

    }

    public String createAdmin(RegisterRequest request) throws MessagingException {
        var password = passwordGenerator.generatePassword(12);
        var user = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .passwordResetRequired(true)
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .role(ADMIN)
                .build();
        userRepository.save(user);
        emailService.sendMailWithTemporaryPassword(
                user.getEmail(),
                user.getFirstName(),
                password
        );
        return "Admin created successfully";
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var user = (User) authentication.getPrincipal();
            var token = jwtService.generateToken(user.getUsername());
            return AuthenticationResponse.builder()
                    .token(token)
                    .passwordResetRequired(user.isPasswordResetRequired())
                    .role(user.getRole().name())
                    .build();

        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public String resetPassword(String token, String email, ResetPasswordRequest request) {
        var savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token does not exist"));

        validateRequest(email, request, savedToken);

        var user = userRepository.findByEmail(savedToken.getUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        tokenRepository.delete(savedToken);
        return "Password reset successful";
    }

    public String resetPasswordRequest(String email) throws MessagingException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        sendPasswordResetMail(user);
        return "Password reset code sent to your email address";
    }

    public String firstPasswordReset(String email, ResetPasswordRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordResetRequired(false);
        userRepository.save(user);
        return "Password reset successful";
    }

    private void sendPasswordResetMail(User user) throws MessagingException {
        String token = tokenService.generateAndSaveToken(user);
        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getFirstName(),
                token
        );
    }

    public User updateUser(Long userId, UpdateUserRequest request) {
        var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }

    private void validateRequest(String email, ResetPasswordRequest request, Token savedToken) {
        if (!email.equals(savedToken.getUser().getEmail()))
            throw new InvalidTokenException("Invalid token");

        if (savedToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new TokenExpiredException("Token is expired.");
    }

}