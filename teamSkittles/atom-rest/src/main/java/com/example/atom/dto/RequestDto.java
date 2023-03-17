package com.example.atom.dto;

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

}