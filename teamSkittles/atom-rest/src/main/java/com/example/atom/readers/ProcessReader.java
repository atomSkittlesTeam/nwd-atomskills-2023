package com.example.atom.readers;

import com.example.atom.entities.Process;
import com.example.atom.entities.User;
import com.example.atom.repositories.ProcessRepository;
import com.example.atom.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProcessReader {
    private final ProcessRepository processRepository;

    private final UserService userService;

    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }

    public List<Process> getAllProcessesForCurrentUser() {
        User user = userService.getCurrentUser();
        if (user != null) {
            String userLogin = user.getLogin();
            return processRepository.findAllByUserLogin(userLogin);
        } else {
            throw new RuntimeException("User is undefined!");
        }
    }
}
