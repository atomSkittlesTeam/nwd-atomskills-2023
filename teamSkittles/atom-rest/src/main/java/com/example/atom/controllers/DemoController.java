package com.example.atom.controllers;

import com.example.atom.dto.DemoDto;
import com.example.atom.dto.RequestDto;
import com.example.atom.dto.queryobjects.DemoDbObject;
import com.example.atom.readers.DemoReader;
import com.example.atom.services.DemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("demo")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @Autowired
    private DemoReader demoReader;

    @PostMapping("/post-dto")
    public void postDemo(@RequestBody DemoDto demoDto) {
        demoService.postDemo(demoDto);
    }

    @PostMapping("/post-list-dto")
    public void postListDemo(@RequestBody List<DemoDto> listDemoDtos) {
        demoService.postListDemo(listDemoDtos);
    }

    @PutMapping("/put-dto")
    public void putDemo(@RequestBody DemoDto demoDto) {
        demoService.putDemo(demoDto);
    }

    @PutMapping("/put-list-dto")
    public void putListDemo(@RequestBody List<DemoDto> listDemoDtos) {
        demoService.putListDemo(listDemoDtos);
    }

    @DeleteMapping("/delete-dto/{id}")
    public void deleteDemoById(@PathVariable Long id) {
        demoService.deleteDemoById(id);
    }

    @PostMapping("/delete-list-dto")
    public void deleteDemoById(@RequestBody List<Long> ids) {
        demoService.deleteListDemoByIds(ids);
    }

    @GetMapping("/get-data/{id}")
    public DemoDto getDemo(@PathVariable Long id) {
        return demoService.getDemoById(id);
    }

    @GetMapping("/get-data")
    public List<DemoDto> getListDemo() {
        return demoService.getListDemo();
    }

    @GetMapping("/get-data-as-map")
    public Map<Long, DemoDto> getMapDemoToId() {
        return demoService.getMapDemoToId();
    }

    @PostMapping("/query")
    public List<DemoDbObject> getDemoDbObjects(@RequestBody RequestDto requestDto) {
        return demoReader.getDemoDbObjects(requestDto);
    }

    //stats
    @GetMapping("/year-stat")
    public Map<String, Integer> getYearStat() {
        return demoService.getYearStat();
    }

    @GetMapping("/string-stat")
    public Map<String, Integer> getStringStat() {
        return demoService.getStringStat();
    }

}
