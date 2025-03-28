package com.example.attendance_system.dto;

import com.example.attendance_system.model.User;
import com.example.attendance_system.role.Role;
import lombok.Data;
import java.util.UUID;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private Role role;

    // Static method to convert User to UserDTO
    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setMiddleName(user.getMiddleName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        return dto;
    }

}