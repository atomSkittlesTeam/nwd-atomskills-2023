package com.example.atom.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DemoRefDto {
    private Long id;
    private Long demoId;
    private String stringPropertyRef;


}
