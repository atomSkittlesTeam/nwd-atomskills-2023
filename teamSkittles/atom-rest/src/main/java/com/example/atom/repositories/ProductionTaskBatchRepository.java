package com.example.atom.repositories;

import com.example.atom.entities.ProductionTaskBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionTaskBatchRepository extends JpaRepository<ProductionTaskBatch, Long> {
    List<ProductionTaskBatch> findAllByProductionTaskId(Long taskId);
}
