package com.example.attendance_system.user;

import com.example.attendance_system.config.JWTService;
import com.example.attendance_system.email.EmailService;
import com.example.attendance_system.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PasswordGenerator passwordGenerator;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final TokenRepository tokenRepository;


    public void createUser(RegisterRequest request) {
        String password = passwordGenerator.generatePassword(12);

        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role does not exist"));
        var user = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .passwordResetRequired(true)
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .roles(List.of(userRole))
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
                .roles(user.getRoles())
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

    public String resetPasswordRequest(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        sendPasswordResetMail(user);
        return "Password reset code sent to your email address";
    }

    private void sendPasswordResetMail(User user) {
        String token = generateAndSaveToken(user);
        emailService.sendEmail(user.getEmail(),
                "Password Reset",
                "You requested to reset your password. Enter the following code to reset your password. " + token);
    }

    private String generateAndSaveToken(User user) {
        String generatedToken = generateCode();
        var token = Token.builder()
                .token(generatedToken)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateCode() {
        String characters = "0123456789";
        StringBuilder token = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            var randomIndex = random.nextInt(characters.length());
            token.append(characters.charAt(randomIndex));
        }
        return token.toString();
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
}