package com.example.atom.repositories;

import com.example.atom.entities.ProcessStage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessStageRepository extends JpaRepository<ProcessStage,Long> {
    ProcessStage findByStageName(String stageName);
}
