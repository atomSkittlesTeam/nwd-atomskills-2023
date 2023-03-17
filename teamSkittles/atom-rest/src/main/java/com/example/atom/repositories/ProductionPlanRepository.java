package com.example.atom.repositories;

import com.example.atom.entities.ProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {
}
