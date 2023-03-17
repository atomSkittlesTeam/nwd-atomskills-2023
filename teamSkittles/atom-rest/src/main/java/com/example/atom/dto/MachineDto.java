package com.example.atom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineDto {
    private Long id;
    private String code;
    private StateDto state;
    private Object advInfo;
    private Date beginDateTime;
    private Date endDateTime;

}