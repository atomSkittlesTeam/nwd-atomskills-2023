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

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(
            name = "authorities",
            joinColumns = {@JoinColumn(name = "login")})
    @Column(name = "authority")
    private List<String> roles;

    public void update(UserDto userDto) {
        this.login = userDto.login;
        this.roles = userDto.getRole();
    }
}
