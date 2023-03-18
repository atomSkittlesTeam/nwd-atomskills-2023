package com.example.atom.controllers;

import com.example.atom.entities.ProductionTask;
import com.example.atom.entities.ProductionTaskBatchItem;
import com.example.atom.entities.ProductionTaskBatch;
import com.example.atom.readers.ProductionTaskReader;
import com.example.atom.services.ProductionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("production")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductionController {

    private final ProductionTaskService productionTaskService;

    private final ProductionTaskReader productionTaskReader;

    @PostMapping("plan/{productionId}/task")
    @Transactional
    public void createProductionTask(@PathVariable Long productionId) {
        productionTaskService.createProductionTask(productionId);
    }

    @GetMapping("plan/tasks")
    @Transactional
    public List<ProductionTask> getProductionTask() {
        return productionTaskReader.getAllProductionTasks();
    }

    @GetMapping("plan/tasks/{productionTaskId}/batches")
    @Transactional
    public List<ProductionTaskBatch> getProductionTaskBatches(@PathVariable Long productionTaskId) {
        return productionTaskReader.getProductionTaskBatches(productionTaskId);
    }

    @GetMapping("plan/tasks/{productionTaskId}/batches/{batchId}")
    @Transactional
    public List<ProductionTaskBatchItem> getProductionBatchItems(@PathVariable Long productionTaskId,
                                                                 @PathVariable Long batchId) {
        return productionTaskReader.getProductionBatchItems(batchId);
    }

    @GetMapping("all-tasks")
    public List<ProductionTask> getProductionTasks() {
        return productionTaskService.getAllProductionTasks();
    }

    @GetMapping("all-batches/{id}")
    public List<ProductionTaskBatch> getProductionTaskBatchesByTaskId(@PathVariable("id") Long id) {
        return productionTaskReader.getProductionTaskBatches(id);
//        return productionTaskService.getAllProductionTaskBatchesByTaskId(id);
    }

    @GetMapping("all-batch-items/{id}")
    public List<ProductionTaskBatchItem> getBatchItemsByBatchId(@PathVariable("id") Long id) {
        return productionTaskReader.getProductionBatchItems(id);
    }
}
