package com.example.atom.entities;

import com.example.atom.dto.ProductionPlanStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ProductionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private Long priority;
    private Long requestId;
    @Enumerated(EnumType.STRING)
    private ProductionPlanStatus productionPlanStatus;

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
