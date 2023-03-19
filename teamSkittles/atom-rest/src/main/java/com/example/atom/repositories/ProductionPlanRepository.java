package com.example.atom.repositories;

import com.example.atom.dto.ProductionPlanStatus;
import com.example.atom.entities.ProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {
    List<ProductionPlan> findAllByProductionPlanStatusEqualsOrderByPriority(ProductionPlanStatus productionPlanStatus);
}
