package com.example.atom.dto;

import com.example.atom.entities.User;

import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
public class UserDto {
    public String login;
    private List<String> role;

    public UserDto(User user) {
        this.login = user.getLogin();
        this.role = user.getRoles();
    }
}