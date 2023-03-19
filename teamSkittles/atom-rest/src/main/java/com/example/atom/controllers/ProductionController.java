package com.example.atom.controllers;

import com.example.atom.dto.MachineDto;
import com.example.atom.dto.MachineHistoryDto;
import com.example.atom.entities.*;
import com.example.atom.readers.MachineReader;
import com.example.atom.readers.ProductionTaskReader;
import com.example.atom.services.MachineService;
import com.example.atom.services.ProductionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("production")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductionController {

    private final ProductionTaskService productionTaskService;

    private final ProductionTaskReader productionTaskReader;

    private final MachineReader machineReader;

    private final MachineService machineService;

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

    @GetMapping("all-machines")
    public List<MachineDto> allMachineNames() {
        List<MachineDto> machinesList = new ArrayList<>();
        LinkedHashMap<MachineType, LinkedHashMap<String, Integer>> map = machineReader.getAllMachines();
        map.values().forEach((v) -> {
            v.forEach((k,value) -> {
                MachineDto machineDto = new MachineDto();
                machineDto.setCode(k);
                machineDto.setPort(value);
                machinesList.add(machineDto);
            });
        });
        return machinesList;
    }

    @GetMapping("all-batch-items-by-machine/{machineCode}")
    public List<ProductionTaskBatchItem> getAllItemsByMachineCode(@PathVariable String machineCode) {
        return productionTaskReader.getProductionBatchItemsByMachineCode(machineCode);
    }

    @GetMapping("history/{port}")
    public List<MachineHistoryDto> getAllHistoryOfMachineByCode(@PathVariable int port) {
        List<MachineHistoryDto> historyDtos = new ArrayList<>();
        historyDtos = machineService.getHistoryForMachines(port);
        return historyDtos;
    }

    @GetMapping("analytics/{port}")
    public Map<String, Integer> getAnalyticsByMachinePort(@PathVariable int port) {
        List<MachineHistoryDto> historyDtos = new ArrayList<>();
        historyDtos = machineService.getHistoryForMachines(port);
        List<MachineHistoryDto> brokenHistoryList = historyDtos.stream().filter(e ->
                e.getState().getCode().equals(MachineState.BROKEN.toString())).toList();
        List<MachineHistoryDto> waitingHistoryList = historyDtos.stream().filter(e ->
                e.getState().getCode().equals(MachineState.WAITING.toString())).toList();
        List<MachineHistoryDto> repairingHistoryList = historyDtos.stream().filter(e ->
                e.getState().getCode().equals(MachineState.REPAIRING.toString())).toList();
        List<MachineHistoryDto> workingHistoryList = historyDtos.stream().filter(e ->
                e.getState().getCode().equals(MachineState.WORKING.toString())).toList();
        Map<String, Integer> mapTimeByState = new HashMap<>();
        mapTimeByState.put("Поломка", getTimeOfStateByHistoryDto(brokenHistoryList));
        mapTimeByState.put("Ожидание", getTimeOfStateByHistoryDto(waitingHistoryList));
        mapTimeByState.put("Ремонт", getTimeOfStateByHistoryDto(repairingHistoryList));
        mapTimeByState.put("Работа", getTimeOfStateByHistoryDto(workingHistoryList));
        return mapTimeByState;
    }

    private Integer getTimeOfStateByHistoryDto(List<MachineHistoryDto> list) {
        List<Long> times = list.stream().map(e -> {
            if (e.getEndDateTime() != null) {
                return (e.getEndDateTime().getTime() - e.getBeginDateTime().getTime()) / 1000;
            } else {
                return (Date.from(Instant.now()).getTime()  - (e.getBeginDateTime().getTime() - 180 * 60 * 1000)) / 1000;
            }
        }).toList();
        return times.stream().mapToInt(Long::intValue).sum();
    }

    @GetMapping("analytics-all")
    public Map<String, Integer> getAnalyticsForAllMachines() {
        List<MachineDto> machinesList = new ArrayList<>();
        LinkedHashMap<MachineType, LinkedHashMap<String, Integer>> map = machineReader.getAllMachines();
        map.values().forEach((v) -> {
            v.forEach((k,value) -> {
                MachineDto machineDto = new MachineDto();
                machineDto.setCode(k);
                machineDto.setPort(value);
                machinesList.add(machineDto);
            });
        });
        Map<String, Integer> mapNumberOfItemsByMachineCode = new HashMap<>();
        for (MachineDto dto : machinesList) {
            mapNumberOfItemsByMachineCode.put(dto.getCode(),
                    productionTaskReader.getProductionBatchItemsByMachineCode(dto.getCode()).size());
        }
        return mapNumberOfItemsByMachineCode;
    }
}
