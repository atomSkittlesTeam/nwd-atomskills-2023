package com.example.atom.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    private boolean closed;

    public ProductionTask(Long productionPlanId, Request request) {
        this.productionPlanId = productionPlanId;
        this.requestId = request.getId();
        this.requestNumber = request.getNumber();
    }
}
