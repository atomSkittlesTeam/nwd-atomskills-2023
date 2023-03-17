package com.example.atom.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    @GetMapping("hello")
    public String helloWorld() {
        return "Hello world";
    }

}
