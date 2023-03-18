package com.example.atom.services;

import com.example.atom.dto.*;
import com.example.atom.entities.*;
import com.example.atom.readers.MachineReader;
import com.example.atom.repositories.ProductionTaskBatchItemRepository;
import com.example.atom.repositories.ProductionTaskBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
                for (ProductionTaskBatchItem productionTask : productionQueue) {
                    // проверяем нужно точить или фрезеровать

                    ProductionTaskBatch productionTaskBatch = productionTaskBatchMap.get(productionTask.getBatchId());
                    // проверяем не начали ли точить
                    if (productionTask.getLatheStartTimestamp() == null) {
                        // точим если есть доступные станки для точения
                        if (latheMachineDtoList != null && !latheMachineDtoList.isEmpty()) {
                            MachineDto firstFoundedLatheMachine = latheMachineDtoList.get(0);
                            // отправляем на станок
                            this.sendOnMachine(firstFoundedLatheMachine,
                                    productionTaskBatch.getProductId(),
                                    productionTask.getBatchId(),
                                    productionTask.getId(),
                                    productionTaskBatch.getProductionTaskId()
                            );
                            productionTask.setLatheStartTimestamp(Instant.now());
                            productionTaskBatchItemRepository.save(productionTask);
                            latheMachineDtoList = latheMachineDtoList.stream().filter(e -> !e.getId().equals(firstFoundedLatheMachine.getId())).toList();
                        }
                        // проверяем не начали ли фрезеровать
                    } else if (productionTask.getMillingStartTimestamp() == null) {
                        // фрезеруем, если есть доступные
                        if (millingMachineDtoList != null && !millingMachineDtoList.isEmpty()) {
                            MachineDto firstFoundedMillingMachine = millingMachineDtoList.get(0);
                            this.sendOnMachine(firstFoundedMillingMachine,
                                    productionTaskBatch.getProductId(),
                                    productionTask.getBatchId(),
                                    productionTask.getId(),
                                    productionTaskBatch.getProductionTaskId()
                            );
                            productionTask.setMillingStartTimestamp(Instant.now());
                            productionTaskBatchItemRepository.save(productionTask);
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
                .collect(Collectors.groupingBy(e -> e.getAdvInfo().getAdvInfo().getBatchItemId()));

        // список изделий для всех
        List<ProductionTaskBatchItem> productionTaskBatchItem =
                productionTaskBatchItemRepository
                .findAllById(mapForBatchItems.keySet());

        for (Map.Entry<Long, List<MachineHistoryDto>> entryForBatchItem : mapForBatchItems.entrySet()) {
            List<MachineHistoryDto> workingHistory = entryForBatchItem.getValue()
                    .stream().filter(e -> e.getState()
                            .getCode().equals(MachineState.WORKING.toString()))
                    .toList();

            Date maxBrokenDate = workingHistory.stream().filter(c ->
                c.getState().getCode().equals(MachineState.BROKEN.toString()
            ))
                    .map(MachineHistoryDto::getBeginDateTime)
                    .max(Date::compareTo)
                    .orElse(null);

            Date maxWorkingEndDate = workingHistory.stream().filter(c ->
                            c.getState().getCode().equals(MachineState.WORKING.toString()
                            ))
                    .map(MachineHistoryDto::getEndDateTime)
                    .max(Date::compareTo)
                    .orElse(null);

            if (maxWorkingEndDate != null) {
                // возможно выполнена
                if (maxBrokenDate != null && maxBrokenDate.after(maxWorkingEndDate)) {
                    // сломалась, отправить обратно в пр-во

                } else {
                    // готово
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
            dtos.addAll(machineService.getHistoryForMachines(machineDto.getPort()));
        }
        return dtos;
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
