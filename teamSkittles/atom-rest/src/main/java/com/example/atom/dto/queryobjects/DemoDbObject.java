package com.example.atom.dto.queryobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
public class DemoDbObject {
    public Long id;
    public String stringProperty;
    public Integer integerProperty;
    public Float floatProperty;
    public Boolean booleanProperty;
    public Instant instantProperty;
    @JsonFormat(pattern = "dd/MM/yyyy")
    public Date dateProperty;
}
