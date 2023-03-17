package com.example.atom.entities;

import com.example.atom.dto.ContractorDto;
import com.example.atom.dto.DemoDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Contractor {

    @Id
    private Long id;
    private String inn;
    private String caption;

    public Contractor getDemoFromDto(ContractorDto contractorDto) {
        this.id = contractorDto.getId();
        this.inn = contractorDto.getInn();
        this.caption = contractorDto.getCaption();
        return this;
    }

}
