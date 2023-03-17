package com.example.atom.controllers;


import com.example.atom.dto.UserDto;
import com.example.atom.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("all")
    public List<UserDto> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping("{login}/roles")
    public List<String> getUserRoles(@PathVariable String login) {
        return userService.getUserRole(login);
    }

    @PutMapping("update")
    public UserDto updateUser(@RequestBody UserDto user) {
        return userService.updateUser(user);
    }
}
