package com.example.atom.services;

import com.example.atom.dto.ProductionPlanStatus;
import com.example.atom.dto.RequestPositionDto;
import com.example.atom.entities.ProductionPlan;
import com.example.atom.entities.ProductionTask;
import com.example.atom.entities.ProductionTaskQueue;
import com.example.atom.entities.Request;
import com.example.atom.readers.RequestReader;
import com.example.atom.repositories.ProductionPlanRepository;
import com.example.atom.repositories.ProductionTaskQueueRepository;
import com.example.atom.repositories.ProductionTaskRepository;
import com.example.atom.repositories.RequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionTaskService {

    private final ProductionTaskQueueRepository productionTaskQueueRepository;

    private final ProductionPlanRepository productionPlanRepository;

    private final ProductionTaskRepository productionTaskRepository;

    private final RequestRepository requestRepository;

    private final RequestReader requestReader;

    private final RequestService requestService;

    @Transactional
    public void createProductionTask(@PathVariable Long productionId) {
        ProductionPlan productionPlan = productionPlanRepository
                .findById(productionId)
                .orElseThrow(() ->
                        new RuntimeException("Позиция плана не найдена в локальном справочнике"));

        Request request = requestRepository.findById(productionPlan.getRequestId()).orElseThrow(() ->
                new RuntimeException("Заявка не найдена в локальном справочнике"));

        // создать сущность заказ-наряда
        ProductionTask productionTask = new ProductionTask(productionPlan.getId(), request);
        productionTask = productionTaskRepository.save(productionTask);

        // добавить в очередь заказ наряд
        this.putTaskToProductionTaskQueue(productionTask);

        // send CRM
        this.requestService.changeRequestStatusInProductionCrm(request.getId());

        productionPlan.setProductionPlanStatus(ProductionPlanStatus.IN_PRODUCTION);
    }

    private void putTaskToProductionTaskQueue(ProductionTask productionTask) {
        // get item of request
        List<RequestPositionDto> positionDtos =
                requestReader.getRequestPositionById(productionTask.getRequestId());

        List<ProductionTaskQueue> queue = new ArrayList<>();
        for (RequestPositionDto positionDto : positionDtos) {
            ProductionTaskQueue productionTaskQueue = new ProductionTaskQueue(productionTask, positionDto);
            queue.add(productionTaskQueue);
        }
        productionTaskQueueRepository.saveAll(queue);
    }

}
