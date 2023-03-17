package com.example.atom.entities;

import com.example.atom.dto.Types;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private Types type;
    private Boolean emailSign;
    private Boolean frontSign;
    private Long objectId; //либо request, либо machine
    private String objectName; //для request - number
    private Instant instant; //дата появления сообщения

    public Message(Types type, Boolean emailSign,
                   Boolean frontSign, Long objectId, String objectName) {
        this.type = type;
        this.emailSign = emailSign;
        this.frontSign = frontSign;
        this.objectId = objectId;
        this.objectName = objectName;
        this.instant = Instant.now();
    }
}
