package com.example.atom.controllers;

import com.example.atom.dto.DemoDto;
import com.example.atom.dto.queryobjects.DemoDbObject;
import com.example.atom.readers.DemoReader;
import com.example.atom.services.ParsingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("parsing")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ParsingController {

    private final ParsingService parsingService;

    private final DemoReader demoReader;

    @GetMapping("/parse-excel")
    public void parseExcel() {
        parsingService.parseExcel();
    }

    @GetMapping("/parse-excel/{path}") //на будущее, если будем явно задавать папку/файл
    public void parseExcelWithPath(@PathVariable String path) {
        parsingService.parseJson(path);
    }

    @PostMapping("/parse-excel/file")
    public void parseExcelWithFile(@RequestBody Object file) {
        int i = 0;
    }

    @GetMapping("/parse-json")
    public void parseJson() {
        parsingService.parseJson();
    }

    @GetMapping("/parse-json/{path}") //на будущее, если будем явно задавать папку/файл
    public void parseJson(@PathVariable String path) {
        parsingService.parseJson(path);
    }

    @PostMapping("/parse-json/file")
    public void parseJsonWithFile(@RequestBody Object file){
        Gson gson = new Gson();
        String json = gson.toJson(file);
        parsingService.parseJsonFromFileOrString(json);
    }
}
