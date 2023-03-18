package com.example.atom.services;

import com.example.atom.dto.MachineDto;
import com.example.atom.entities.ProductionTaskBatchItem;
import com.example.atom.repositories.ProductionTaskBatchItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionTaskQueueWorker {

    private final ProductionTaskBatchItemRepository productionTaskBatchItemRepository;

    private final MachineService machineService;

    @Scheduled(fixedDelay = 1000 * 60)
    @Transactional
    private void monitorQueue() {
        System.out.println("Запущен мониторинг очереди на станки...");

        // get all waiting machines
        List<MachineDto> waitingMachines = machineService.getAllWaitingMachines();

        if (waitingMachines.isEmpty()) {
            System.out.println("Нет свободных станков!");
        } else {

            List<ProductionTaskBatchItem> productionQueue = productionTaskBatchItemRepository.findAll();
            if (!productionQueue.isEmpty()) {
                for (ProductionTaskBatchItem productionTask : productionQueue) {
                    // try to send on machine

                }
            } else {
                System.out.println("Отличная работа! В очереди нет заказ-нарядов!");
            }
        }
    }

    private void sendOnMachine(ProductionTaskBatchItem productionTask) {

    }


}
