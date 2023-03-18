package com.example.atom.controllers;

import com.example.atom.dto.*;
import com.example.atom.readers.MachineReader;
import com.example.atom.readers.RequestReader;
import com.example.atom.services.MachineService;
import com.example.atom.services.ProductionPlanService;
import com.example.atom.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("machine")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MachineController {

    @Autowired
    private MachineService machineService;

    @Autowired
    private MachineReader machineReader;

    @GetMapping("get-broken-machine-by-id/{id}")
    public void getPlan(@PathVariable Long id) {
        MachineDto machineDto = machineService.getAllBrokenMachinesByStatusAndId(id);
        machineReader.setStatusToMachine(machineDto.getPort(), "");
    }

}
