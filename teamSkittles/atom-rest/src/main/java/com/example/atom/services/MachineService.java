package com.example.atom.services;

import com.example.atom.dto.MachineDto;
import com.example.atom.dto.MessageDto;
import com.example.atom.dto.RequestDto;
import com.example.atom.dto.Types;
import com.example.atom.entities.Message;
import com.example.atom.entities.RequestExtension;
import com.example.atom.readers.MachineReader;
import com.example.atom.readers.RequestReader;
import com.example.atom.repositories.MessageRepository;
import com.example.atom.repositories.RequestRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MachineService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MachineReader machineReader;

    @Autowired
    private EmailServiceImpl emailService;

    @Scheduled(fixedDelay = 1000 * 1)
    @Transactional
    public void getAllBrokenMachines() {
        //вычитаю из всех реквестов, которые пришли из сервиса те, которые уже были в бд, получил новые
        LinkedHashMap<String, LinkedHashMap<String, Integer>> allMachines = machineReader.getAllMachines();
        List<LinkedHashMap<String, Integer>> listOfMaps = allMachines.values().stream().toList();
        List<Integer> allMachinesPorts = new ArrayList<>();
        listOfMaps.forEach(map -> allMachinesPorts.addAll(map.values().stream().toList()));
        for (Integer port : allMachinesPorts) {
            MachineDto machineDto = machineReader.getMachineStatusByPort(port);
            if (machineDto.getState() != null && machineDto.getState().getCode().equals("WAITING")){
                saveMessageOfBrokenMachine(machineDto);
            }
        }
        sendMessageOfBrokenMachines();
    }

    public void saveMessageOfBrokenMachine(MachineDto machineDto) {
        List<Message> previousMessagesWithSameMachineId = messageRepository.findAllByObjectId(machineDto.getId());
        if(previousMessagesWithSameMachineId.isEmpty() ||
                //список совпадений - нулевой - записали
                (!previousMessagesWithSameMachineId.isEmpty()
        && previousMessagesWithSameMachineId.stream().filter(e ->
                e.getMachineDate().equals(machineDto.getBeginDateTime().toInstant())).toList().isEmpty())) //список совпадений есть, но
            //нет совпадений - записали
        {
            Message message = new Message(Types.machineBroke, false,
                    false, machineDto.getId(), machineDto.getCode(), machineDto.getBeginDateTime().toInstant());
            messageRepository.save(message);
            messageRepository.flush();
        }
    }

    @Transactional
    public void sendMessageOfBrokenMachines() {
        List<Message> messages = messageRepository.findAll();
        List<Message> newMessages = messages.stream().filter(e -> e.getEmailSign().equals(false)
                && e.getType().equals(Types.machineBroke)).toList();
        if (newMessages.size() > 0) {
            String numbers = String.join(",", newMessages.stream().map(Message::getObjectName).toList());
            emailService.sendSimpleMessage("sergej.davidyuk@yandex.ru",
                    "Произошла поломка станка!",
                    ("Станки с кодами: " + numbers + " сломаны"));
            newMessages.forEach(e -> e.setEmailSign(true));
            messageRepository.saveAll(newMessages);
        }
    }

}
