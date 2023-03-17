package com.example.atom.repositories;

import com.example.atom.entities.RequestExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<RequestExtension, Long> {
}
