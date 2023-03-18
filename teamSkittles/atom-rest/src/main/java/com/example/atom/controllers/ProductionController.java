package com.example.atom.controllers;

import com.example.atom.entities.ProductionTask;
import com.example.atom.entities.ProductionTaskQueue;
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

    @GetMapping("plan/tasks/{productionTaskId}")
    @Transactional
    public List<ProductionTaskQueue> getProductionTaskItems(@PathVariable Long productionTaskId) {
        return productionTaskReader.getProductionTaskItems(productionTaskId);
    }
}
