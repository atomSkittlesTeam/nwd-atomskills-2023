package com.example.atom.services;

import com.example.atom.entities.ProcessStage;
import com.example.atom.repositories.ProcessStageRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessStageService {

    private final ProcessStageRepository processStageRepository;

    @PostConstruct
    public void initializeProcessStages() {
        List<ProcessStage> stages = processStageRepository.findAll();
        if (stages.isEmpty()) {
            this.generateProcessStages();
        }
    }

    private void generateProcessStages() {
        ArrayList<String> roles = new ArrayList<>();
        roles.add("admin");

        List<ProcessStage> stages = new ArrayList<>();

        ProcessStage processStage1 = new ProcessStage("FIRST_STAGE", "Первый этап", roles);
        stages.add(processStage1);

        ProcessStage processStage2 = new ProcessStage("SECOND_STAGE", "Второй этап", roles);
        stages.add(processStage2);

        ProcessStage processStage3 = new ProcessStage("THIRD_STAGE", "Третий этап", roles);
        stages.add(processStage3);

        processStageRepository.saveAll(stages);
    }
}
