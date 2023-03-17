package com.example.atom.controllers;

import com.example.atom.dto.DemoDto;
import com.example.atom.services.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("integration")
@RequiredArgsConstructor
@CrossOrigin("*")
public class IntegrationController {

    @Autowired
    IntegrationService integrationService;

    @GetMapping("/get-data")
    public List<DemoDto> getDataFromServer() {
        return integrationService.getDataFromServer();
    }

    @GetMapping("/send-id/{id}")
    public List<DemoDto> parseExcelWithPath(@PathVariable Long id) {
        return integrationService.sendIdToServer(id);
    }

    @PostMapping("/send-dto")
    public void sendDemoToServer(@RequestBody DemoDto demoDto) {
        this.integrationService.sendDemoToServer(demoDto);
    }

    @PostMapping("/send-dto-list")
    public void sendDemoListToServer(@RequestBody List<DemoDto> demoDtoList) {
        this.integrationService.sendDemoListToServer(demoDtoList);
    }

}
