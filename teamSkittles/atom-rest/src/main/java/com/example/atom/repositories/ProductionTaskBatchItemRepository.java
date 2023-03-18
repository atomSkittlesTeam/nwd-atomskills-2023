package com.example.atom.repositories;

import com.example.atom.entities.ProductionTaskBatchItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionTaskBatchItemRepository extends JpaRepository<ProductionTaskBatchItem, Long> {
    List<ProductionTaskBatchItem> findAllByBatchId(Long batchId);

//    List<ProductionTaskBatchItem> findAllByLatheMachineCodeOrderByMillingMachineCode(String machineCode);
    List<ProductionTaskBatchItem> findAllByLatheMachineCodeOrMillingMachineCode(String machineCode, String machineCode2);
    List<ProductionTaskBatchItem> findAllBySummaryWorkingTimeProductIsNullOrderByIdAsc();
}
