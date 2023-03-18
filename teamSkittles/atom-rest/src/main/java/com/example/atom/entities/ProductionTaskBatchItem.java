package com.example.atom.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/*

    ОЧЕРЕДЬ ЗАКАЗ НАРЯДОВ НА ЗАПУСК НА СТАНОК

 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionTaskBatchItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long batchId;

    private Instant latheStartTimestamp;

    private Instant latheFinishedTimestamp;

    // время работы на станке
    // точение
    private Integer latheFactTime;

    private Instant millingStartTimestamp;

    private Instant millingFinishedTimestamp;

    // время работы на станке
    private Integer millingFactTime;

    private Long machineId;

    private Instant summaryWorkingTimeProduct;

    public ProductionTaskBatchItem(Long batchId) {
        this.batchId = batchId;
    }
}
