package com.example.atom.services;

import com.example.atom.dto.DemoDto;
import com.example.atom.entities.Demo;
import com.example.atom.entities.Process;
import com.example.atom.entities.User;
import com.example.atom.repositories.DemoRepository;
import com.example.atom.repositories.ProcessRepository;
import com.example.atom.repositories.ProcessStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;

    private final ProcessStageRepository processStageRepository;

    private final UserService userService;

    private final DemoRepository demoRepository;

    public void startProcess(DemoDto demoDto) {
        User user = userService.getCurrentUser();
        Demo demo = demoRepository.findById(demoDto.getId()).orElse(null);

        if (user == null || demo == null) {
            throw new RuntimeException("User is undefined or Business object not found in database!");
        } else {
            Process process = new Process();
            process.createProcess(user, processStageRepository, demo);
            processRepository.save(process);
        }
    }
}
