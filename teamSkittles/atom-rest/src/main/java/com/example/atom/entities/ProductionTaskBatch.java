package com.example.atom.entities;

import com.example.atom.dto.RequestPositionDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

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

    private Long productId;

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
        this.productId = positionDto.getProduct().getId();
        this.productName = positionDto.getProduct().getCaption();
        this.requestPositionId = positionDto.getId();
        this.quantity = positionDto.getQuantity();
        this.latheTime = positionDto.getProduct().getLatheTime();
        this.millingTime = positionDto.getProduct().getMillingTime();
    }

    public void completeBatchItem(Instant endBatchTime) {
        this.quantityExec++;
        if (Objects.equals(quantity, quantityExec)) {
            this.endBatchTime = endBatchTime;
            Duration res = Duration.between(this.startBatchTime,
                   this.endBatchTime);
            this.summaryWorkingTimeBatch = res.getNano();

            // find all batches for current ProductionTask
            // if all batches are rdy, then close task
        }
    }

}
