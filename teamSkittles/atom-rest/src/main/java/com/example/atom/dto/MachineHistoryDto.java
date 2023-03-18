package com.example.atom.dto;

import com.example.atom.entities.MachineType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineHistoryDto {
    private Long id;
    private String code;
    private StateDto state;
    private Date beginDateTime;
    private Date endDateTime;
    private MachineType machineType;
    private int port;
    private AdvInfoAdditionalDto advInfo;
}
