package com.example.atom.entities;

import com.example.atom.dto.RequestStatus;
import com.example.atom.dto.Types;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class ProductionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private Long priority;

    //todo sequence counter to priority
    private Long requestId;
    private RequestStatus requestStatus;

//    public ProductionPlan(Types type, Boolean emailSign,
//                          Boolean frontSign, Long objectId, String objectName, Instant machineDate) {
//        this.type = type;
//        this.emailSign = emailSign;
//        this.frontSign = frontSign;
//        this.objectId = objectId;
//        this.objectName = objectName;
//        this.instant = Instant.now();
//        if(machineDate != null) {
//            this.machineDate = machineDate;
//        }
//    }
}
