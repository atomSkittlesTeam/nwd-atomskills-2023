package com.example.atom.services;


import com.example.atom.dto.UserDto;
import com.example.atom.entities.User;
import com.example.atom.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = users
                .stream()
                .map(UserDto::new)
                .collect(Collectors.toList());

        return userDtos;
    }

    public List<String> getUserRole(String login) {
        User user = userRepository.findByLogin(login);
        List<String> userRoles = user.getRoles().stream().toList();

        return userRoles;
    }

    public UserDto updateUser(UserDto userDto) {
        User user = userRepository.findByLogin(userDto.login);
        user.update(userDto);
        userRepository.save(user);
        return userDto;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByLogin(auth.getName());
    }


}
