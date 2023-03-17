package com.example.atom.dto;

import com.example.atom.entities.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;
    private String number;
    private Date date;
    private ContractorDto contractor;
    private String description;
    private StateDto state;
    private Date releaseDate;
    private Integer time;
    private Long priority;

    public RequestDto getRequestDtoFromEntity(Request request) {
        this.id = request.getId();
        this.number = request.getNumber();
        this.date = request.getDate();
        this.description = request.getDescription();
        this.releaseDate = request.getReleaseDate();
        this.time = request.getTime();
        return this;
    }

}