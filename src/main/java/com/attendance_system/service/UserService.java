package com.attendance_system.service;

import com.attendance_system.config.JWTService;
import com.attendance_system.email.EmailService;
import com.attendance_system.exceptions.InvalidTokenException;
import com.attendance_system.exceptions.ResourceNotFoundException;
import com.attendance_system.exceptions.TokenExpiredException;
import com.attendance_system.exceptions.UserNotFoundException;
import com.attendance_system.model.Token;
import com.attendance_system.model.User;
import com.attendance_system.repository.TokenRepository;
import com.attendance_system.repository.UserRepository;
import com.attendance_system.request.AuthenticationRequest;
import com.attendance_system.request.RegisterRequest;
import com.attendance_system.request.ResetPasswordRequest;
import com.attendance_system.request.UpdateUserRequest;
import com.attendance_system.response.AuthenticationResponse;
import com.attendance_system.role.Role;
import com.attendance_system.role.Roles;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.attendance_system.role.Role.FACILITATOR;
import static com.attendance_system.role.Role.RECEPTION;

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



    public String createUser(RegisterRequest request, Roles role) throws MessagingException {
        String password = passwordGenerator.generatePassword(12);

        var user = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .passwordResetRequired(true)
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .role(role.getRole())
                .build();
         userRepository.save(user);
         emailService.sendMailWithTemporaryPassword(
                 user.getEmail(),
                 user.getFirstName(),
                 password
         );
        return "User created successfully";
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

            return user.isPasswordResetRequired() ? AuthenticationResponse.builder()
                        .passwordResetRequired(user.isPasswordResetRequired())
                        .build() : AuthenticationResponse.builder()
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

        validateRequest(email, savedToken);

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



    // getting user with email
    public User getUserByEmail(String email) {
      return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

    }

    public Page<User> getAllNsps(Pageable pageable) {
        return userRepository.findByRole(Role.NSP, pageable);

    }

    @Transactional
    public String grantReceptionPrivilege(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getRole().equals(FACILITATOR))
            return "Only facilitators should be granted reception privilege";

        user.setRole(RECEPTION);
        userRepository.save(user);
        return "Reception privilege granted";
    }

    @Transactional
    public String revokeReceptionPrivilege(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getRole().equals(RECEPTION))
            return "Only receptionists should have reception privilege revoked";

        user.setRole(FACILITATOR);
        userRepository.save(user);
        return "Reception privilege revoked";
    }

    //getting a facilitator
    public Page<User> getAllFacilitators(Pageable pageable) {
        return userRepository.findByRole(FACILITATOR, pageable);
    }


    private void validateRequest(String email, Token savedToken) {
        if (!email.equals(savedToken.getUser().getEmail()))
            throw new InvalidTokenException("Invalid token");

        if (savedToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new TokenExpiredException("Token is expired.");
    }

}