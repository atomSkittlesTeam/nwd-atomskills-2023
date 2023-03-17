package com.example.atom.repositories;

import com.example.atom.entities.DemoRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository()
public interface DemoRefRepository extends JpaRepository<DemoRef, Long> {
}
