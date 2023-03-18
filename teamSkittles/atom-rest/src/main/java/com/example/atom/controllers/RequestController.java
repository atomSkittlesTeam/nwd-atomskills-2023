package com.example.atom.controllers;

import com.example.atom.dto.*;
import com.example.atom.entities.Product;
import com.example.atom.entities.ProductionPlan;
import com.example.atom.entities.Request;
import com.example.atom.readers.RequestReader;
import com.example.atom.services.ProductionPlanService;
import com.example.atom.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("requests")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ProductionPlanService productionPlanService;

    @Autowired
    private RequestReader requestReader;

    @GetMapping("debug")
    public void helloWorld() {
        int i = 0;
    }

    @GetMapping("")
    public List<RequestDto> getRequests() {
        return requestReader.getRequests();
    }

    @GetMapping("get-plan")
    public List<RequestDto> getPlan() {
        return requestReader.getPlan();
    }

    @GetMapping("{id}")
    public RequestDto getRequestById(@PathVariable Long id) {
        return requestReader.getRequestById(id);
    }

    @GetMapping("{id}/items")
    public List<RequestPositionDto> getRequestPositionById(@PathVariable Long id) {
        return requestReader.getRequestPositionById(id);
    }

    @GetMapping("new-messages")
    public List<MessageDto> getNewMessages() {
        return requestService.getNewMessages();
    }

    @PostMapping("message")
    public void messageSetFrontSing(@RequestBody List<Long> messageIds) {
        requestService.messageSetFrontSing(messageIds);
    }

    @PostMapping("ordered-plan")
    public List<RequestDto> orderedPlan(@RequestBody List<Long> requestIds) {
        return requestService.getOrderedRequests(requestIds);
    }

    @PostMapping("approve-plan/{id}")
    @Transactional
    public List<RequestDto> approvePlan(@RequestBody List<ProductionPlanDto> productionPlanDtos, @PathVariable("id") Long planId) {
        productionPlanService.approvePlan(planId, productionPlanDtos);
        return requestReader.getPlan();
    }

    @PostMapping("save-blank")
    @Transactional
    public List<RequestDto> savePlanDraft(@RequestBody List<ProductionPlanDto> productionPlanDtos) {
        productionPlanService.blankSave(productionPlanDtos);
        return requestReader.getPlan();
    }

}
