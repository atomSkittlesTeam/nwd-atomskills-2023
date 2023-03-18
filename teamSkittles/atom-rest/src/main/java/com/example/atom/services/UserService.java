package com.example.atom.services;


import com.example.atom.dto.UserDto;
import com.example.atom.entities.Role;
import com.example.atom.entities.User;
import com.example.atom.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
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

    public String getUserRole(String login) {
        User user = userRepository.findByLogin(login);
        String userRole = user.getRole();
        return userRole;
    }

    public UserDto updateUser(UserDto userDto) {
        User currentUser = getCurrentUser();
        if (!currentUser.getRole().equals("admin")) {
            throw new RuntimeException("Необходима роль администратора!");
        }
        User user = userRepository.findByLogin(userDto.login);
        if(user == null) {
            throw new RuntimeException("Не найден пользователь");
        }
        user.update(userDto);
        if(!isValidatedOfChiefCreate(user)) {
            throw new RuntimeException("Нельзя создавать второго шефа!");
        }
        userRepository.save(user);
        return userDto;
    }

    public void deleteUser(String login) {
        User currentUser = getCurrentUser();
        if (!currentUser.getRole().equals("admin")) {
            throw new RuntimeException("Необходима роль администратора!");
        }
        User user = userRepository.findByLogin(login);
        if(user == null) {
            throw new RuntimeException("Не найден пользователь");
        }
        userRepository.delete(user);
    }

    public boolean isValidatedOfChiefCreate(User userForCreateOrUpdate) {
        Boolean validated = false;
        User chief = userRepository.findByRole("chief");
        //если есть шеф, и новый пользователь не совпадает с ним по логину, запрещаем
        if(chief != null && !chief.getLogin().equals(userForCreateOrUpdate.getLogin())
                && userForCreateOrUpdate.getRole().equals("chief")) {
            throw new RuntimeException("Нельзя создать нового начальника!");
        }
        validated = true;
        return validated;
    }

    public boolean isValidatedOfDublicateCreate(User userForCreate) {
        Boolean validated = false;
        User dublicate = userRepository.findByLogin(userForCreate.login);
        //если есть дубликат, запрещаем
        if(dublicate != null) {
            throw new RuntimeException("Логин неуникален!");
        }
        validated = true;
        return validated;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByLogin(auth.getName());
    }

    public List<String> getEmailsByRole(String role) {
        List<User> users = userRepository.findAll().stream().filter(e -> e.getRole().equals(role)).toList();
        if(users == null || users.isEmpty()) {
            new RuntimeException("Не найдены пользователи по роли");
        }
        return users.stream().map(User::getEmail).toList();
    }

    public String getEmailByLogin(String login) {
        //user обязательно один
        List<User> users = userRepository.findAll().stream().filter(e -> e.getRole().equals(login)).toList();
        if(users == null || users.isEmpty()) {
            throw new RuntimeException("Не найден пользователь по логину");
        }
        return users.get(0).getEmail();
    }


    //////////////init admin////////////////////
    @PostConstruct
    public void initializeAdmin() {
        User adminRole = userRepository.findByLogin("admin");
        if (adminRole == null) {
            this.generateAdminUser();
        }
    }

    private void generateAdminUser() {
        User admin = new User();
        admin.setLogin("admin");
        admin.setPassword("admin");
        admin.setEmail("admin@email");
        admin.setFullName("Админ Админыч Админов");
        User user = User.builder()
                .login(admin.getLogin())
                .password(passwordEncoder.encode(admin.getPassword()))
                .role("admin")
                .build();
        userRepository.save(user);
    }


}
