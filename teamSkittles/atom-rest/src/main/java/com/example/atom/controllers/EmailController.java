package com.example.atom.controllers;

import com.example.atom.dto.RequestDto;
import com.example.atom.dto.RequestPositionDto;
import com.example.atom.services.EmailServiceImpl;
import com.example.atom.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("email")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EmailController {

    @Autowired
    EmailServiceImpl emailService;

    @PostMapping("send")
    public void helloWorld(@RequestBody List<RequestDto> newRequestDtos) {
        emailService.sendSimpleMessage("sergej.davidyuk@yandex.ru", "А если очень поменять тему?",
                "Я стараюсь не попасть в спам \n Вот бы не попасть то \nЩа не попаду");
    }
}
