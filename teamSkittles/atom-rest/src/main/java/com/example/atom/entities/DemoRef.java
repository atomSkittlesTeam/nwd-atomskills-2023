package com.example.atom.entities;

import com.example.atom.dto.DemoRefDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class DemoRef {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public Long demoId;
    public String stringPropertyRef;

    public DemoRef getDemoRefFromDto(DemoRefDto demoRefDto) {
        this.demoId = demoRefDto.getDemoId();
        this.stringPropertyRef = demoRefDto.getStringPropertyRef();
        return this;
    }
}
