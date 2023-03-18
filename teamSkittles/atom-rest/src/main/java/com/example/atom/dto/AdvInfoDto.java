package com.example.atom.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdvInfoDto {
    @NotNull
    private Long productId;

    private Long batchId;

    private Long batchItemId;

    /*
        @Param id заказ наряда
     */
    private Long productionTaskId;
}
