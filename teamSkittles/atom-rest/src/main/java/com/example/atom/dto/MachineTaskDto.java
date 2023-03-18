package com.example.atom.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MachineTaskDto {
    @NotNull
    private Long productId;

    private Long batchId;

    private Long batchItemId;

    /*
        @Param id заказ наряда
     */
    private Long productionTaskId;
}
