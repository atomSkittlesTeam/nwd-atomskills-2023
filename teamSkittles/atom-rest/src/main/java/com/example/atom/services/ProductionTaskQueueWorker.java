package com.example.atom.services;

import com.example.atom.dto.*;
import com.example.atom.entities.*;
import com.example.atom.readers.MachineReader;
import com.example.atom.repositories.ProductionTaskBatchItemRepository;
import com.example.atom.repositories.ProductionTaskBatchRepository;
import com.example.atom.repositories.ProductionTaskRepository;
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

    private final ProductionTaskRepository productionTaskRepository;

    private final RequestService requestService;

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
                    } else if (batchItem.getLatheFinishedTimestamp() != null
                            && batchItem.getMillingStartTimestamp() == null) {
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
    public void updateProductionPlanBatchState() {
        List<MachineHistoryDto> history = this.getAllStatusesMachinesHistory();

        Map<String, List<MachineHistoryDto>> machineHistoryMap = history.stream()
                .collect(Collectors.groupingBy(MachineHistoryDto::getCode));


        Map<Long, List<MachineHistoryDto>> mapForBatchItems = history.stream()
                .filter(e -> e.getAdvInfo() != null && e.getAdvInfo().getAdvInfo() != null
                        && e.getAdvInfo().getAdvInfo().getBatchItemId() != null)
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
            ProductionTaskBatchItem batchItem = productionTaskBatchItemMap.get(entryForBatchItem.getKey());
            if (batchItem != null) {
                this.executeByStates(batchItem, historyForBatchItem, machineHistoryMap);
            }
        }
    }

    @Transactional
    public void executeByStates(ProductionTaskBatchItem batchItem,
                                 List<MachineHistoryDto> historyForBatchItem,
                                 Map<String, List<MachineHistoryDto>> machineHistoryMap) {
        // изменения по изделию
        List<MachineHistoryDto> historyForBatchItemLathe = historyForBatchItem.stream()
                .filter(e -> e.getMachineType().equals(MachineType.lathe)).toList();

        List<MachineHistoryDto> historyForBatchItemMilling = historyForBatchItem.stream()
                .filter(e -> e.getMachineType().equals(MachineType.milling)).toList();

        this.applyChanges(historyForBatchItemLathe, batchItem, MachineType.lathe, machineHistoryMap);
        this.applyChanges(historyForBatchItemMilling, batchItem, MachineType.milling, machineHistoryMap);
    }

    @Transactional
    public void applyChanges(List<MachineHistoryDto> story,
                              ProductionTaskBatchItem batchItem,
                              MachineType machineType,
                              Map<String, List<MachineHistoryDto>> machineHistoryMap) {

        boolean lathe = machineType.equals(MachineType.lathe);

//        Date maxBrokenDate = story.stream().filter(c ->
//                        c.getState().getCode().equals(MachineState.BROKEN.toString()
//                        ))
//                .map(MachineHistoryDto::getBeginDateTime)
//                .filter(Objects::nonNull)
//                .max(Date::compareTo)
//                .orElse(null);

        MachineHistoryDto machineHistoryWithMaxWorkingEndDate =
                story.stream()
                        .filter(e -> e.getEndDateTime() != null)
                        .max(Comparator.comparing(MachineHistoryDto::getEndDateTime))
                        .orElse(null);

        if (machineHistoryWithMaxWorkingEndDate != null) {
            List<MachineHistoryDto> machineHistory = machineHistoryMap
                    .get(machineHistoryWithMaxWorkingEndDate.getCode());

            boolean broken = this.wasBroken(machineHistory, machineHistoryWithMaxWorkingEndDate);

            if (broken) {
                // сломалась, отправить обратно в пр-во
                if (lathe) {
                    batchItem.setLatheStartTimestamp(null);
                    batchItem.setLatheMachineCode(null);
                } else {
                    batchItem.setMillingStartTimestamp(null);
                    batchItem.setMillingMachineCode(null);
                }
                productionTaskBatchItemRepository.save(batchItem);
            } else if (machineHistoryWithMaxWorkingEndDate != null) {
                // готово
                if (lathe) {
                    batchItem.setLatheFinishedTimestamp(machineHistoryWithMaxWorkingEndDate.getEndDateTime().toInstant());
                    Duration res = Duration.between(batchItem.getLatheStartTimestamp(),
                            batchItem.getLatheFinishedTimestamp());
                    batchItem.setLatheFactTime(res.getNano());
                    productionTaskBatchItemRepository.save(batchItem);
                } else {
                    batchItem.setMillingFinishedTimestamp(machineHistoryWithMaxWorkingEndDate.getEndDateTime().toInstant());
                    Duration res = Duration.between(batchItem.getMillingStartTimestamp(),
                            batchItem.getMillingFinishedTimestamp());
                    batchItem.setMillingFactTime(res.getNano());
                    ProductionTaskBatch productionTaskBatch = productionTaskBatchRepository
                            .findById(batchItem.getBatchId()).orElse(null);
                    productionTaskBatchItemRepository.save(batchItem);
                    this.checkProductionTaskComplete(batchItem, productionTaskBatch);
                    this.putToCrm(batchItem, productionTaskBatch);
                }
            }
        }
    }

    private boolean wasBroken(List<MachineHistoryDto> list,
                              MachineHistoryDto currentHistoryElement) {
        list.sort(Comparator.comparing(MachineHistoryDto::getBeginDateTime));
        int i = list.indexOf(currentHistoryElement);
        MachineHistoryDto machineHistoryDto = list.get(i + 1);
        return machineHistoryDto.getState().getCode().equals(MachineState.BROKEN.toString());
    }

    private void putToCrm(ProductionTaskBatchItem batchItem, ProductionTaskBatch productionTaskBatch) {
        ProductionTask productionTask = productionTaskRepository.findById(
                productionTaskBatch.getProductionTaskId())
                .orElse(null);
        this.requestService.sendToCrmCountForBatchItem(
                productionTask.getRequestId(),
                productionTaskBatch.getRequestPositionId(),
                1L
        );
    }

    private List<MachineHistoryDto> getAllStatusesMachinesHistory() {
        // get all statuses machines
        List<MachineDto> machineDtos = machineService.getMachinesByStatus(null);
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

    private void checkProductionTaskComplete(ProductionTaskBatchItem batchItem,
                                             ProductionTaskBatch productionTaskBatch) {
        // проверить выполнилась ли партия
        List<ProductionTaskBatch> allBatchesInTask = productionTaskBatchRepository
                .findAllByProductionTaskId(productionTaskBatch.getProductionTaskId());

        boolean isProductionTaskComplete = this.isProductionTaskComplete(productionTaskBatch,
                allBatchesInTask);
        if (isProductionTaskComplete) {
            Instant maxEndBatchTime = allBatchesInTask.stream()
                    .map(ProductionTaskBatch::getEndBatchTime)
                    .max(Instant::compareTo)
                    .orElse(null);
            this.completeProductionTask(productionTaskBatch.getProductionTaskId(), maxEndBatchTime);
        }

        productionTaskBatch.completeBatchItem(batchItem.getMillingFinishedTimestamp());
    }


    private boolean isProductionTaskComplete(ProductionTaskBatch productionTaskBatch,
                                             List<ProductionTaskBatch> allBatchesInTask) {
        boolean allBatchesComplete = true;
        for (ProductionTaskBatch taskBatch : allBatchesInTask) {
            if (taskBatch.getEndBatchTime() == null) {
                allBatchesComplete = false;
                break;
            }
        }
        return allBatchesComplete;
    }

    private void completeProductionTask(Long taskId, Instant closeDate) {
        ProductionTask productionTask = productionTaskRepository
                .findById(taskId).orElse(null);
        productionTask.setCloseDate(closeDate);
        productionTaskRepository.save(productionTask);
    }

    @Scheduled(fixedDelayString = "${scheduled.repairing-machines}")
    public void getAllRepairingMachines() {
        //смотрю всю историю всех станков, вычисляю, не пришел ли ко мне починенный станок
        List<MachineHistoryDto> listHistory = this.getAllStatusesMachinesHistory();
//        Map<String, MachineHistoryDto> mapHistoryByCode =
//                listHistory.stream().collect(Collectors.toMap(MachineHistoryDto::getCode, e -> e));
        Map<String, List<MachineHistoryDto>> mapHistoryListByCode =
                listHistory.stream().collect(Collectors.groupingBy(MachineHistoryDto::getCode, Collectors.toList()));
        mapHistoryListByCode.forEach((k, list) -> {
            list.sort(Comparator.comparing(MachineHistoryDto::getBeginDateTime).reversed()); //сначала должны идти новые
            MachineHistoryDto lastRepaired = list.stream().filter(e -> {
                return (e.getState().getCode().equals(MachineState.REPAIRING.toString()) &&
                        e.getBeginDateTime() != null && e.getEndDateTime() != null);
            }).findFirst().orElse(null);
            if (lastRepaired != null) {
                machineService.saveMessageOfMachine(lastRepaired.getId(), lastRepaired.getCode(),
                        lastRepaired.getBeginDateTime(), Types.machineRepair);
            }
        });
        machineService.sendEmailOfRepairedMachines();
        System.out.println("Дернул все станки на починку");
    }
}
