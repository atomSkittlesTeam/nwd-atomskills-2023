package com.example.atom.services;

import com.example.atom.dto.ProductionPlanStatus;
import com.example.atom.dto.RequestPositionDto;
import com.example.atom.entities.*;
import com.example.atom.readers.RequestReader;
import com.example.atom.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionTaskService {

    private final ProductionTaskBatchItemRepository productionTaskBatchItemRepository;

    private final ProductionPlanRepository productionPlanRepository;

    private final ProductionTaskRepository productionTaskRepository;

    private final ProductionTaskBatchRepository productionTaskBatchRepository;

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

        List<ProductionTaskBatchItem> queue = new ArrayList<>();
        for (RequestPositionDto positionDto : positionDtos) {
            // создаем партию
            ProductionTaskBatch productionTaskBatch = new ProductionTaskBatch(
                    productionTask.getId(),
                    positionDto);
            productionTaskBatch = productionTaskBatchRepository.save(productionTaskBatch);

            // каждый элемент партии пихаем в очередь
            for (int i = 0; i < productionTaskBatch.getQuantity(); i++) {
                ProductionTaskBatchItem productionTaskBatchItem = new ProductionTaskBatchItem(productionTaskBatch.getId());
                queue.add(productionTaskBatchItem);
            }
        }
        productionTaskBatchItemRepository.saveAll(queue);
    }


}
