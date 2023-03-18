package com.example.atom.controllers;

import com.example.atom.dto.ProductionPlanStatus;
import com.example.atom.entities.ProductionPlan;
import com.example.atom.entities.ProductionTask;
import com.example.atom.repositories.ProductionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("production")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductionController {

    private final ProductionPlanRepository productionPlanRepository;

    @PostMapping("productionPlan/{id}")
    @Transactional
    public void createProductionTask(@PathVariable Long productionId) {

        ProductionPlan productionPlan = productionPlanRepository
                .findById(productionId)
                .orElse(null);

        // send CRM

        // send на станки

        // создать сущность заказ-наряда
        ProductionTask productionTask = new ProductionTask();


        productionPlan.setProductionPlanStatus(ProductionPlanStatus.IN_PRODUCTION);

    }


}
