package com.example.atom.controllers;

import com.example.atom.dto.ProductionPlanStatus;
import com.example.atom.entities.ProductionPlan;
import com.example.atom.entities.ProductionTask;
import com.example.atom.entities.Request;
import com.example.atom.repositories.ProductionPlanRepository;
import com.example.atom.repositories.RequestRepository;
import com.example.atom.services.ProductionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("production")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductionController {

    private final ProductionTaskService productionTaskService;

    @PostMapping("plan/{id}/task")
    @Transactional
    public void createProductionTask(@PathVariable Long productionId) {
        productionTaskService.createProductionTask(productionId);
    }
}
