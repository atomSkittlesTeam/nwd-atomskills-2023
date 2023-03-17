package com.example.atom.repositories;

import com.example.atom.entities.Message;
import com.example.atom.entities.RequestExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
