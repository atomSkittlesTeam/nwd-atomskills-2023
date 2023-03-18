package com.example.atom.repositories;

import com.example.atom.entities.ProductionTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionTaskRepository extends JpaRepository<ProductionTask, Long> {
}
