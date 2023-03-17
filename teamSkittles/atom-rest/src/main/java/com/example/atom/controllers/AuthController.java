package com.example.atom.controllers;


import com.example.atom.entities.User;
import com.example.atom.repositories.UserRepository;
import com.example.atom.services.RoleService;
import com.example.atom.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UserRepository userRepository;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/registration")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void register(@RequestBody User newUser) {
        User user = User.builder()
                .login(newUser.getLogin())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .email(newUser.getEmail())
                .fullName(newUser.getFullName())
                .role("user")
                .build();
        if(!userService.isValidatedOfChiefCreate(user)) {
            throw new RuntimeException("Нельзя создавать второго шефа!");
        }
        userRepository.save(user);
    }

    @PostMapping("test")
    public Boolean getLogin() {
        return true;
    }
}
