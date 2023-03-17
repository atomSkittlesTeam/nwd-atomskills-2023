package com.example.atom.repositories;

import com.example.atom.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findByObjectId(Long objectId);
    List<Message> findAllByObjectId(Long objectId);
}
