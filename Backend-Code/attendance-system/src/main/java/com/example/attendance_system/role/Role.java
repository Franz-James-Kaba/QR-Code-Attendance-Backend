package com.example.attendance_system.role;


import com.example.attendance_system.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique=true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

//    @PrePersist
//    protected void onCreate() {
//        if (this.createdDate == null) {
//            this.createdDate = LocalDateTime.now();
//        }
//    }

}
