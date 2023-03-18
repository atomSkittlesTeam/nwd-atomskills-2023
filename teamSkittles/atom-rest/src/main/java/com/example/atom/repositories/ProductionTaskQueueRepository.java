package com.example.atom.repositories;

import com.example.atom.entities.ProductionTaskQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionTaskQueueRepository extends JpaRepository<ProductionTaskQueue, Long> {
    List<ProductionTaskQueue> findAllByProductionTaskId(Long productionTaskId);
}
