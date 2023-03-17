package com.example.atom.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Data
@NoArgsConstructor
public class ProcessStage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String stageName;

    private String stageTitle;

    /* roles list which available to approve current stage */
    private List<String> roles;

    public ProcessStage(String stageName, String stageTitle, List<String> roles) {
        this.stageName = stageName;
        this.stageTitle = stageTitle;
        this.roles = roles;
    }
}
