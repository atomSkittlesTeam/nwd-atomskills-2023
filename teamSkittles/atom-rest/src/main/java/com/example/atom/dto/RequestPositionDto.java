package com.example.atom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestPositionDto {
    private Long id;
    private RequestDto request;
    private ProductDto product;
    private Integer quantity;
    private Integer quantityExec;

}