package com.example.atom.entities;

import com.example.atom.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @Column(nullable = false, unique = true)
    public String login;
    private String password;
    private String fullName;
    private String email;

    @JoinTable(
            name = "authorities",
            joinColumns = {@JoinColumn(name = "login")})
    @Column(name = "authority")
    private String role;

    public void update(UserDto userDto) {
        this.login = userDto.login;
        this.role = userDto.getRole();
        this.email = userDto.getEmail();
        this.fullName = userDto.getFullName();
    }
}
