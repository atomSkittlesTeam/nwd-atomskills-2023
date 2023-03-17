package com.example.atom.dto;

import com.example.atom.entities.Contractor;
import com.example.atom.entities.Demo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ContractorDto {
    private Long id;
    private String inn;
    private String caption;

    public ContractorDto(Contractor contractor) {
        this.id = contractor.getId();
        this.inn = contractor.getInn();
        this.caption = contractor.getCaption();
    }
}
