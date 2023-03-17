package com.example.atom.entities;

import com.example.atom.repositories.ProcessStageRepository;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class Process {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /* to join with business entity */
    private Long businessKey;

    /* foreign key for user */
    private String userLogin;

    private Instant processStartDate;

    private Instant processEndDate;

    /* foreign key for ProcessStage */
    private Long currentStageId;

    public void completeProcess() {
        this.processEndDate = Instant.now();
    }

    public void createProcess(User user, ProcessStageRepository processStageRepository, Demo demo) {
        this.userLogin = user.getLogin();
        this.processStartDate = Instant.now();
        this.currentStageId = processStageRepository.findByStageName("FIRST_STAGE").getId();
        this.businessKey = demo.getId();
    }
}
