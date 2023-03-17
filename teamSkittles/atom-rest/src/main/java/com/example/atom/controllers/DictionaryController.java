package com.example.atom.controllers;

import com.example.atom.dto.ContractorDto;
import com.example.atom.services.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("hello")
    public String helloWorld() {
        return "Hello world";
    }

    @GetMapping("/dict/contractors")
    public List<ContractorDto> getContractors() {
        dictionaryService.getContractors();
    }

}
