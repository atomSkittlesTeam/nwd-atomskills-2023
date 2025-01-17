package com.example.atom.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;


/*
    ЗАКАЗ НАРЯД

 */
@Entity
@Data
@NoArgsConstructor
public class ProductionTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    // id позиции плана
    private Long productionPlanId;

    // id заказа
    private Long requestId;

    // номер заказа
    private String requestNumber;

    @CreationTimestamp
    private Instant creationDate;

    private Instant closeDate;

    public ProductionTask(Long productionPlanId, Request request) {
        this.productionPlanId = productionPlanId;
        this.requestId = request.getId();
        this.requestNumber = request.getNumber();
    }
}
