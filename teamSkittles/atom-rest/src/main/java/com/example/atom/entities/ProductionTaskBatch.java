package com.example.atom.entities;

import com.example.atom.dto.RequestPositionDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class ProductionTaskBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    // id заказ наряда
    private Long productionTaskId;

    private String productName;

    private Long requestPositionId;

    private Integer quantity;

    private Integer quantityExec = 0;

    /* точильная на 1 деталь по справочнику */
    private Integer latheTime;

    /* фрезерная на 1 деталь по справочнику */
    private Integer millingTime;

    private Integer summaryWorkingTimeBatch;

    private Instant startBatchTime;

    private Instant endBatchTime;

    public ProductionTaskBatch(Long productionTaskId, RequestPositionDto positionDto) {
        this.productionTaskId = productionTaskId;
        this.productName = positionDto.getProduct().getCaption();
        this.requestPositionId = positionDto.getId();
        this.quantity = positionDto.getQuantity();
        this.latheTime = positionDto.getProduct().getLatheTime();
        this.millingTime = positionDto.getProduct().getMillingTime();
    }
}
