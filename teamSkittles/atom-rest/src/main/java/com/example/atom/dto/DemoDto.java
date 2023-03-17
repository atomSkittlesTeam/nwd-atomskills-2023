package com.example.atom.dto;

import com.example.atom.entities.Demo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class DemoDto {
    private Long id;
    private String stringProperty;
    private Integer integerProperty;
    private Float floatProperty;
    private Boolean booleanProperty;
    private Instant instantProperty;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateProperty;
    private List<DemoRefDto> demoRefDtoList;

    public DemoDto(Demo demo) {
        this.id = demo.getId();
        this.stringProperty = demo.getStringProperty();
        this.integerProperty = demo.getIntegerProperty();
        this.floatProperty = demo.getFloatProperty();
        this.booleanProperty = demo.getBooleanProperty();
        this.instantProperty = demo.getInstantProperty();
        this.dateProperty = demo.getDateProperty();
    }
}
