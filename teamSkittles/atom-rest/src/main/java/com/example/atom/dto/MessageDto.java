package com.example.atom.dto;

import com.example.atom.entities.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class MessageDto {
    private Long id;
    private Types type;
    private Boolean emailSign;
    private Boolean frontSign;
    private Long objectId; //либо request, либо machine
    private String objectName; //для request - number
    private Instant instant; //дата появления сообщения
    private String customText; //вдруг че пилить надо будет вручную

    public MessageDto(Message message) {
        this.id = message.getId();
        this.type = message.getType();
        this.emailSign = message.getEmailSign();
        this.frontSign = message.getFrontSign();
        this.objectId = message.getObjectId();
        this.objectName = message.getObjectName();
        this.instant = message.getInstant();
        switch (type) {
            case newRequests ->
                    this.customText = "Новая заявка! Номер заявки: " + message.getObjectName();
            case machineBroke ->
                    this.customText = "Станок сломан! Код станка: " + message.getObjectName();
            case machineRepair ->
                    this.customText = "Станок отремонтирован! Код станка: " + message.getObjectName();
        }
    }
}
