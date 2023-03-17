package com.example.atom.configs;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsMapper {

    public UserDetails toUserDetails(com.example.atom.entities.User userCredentials) {

        return User.withUsername(userCredentials.getLogin())
                .password(userCredentials.getPassword())
                .roles(userCredentials.getRoles().toArray(String[]::new))
                .build();
    }
}