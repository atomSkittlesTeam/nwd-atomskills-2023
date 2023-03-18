package com.example.atom.readers;

import com.example.atom.entities.ProductionTask;
import com.example.atom.entities.ProductionTaskQueue;
import com.example.atom.repositories.ProductionTaskQueueRepository;
import com.example.atom.repositories.ProductionTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductionTaskReader {

    private final ProductionTaskRepository productionTaskRepository;

    private final ProductionTaskQueueRepository productionTaskQueueRepository;

    public List<ProductionTask> getAllProductionTasks() {
        return productionTaskRepository.findAll();
    }

    public List<ProductionTaskQueue> getProductionTaskItems(Long productionTaskId) {
        return productionTaskQueueRepository.findAllByProductionTaskId(productionTaskId);
    }
}
