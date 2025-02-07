package com.example.attendance_system.user;

import com.example.attendance_system.email.EmailService;
import com.example.attendance_system.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PasswordGenerator passwordGenerator;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;



    public void createUser(RegisterRequest request) {
        String password = passwordGenerator.generatePassword(12);


        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role does not exist"));
        var user = User.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .roles(List.of(userRole))
                .build();
         userRepository.save(user);
         emailService.sendEmail(user.getEmail(),"Password Reset", "Use this email and this password: "
                 + password + " to login and please reset the password");


    }

    public String resetPassword(String email, ResetPasswordRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User does not exist"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return "Password reset successful";
    }

    }
}
