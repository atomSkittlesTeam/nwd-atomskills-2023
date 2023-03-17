package com.example.atom.controllers;

import com.example.atom.dto.DemoDto;
import com.example.atom.entities.Process;
import com.example.atom.readers.ProcessReader;
import com.example.atom.services.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("process")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProcessController {

    private final ProcessService processService;

    private final ProcessReader processReader;

    @PostMapping("start")
    public void startProcess(@RequestBody DemoDto demoDto) {
        processService.startProcess(demoDto);
    }

    @GetMapping("get")
    public void getProcess() {
        System.out.println("hello world!");
    }

    @GetMapping("get/all")
    public List<Process> getAllProcesses() {
        return this.processReader.getAllProcesses();
    }

    @GetMapping("get/current-user")
    public List<Process> getAvailableProcessesForCurrentUser() {
        return this.processReader.getAllProcessesForCurrentUser();
    }
}
