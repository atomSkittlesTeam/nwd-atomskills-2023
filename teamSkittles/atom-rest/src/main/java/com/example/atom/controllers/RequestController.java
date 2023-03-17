package com.example.atom.controllers;

import com.example.atom.dto.ContractorDto;
import com.example.atom.dto.RequestDto;
import com.example.atom.dto.RequestPositionDto;
import com.example.atom.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("requests")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RequestController {

    @Autowired
    private RequestService dictionaryService;

    @GetMapping("")
    public List<RequestDto> getRequests() {
        return dictionaryService.getRequests();
    }

    @GetMapping("{id}")
    public RequestDto getRequestById(@PathVariable Long id) {
        return dictionaryService.getRequestById(id);
    }

    @GetMapping("{id}/items")
    public List<RequestPositionDto> getRequestPositionById(@PathVariable Long id) {
        return dictionaryService.getRequestPositionById(id);
    }

}
