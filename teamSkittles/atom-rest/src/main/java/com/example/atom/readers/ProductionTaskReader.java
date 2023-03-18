package com.example.atom.readers;

import com.example.atom.entities.ProductionTask;
import com.example.atom.entities.ProductionTaskBatchItem;
import com.example.atom.entities.ProductionTaskBatch;
import com.example.atom.repositories.ProductionTaskBatchRepository;
import com.example.atom.repositories.ProductionTaskBatchItemRepository;
import com.example.atom.repositories.ProductionTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductionTaskReader {

    private final ProductionTaskRepository productionTaskRepository;

    private final ProductionTaskBatchItemRepository productionTaskBatchItemRepository;

    private final ProductionTaskBatchRepository productionTaskBatchRepository;


    public List<ProductionTask> getAllProductionTasks() {
        return productionTaskRepository.findAll();
    }

    public List<ProductionTaskBatch> getProductionTaskBatches(Long productionPlanId) {
        return productionTaskBatchRepository.findAllByProductionTaskId(productionPlanId);
    }

    public List<ProductionTaskBatchItem> getProductionBatchItems(Long batchId) {
        return productionTaskBatchItemRepository.findAllByBatchId(batchId);
    }
}
