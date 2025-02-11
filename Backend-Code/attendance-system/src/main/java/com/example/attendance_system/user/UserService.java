package com.example.attendance_system.user;

import com.example.attendance_system.config.JWTService;
import com.example.attendance_system.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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


    public void createUser(RegisterRequest request) {
        String password = passwordGenerator.generatePassword(12);


        var user = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .passwordResetRequired(true)
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .role(USER)
                .enabled(false)
                .build();
         userRepository.save(user);
         emailService.sendEmail(user.getEmail(),"Password Reset", "Use this email and this password: "
                 + password + " to login and please reset the password");


    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        if (!authentication.isAuthenticated()) {
            throw new IllegalStateException("Authentication failed");
        }

        var user = (User) authentication.getPrincipal();
        var token = jwtService.generateToken(user.getUsername());
        return AuthenticationResponse.builder()
                .token(token)
                .passwordResetRequired(user.isPasswordResetRequired())
                .role(user.getRole().name())
                .build();
    }

    public String resetPassword(String token, String email, ResetPasswordRequest request) {
        var savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token does not exist"));

        if (!email.equals(savedToken.getUser().getEmail()))
            throw new IllegalStateException("Invalid token");

        if (savedToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Token is expired.");

        var user = userRepository.findByEmail(savedToken.getUser().getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        tokenRepository.delete(savedToken);
        return "Password reset successful";
    }

    public String resetPasswordRequest(String email) throws MessagingException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        sendPasswordResetMail(user);
        return "Password reset code sent to your email address";
    }

    public String firstPasswordReset(String email, ResetPasswordRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User does not exist"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordResetRequired(false);
        user.setEnabled(true);
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
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        return userRepository.save(user);
    }
}