package com.example.atom.services;

import com.example.atom.dto.DemoDto;
import com.example.atom.entities.Demo;
import com.example.atom.repositories.DemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DemoService {

    @Autowired
    private DemoRepository demoRepository;

    public void postDemo(DemoDto demoDto) { //add
        Demo demo = new Demo();
        demo = demoMapper(demoDto, demo);
        demoRepository.save(demo);
    }

    public void postListDemo(List<DemoDto> listDemoDtos) {
        List<Demo> demosToSave = new ArrayList<>();
        for(DemoDto demoDto : listDemoDtos) {
            Demo demo = new Demo();
            demo = demoMapper(demoDto, demo);
            demosToSave.add(demo);
        }
        demoRepository.saveAll(demosToSave);
    }

    public void putDemo(DemoDto demoDto) { //update
        Demo demo = demoRepository.findById(demoDto.getId()).orElse(null);
        if(demo == null) {
            throw new RuntimeException("В базе не найдена запись с данным id");
        }
        demo = demoMapper(demoDto, demo);
        demoRepository.save(demo);
    }

    public void putListDemo(List<DemoDto> listDemoDtos) {
        List<Demo> demosToUpdate = demoRepository.findAllById(listDemoDtos.stream().map(DemoDto::getId).toList());
        if(demosToUpdate.isEmpty()) {
            throw new RuntimeException("В базе не найдено ни одной записи с данными id");
        }
        for(DemoDto demoDto : listDemoDtos) {
            Demo demo = new Demo();
            demo = demoMapper(demoDto, demo);
            demosToUpdate.add(demo);
        }
        demoRepository.saveAll(demosToUpdate);
    }

    public void deleteDemoById(Long id) {
        Demo demo = demoRepository.findById(id).orElse(null);
        if(demo == null) {
            throw new RuntimeException("В базе не найдена запись с данным id");
        }
        demoRepository.deleteById(id);
    }

    public void deleteListDemoByIds(List<Long> ids) {
        List<Demo> demosToDelete = demoRepository.findAllById(ids);
        if(demosToDelete.isEmpty()) {
            throw new RuntimeException("В базе не найдено ни одной записи с данными id");
        }
        demoRepository.deleteAllById(demosToDelete.stream().map(Demo::getId).toList());
    }

    public DemoDto getDemoById(Long id) {
        Demo demo = demoRepository.findById(id).orElse(null);
        if(demo == null) {
            throw new RuntimeException("В базе не найдена запись с данным id");
        }
        return new DemoDto(demo);
    }

    public List<DemoDto> getListDemo() {
        List<Demo> demoList = demoRepository.findAll();
        return demoList
                .stream()
                .sorted(Comparator.comparing(Demo::getId))
                .map(DemoDto::new)
                .collect(Collectors.toList());
    }

    public Map<Long, DemoDto> getMapDemoToId() {
        List<Demo> demoList = demoRepository.findAll();
        return demoList
                .stream()
                .map(DemoDto::new)
                .collect(Collectors.toMap(DemoDto::getId, Function.identity()));
    }

    //stats
    public Map<String, Integer> getYearStat() {
        Map<String, Integer> result = new HashMap<>();
        List<DemoDto> listDemo = this.getListDemo();
        Map<Object, Long> groupedMap = listDemo.stream().collect(
                Collectors.groupingBy(e ->
                        Optional.ofNullable(e.getDateProperty()).orElse(new Date()).getYear() + 1900, Collectors.counting())
        );
        groupedMap.forEach((k,v) -> {
            result.put(k.toString(),v.intValue());
        });
        return result;
    }

    public Map<String, Integer> getStringStat() {
        Map<String, Integer> result = new HashMap<>();
        List<DemoDto> listDemo = this.getListDemo();
        Map<Object, Long> groupedMap = listDemo.stream().collect(
                Collectors.groupingBy(e ->
                        Optional.ofNullable(e.getStringProperty()).orElse("Нет строки"), Collectors.counting())
        );
        groupedMap.forEach((k,v) -> {
            result.put(k.toString(),v.intValue());
        });
        return result;
    }


    //helpers
    private Demo demoMapper(DemoDto demoDto, Demo demo) {
        demo.setStringProperty(demoDto.getStringProperty());
        demo.setIntegerProperty(demoDto.getIntegerProperty());
        demo.setFloatProperty(demoDto.getFloatProperty());
        demo.setBooleanProperty(demoDto.getBooleanProperty());
        demo.setInstantProperty(demoDto.getInstantProperty());
        demo.setDateProperty(demoDto.getDateProperty());
        return demo;
    }
}
