package com.example.atom.repositories;

import com.example.atom.entities.Process;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessRepository extends JpaRepository<Process, Long> {
    List<Process> findAllByUserLogin(String userLogin);
}
