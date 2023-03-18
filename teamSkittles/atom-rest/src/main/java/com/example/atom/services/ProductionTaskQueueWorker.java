package com.example.atom.services;

import com.example.atom.dto.MachineDto;
import com.example.atom.dto.MachineTaskDto;
import com.example.atom.entities.MachineState;
import com.example.atom.entities.MachineType;
import com.example.atom.entities.ProductionTaskBatch;
import com.example.atom.entities.ProductionTaskBatchItem;
import com.example.atom.readers.MachineReader;
import com.example.atom.repositories.ProductionTaskBatchItemRepository;
import com.example.atom.repositories.ProductionTaskBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionTaskQueueWorker {

    private final ProductionTaskBatchItemRepository productionTaskBatchItemRepository;

    private final ProductionTaskBatchRepository productionTaskBatchRepository;

    private final MachineService machineService;

    private final MachineReader machineReader;

    @Scheduled(fixedDelayString = "${scheduled.queue-view}")
    @Transactional
    private void monitorQueue() {
        System.out.println("Запущен мониторинг очереди на станки...");

        // get all waiting machines
        List<MachineDto> waitingMachines = machineService.getAllWaitingMachines();
        Map<MachineType, List<MachineDto>> machineDtoMap =
                waitingMachines
                .stream()
                .collect(Collectors.groupingBy(MachineDto::getMachineType));

        if (waitingMachines.isEmpty()) {
            System.out.println("Нет свободных станков!");
        } else {
            List<ProductionTaskBatchItem> productionQueue = productionTaskBatchItemRepository
                    .findAllBySummaryWorkingTimeProductIsNullOrderByIdAsc();

            List<ProductionTaskBatch> productionTaskBatchesForItems = productionTaskBatchRepository
                    .findAllById(productionQueue.stream()
                            .map(ProductionTaskBatchItem::getBatchId)
                            .distinct()
                            .toList()
                    );

            Map<Long, ProductionTaskBatch> productionTaskBatchMap = productionTaskBatchesForItems.stream()
                    .collect(Collectors.toMap(ProductionTaskBatch::getId, Function.identity()));

            if (!productionQueue.isEmpty()) {
                for (ProductionTaskBatchItem productionTask : productionQueue) {
                    // проверяем нужно точить или фрезеровать

                    ProductionTaskBatch productionTaskBatch = productionTaskBatchMap.get(productionTask.getBatchId());
                    // проверяем не начали ли точить
                    if (productionTask.getLatheStartTimestamp() == null) {
                        // точим если есть доступные станки для точения
                        List<MachineDto> latheMachineDtoList = machineDtoMap.get(MachineType.lathe);
                        MachineDto firstFoundedLatheMachine = latheMachineDtoList.get(0);
                        // отправляем на станок
                        this.sendOnMachine(firstFoundedLatheMachine,
                                productionTaskBatch.getProductId(),
                                productionTask.getBatchId(),
                                productionTask.getId(),
                                productionTaskBatch.getProductionTaskId()
                                );
                        // проверяем не начали ли фрезеровать
                    } else if (productionTask.getMillingStartTimestamp() == null) {
                        // фрезеруем, если есть доступные
                        List<MachineDto> millingMachineDtoList = machineDtoMap.get(MachineType.milling);
                        MachineDto firstFoundedMillingMachine = millingMachineDtoList.get(0);
                        this.sendOnMachine(firstFoundedMillingMachine,
                                productionTaskBatch.getProductId(),
                                productionTask.getBatchId(),
                                productionTask.getId(),
                                productionTaskBatch.getProductionTaskId()
                        );
                    }
                }
            } else {
                System.out.println("Отличная работа! В очереди нет заказ-нарядов!");
            }
        }
    }

    private void sendOnMachine(MachineDto machineDto,
                               Long productId,
                               Long batchId,
                               Long batchItemId,
                               Long productionTaskId) {
        MachineTaskDto machineTaskDto = new MachineTaskDto(productId, batchId, batchItemId, productionTaskId);
        machineReader.setStatusToMachine(machineDto.getPort(), MachineState.WORKING, machineTaskDto);
    }
}
