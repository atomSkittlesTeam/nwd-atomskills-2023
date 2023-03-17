package com.example.atom.entities;

import com.example.atom.dto.ContractorDto;
import com.example.atom.dto.StateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String number; //номер заказа (по идее)
    private Date date;
    private String description;
    private Date releaseDate;


//    public RequestExtension(String name, String description) {
//        this.name = name;
//        this.description = description;
//    }
}
