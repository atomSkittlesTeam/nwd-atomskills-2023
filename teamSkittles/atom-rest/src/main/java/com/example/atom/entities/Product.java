package com.example.atom.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String code;
    private String caption;
    private Integer millingTime;
    private Integer latheTime;


//    public RequestExtension(String name, String description) {
//        this.name = name;
//        this.description = description;
//    }
}
