package com.example.atom.services;

import com.example.atom.dto.AdvInfoDto;
import com.example.atom.dto.MachineDto;
import com.example.atom.dto.MachineHistoryDto;
import com.example.atom.dto.MachineTaskDto;
import com.example.atom.entities.MachineState;
import com.example.atom.entities.MachineType;
import com.example.atom.entities.ProductionTaskBatch;
import com.example.atom.entities.ProductionTaskBatchItem;
import com.example.atom.readers.MachineReader;
import com.example.atom.repositories.ProductionTaskBatchItemRepository;
import com.example.atom.repositories.ProductionTaskBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionTaskQueueWorker {

    private final ProductionTaskBatchItemRepository productionTaskBatchItemRepository;

    private final ProductionTaskBatchRepository productionTaskBatchRepository;

    private final MachineService machineService;

    private final MachineReader machineReader;

    @Scheduled(fixedDelay = 1000 * 30)
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
                List<MachineDto> latheMachineDtoList = machineDtoMap.get(MachineType.lathe);
                List<MachineDto> millingMachineDtoList = machineDtoMap.get(MachineType.milling);
                for (ProductionTaskBatchItem batchItem : productionQueue) {
                    // проверяем нужно точить или фрезеровать
                    ProductionTaskBatch productionTaskBatch = productionTaskBatchMap.get(batchItem.getBatchId());
                    // проверяем не начали ли точить
                    if (batchItem.getLatheStartTimestamp() == null) {
                        // точим если есть доступные станки для точения
                        if (latheMachineDtoList != null && !latheMachineDtoList.isEmpty()) {
                            MachineDto firstFoundedLatheMachine = latheMachineDtoList.get(0);
                            // отправляем на станок
                            this.sendOnMachine(firstFoundedLatheMachine,
                                    productionTaskBatch.getProductId(),
                                    batchItem.getBatchId(),
                                    batchItem.getId(),
                                    productionTaskBatch.getProductionTaskId()
                            );
                            batchItem.setLatheStartTimestamp(Instant.now());
                            batchItem.setLatheMachineCode(firstFoundedLatheMachine.getCode());
                            productionTaskBatch.setStartBatchTime(Instant.now());
                            productionTaskBatchItemRepository.save(batchItem);
                            productionTaskBatchRepository.save(productionTaskBatch);
                            latheMachineDtoList = latheMachineDtoList.stream().filter(e -> !e.getId()
                                    .equals(firstFoundedLatheMachine.getId())).toList();
                        }
                        // проверяем не начали ли фрезеровать
                    } else if (batchItem.getMillingStartTimestamp() == null) {
                        // фрезеруем, если есть доступные
                        if (millingMachineDtoList != null && !millingMachineDtoList.isEmpty()) {
                            MachineDto firstFoundedMillingMachine = millingMachineDtoList.get(0);
                            this.sendOnMachine(firstFoundedMillingMachine,
                                    productionTaskBatch.getProductId(),
                                    batchItem.getBatchId(),
                                    batchItem.getId(),
                                    productionTaskBatch.getProductionTaskId()
                            );
                            batchItem.setMillingStartTimestamp(Instant.now());
                            batchItem.setMillingMachineCode(firstFoundedMillingMachine.getCode());
                            productionTaskBatchItemRepository.save(batchItem);
                            millingMachineDtoList = millingMachineDtoList.stream().filter(e -> !Objects.equals(e.getId(), firstFoundedMillingMachine.getId())).toList();
                        }
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
        System.out.println("Отправляю " + productId.toString() + " на станок " + machineDto.getCode());
        MachineTaskDto machineTaskDto = new MachineTaskDto(new AdvInfoDto(productId, batchId, batchItemId, productionTaskId));
        machineReader.setStatusToMachine(machineDto.getPort(), MachineState.WORKING, machineTaskDto);
        System.out.println("!Отправлено! " + productId.toString() + " на станок " + machineDto.getCode() + "!");
    }

    @Scheduled(fixedDelay = 1000 * 60)
    @Transactional
    private void updateProductionPlanBatchState() {
        List<MachineHistoryDto> history = this.getAllStatusesMachinesHistory();

        Map<Long, List<MachineHistoryDto>> mapForBatchItems = history.stream()
                .filter(e -> e.getAdvInfo() != null && e.getAdvInfo().getAdvInfo() != null)
                .collect(Collectors.groupingBy(e -> e.getAdvInfo().getAdvInfo().getBatchItemId()));

        // список изделий для всех
        List<ProductionTaskBatchItem> productionTaskBatchItem =
                productionTaskBatchItemRepository
                .findAllById(mapForBatchItems.keySet());

        Map<Long, ProductionTaskBatchItem> productionTaskBatchItemMap = productionTaskBatchItem
                .stream()
                .collect(Collectors.toMap(ProductionTaskBatchItem::getId, Function.identity()));

        for (Map.Entry<Long, List<MachineHistoryDto>> entryForBatchItem : mapForBatchItems.entrySet()) {
            List<MachineHistoryDto> historyForBatchItem = entryForBatchItem.getValue();
            this.executeByStates(productionTaskBatchItemMap.get(entryForBatchItem.getKey()), historyForBatchItem);
        }
    }

    private void executeByStates(ProductionTaskBatchItem batchItem, List<MachineHistoryDto> historyForBatchItem) {
        // изменения по изделию
        List<MachineHistoryDto> historyForBatchItemLathe = historyForBatchItem.stream()
                .filter(e -> e.getMachineType().equals(MachineType.lathe)).toList();

        List<MachineHistoryDto> historyForBatchItemMilling = historyForBatchItem.stream()
                .filter(e -> e.getMachineType().equals(MachineType.milling)).toList();

        this.applyChanges(historyForBatchItemLathe, batchItem, MachineType.lathe);
        this.applyChanges(historyForBatchItemMilling, batchItem, MachineType.milling);
    }

    private void applyChanges(List<MachineHistoryDto> story, ProductionTaskBatchItem batchItem, MachineType machineType) {

        boolean lathe = machineType.equals(MachineType.lathe);

        Date maxBrokenDate = story.stream().filter(c ->
                        c.getState().getCode().equals(MachineState.BROKEN.toString()
                        ))
                .map(MachineHistoryDto::getBeginDateTime)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);

        Date maxWorkingEndDate = story.stream().filter(c ->
                        c.getState().getCode().equals(MachineState.WORKING.toString()
                        ))
                .map(MachineHistoryDto::getEndDateTime)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);


        if (maxBrokenDate != null && maxBrokenDate.after(maxWorkingEndDate)) {
            // сломалась, отправить обратно в пр-во
            if (lathe) {
                batchItem.setLatheStartTimestamp(null);
                batchItem.setLatheMachineCode(null);
            } else {
                batchItem.setMillingStartTimestamp(null);
                batchItem.setMillingMachineCode(null);
            }

        } else {
            if (maxWorkingEndDate != null) {
            // готово
                if (lathe) {
                    batchItem.setLatheFinishedTimestamp(maxWorkingEndDate.toInstant());
                    Duration res = Duration.between(batchItem.getLatheStartTimestamp(),
                            batchItem.getLatheFinishedTimestamp());
                    batchItem.setLatheFactTime(res.getNano());
                } else {
                    batchItem.setMillingFinishedTimestamp(maxWorkingEndDate.toInstant());
                    Duration res = Duration.between(batchItem.getMillingStartTimestamp(),
                            batchItem.getMillingFinishedTimestamp());
                    batchItem.setMillingFactTime(res.getNano());
                    // проверить выполнилась ли партия

                    ProductionTaskBatch productionTaskBatch = productionTaskBatchRepository
                            .findById(batchItem.getBatchId()).orElse(null);
                    productionTaskBatch.completeBatchItem(batchItem.getMillingFinishedTimestamp());

                }
            }
        }
    }

    private List<MachineHistoryDto> getAllStatusesMachinesHistory() {
        // get all statuses machines
        List<MachineDto> machineDtos = machineService.getMachinesByStatus(null);
        Map<MachineType, List<MachineDto>> machineDtoMap =
                machineDtos
                        .stream()
                        .collect(Collectors.groupingBy(MachineDto::getMachineType));

        List<MachineHistoryDto> dtos = new ArrayList<>();
        for (MachineDto machineDto : machineDtos) {
            List<MachineHistoryDto> list = machineService.getHistoryForMachines(machineDto.getPort());
            list.forEach(e -> {
                e.setMachineType(machineDto.getMachineType());
            });
            dtos.addAll(list);
        }
        return dtos;
    }
}
