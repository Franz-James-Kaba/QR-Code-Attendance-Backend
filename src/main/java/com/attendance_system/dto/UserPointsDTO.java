// UserPointsDTO.java
package com.attendance_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPointsDTO {
    @JsonIgnore
    private Long userId;
    private String firstName;
    private String lastName;
    private Integer totalPoints;
    private Integer position; // Rank based on points

//    public UserPointsDTO(Long userId, String firstName, String lastName, Integer totalPoints, Integer position) {
//        this.userId = userId;
//        this.totalPoints = totalPoints;
//        this.position = position;
//        this.lastName = lastName;
//        this.firstName = firstName;
//    }
}