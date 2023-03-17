package com.example.atom.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContractorDto {
    private Long id;
    private String inn;
    private String caption;

//    public ContractorDto(Contractor contractor) {
//        this.id = contractor.getId();
//        this.inn = contractor.getInn();
//        this.caption = contractor.getCaption();
//    }
}
