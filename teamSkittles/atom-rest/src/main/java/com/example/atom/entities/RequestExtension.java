package com.example.atom.entities;

import com.example.atom.dto.RequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestExtension {
    @Id
    @Column(name = "id", nullable = false)
    private Long requestId;
    private String number; //номер заказа (по идее)
    private Date date;
    private Date releaseDate;


//    public RequestExtension(String name, String description) {
//        this.name = name;
//        this.description = description;
//    }
}
