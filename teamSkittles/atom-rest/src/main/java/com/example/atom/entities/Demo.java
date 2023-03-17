package com.example.atom.entities;

import com.example.atom.dto.DemoDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Demo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //public нужны для экселя
    public Long id;
    public String stringProperty;
    public Integer integerProperty;
    public Float floatProperty;
    public Boolean booleanProperty;
    public Instant instantProperty;
    @JsonFormat(pattern = "dd/MM/yyyy")
    public Date dateProperty;

    public Demo getDemoFromDto(DemoDto demoDto) {
        this.stringProperty = demoDto.getStringProperty();
        this.integerProperty = demoDto.getIntegerProperty();
        this.floatProperty = demoDto.getFloatProperty();
        this.booleanProperty = demoDto.getBooleanProperty();
        this.instantProperty = demoDto.getInstantProperty();
        this.dateProperty = demoDto.getDateProperty();
        return this;
    }
}
