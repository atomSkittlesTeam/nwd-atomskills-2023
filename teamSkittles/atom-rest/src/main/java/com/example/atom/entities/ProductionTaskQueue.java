package com.example.atom.entities;

import com.example.atom.dto.RequestPositionDto;
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
public class ProductionTaskQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    // id заказ наряда
    private Long productionTaskId;

    // id позиции договора
    private Long requestPositionId;

    private String productName;

    private Integer quantity;

    private Integer quantityExec;

    /* точильная по справочнику*/
    private Integer latheTime;

    private boolean latheFlag;

    private Instant latheStartTimestamp;

    private Instant latheFactTimestamp;

    /* фрезерная по справочнику*/
    private Integer millingTime;

    private boolean millingFlag;

    private Instant millingStartTimestamp;

    // время работы на станке
    private Instant millingFactTimestamp;

    private Long machineId;

    private Instant summaryWorkingTime;

    public ProductionTaskQueue(ProductionTask productionTask,
                               RequestPositionDto positionDto) {
        this.productionTaskId = productionTask.getId();
        this.requestPositionId = positionDto.getId();
        this.productName = positionDto.getProduct().getCaption();
        this.quantity = positionDto.getQuantity();
        this.quantityExec = positionDto.getQuantityExec();
        this.millingTime = positionDto.getProduct().getMillingTime();
        this.latheTime = positionDto.getProduct().getLatheTime();
    }
}
