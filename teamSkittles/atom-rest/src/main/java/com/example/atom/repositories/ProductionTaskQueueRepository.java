package com.example.atom.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionTaskQueueRepository extends JpaRepository<com.example.atom.entities.ProductionTaskQueue, Long> {
}
