package com.example.atom.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StateDto {
    private String code;
    private String caption;

//    public StateDto(Contractor contractor) {
//        this.id = contractor.getId();
//        this.inn = contractor.getInn();
//        this.caption = contractor.getCaption();
//    }
}
