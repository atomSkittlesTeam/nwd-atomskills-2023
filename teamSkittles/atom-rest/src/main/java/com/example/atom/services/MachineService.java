package com.example.atom.services;

import com.example.atom.dto.MachineDto;
import com.example.atom.dto.Types;
import com.example.atom.entities.Message;
import com.example.atom.readers.MachineReader;
import com.example.atom.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class MachineService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MachineReader machineReader;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private UserService userService;

    @Scheduled(fixedDelay = 1000 * 60)
    @Transactional
    public void getAllBrokenMachines() {
        LinkedHashMap<String, LinkedHashMap<String, Integer>> allMachines = machineReader.getAllMachines();
        List<LinkedHashMap<String, Integer>> listOfMaps = allMachines.values().stream().toList();
        List<Integer> allMachinesPorts = new ArrayList<>();
        listOfMaps.forEach(map -> allMachinesPorts.addAll(map.values().stream().toList()));
        for (Integer port : allMachinesPorts) {
            MachineDto machineDto = machineReader.getMachineStatusByPort(port);
            if (machineDto.getState() != null && machineDto.getState().getCode().equals("BROKEN")){
                saveMessageOfBrokenMachine(machineDto);
            }
        }
        sendEmailOfBrokenMachines();
        System.out.println("Дернул все станки на поломку");
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
    public void sendEmailOfBrokenMachines() {
        List<Message> messages = messageRepository.findAll();
        List<Message> newMessages = messages.stream().filter(e -> e.getEmailSign().equals(false)
                && e.getType().equals(Types.machineBroke)).toList();
        if (newMessages.size() > 0) {
            String numbers = String.join(",", newMessages.stream().map(Message::getObjectName).toList());
            List<String> emails = userService.getEmailsByRole("chief");
            emails.forEach(email -> {
                emailService.sendSimpleMessage(email,
                        "Произошла поломка станка!",
                        ("Станки с кодами: " + numbers + " сломаны"));
            });
            newMessages.forEach(e -> e.setEmailSign(true));
            messageRepository.saveAll(newMessages);
            messageRepository.flush();
            System.out.println("Отправил сообщение о поломке сообщений");
        }
    }

    public List<MachineDto> getAllWaitingMachines() {
        return this.getMachinesByStatus("WAITING");
    }

    private List<MachineDto> getMachinesByStatus(String status) {
        System.out.println("Получение простаивающих станков...");
        LinkedHashMap<String, LinkedHashMap<String, Integer>> allMachines = machineReader.getAllMachines();
        List<LinkedHashMap<String, Integer>> listOfMaps = allMachines.values().stream().toList();
        List<Integer> allMachinesPorts = new ArrayList<>();
        listOfMaps.forEach(map -> allMachinesPorts.addAll(map.values().stream().toList()));
        List<MachineDto> machineDtos = new ArrayList<>();
        for (Integer port : allMachinesPorts) {
            MachineDto machineDto = machineReader.getMachineStatusByPort(port);
            if (machineDto.getState() != null && machineDto.getState().getCode().equals(status)) {
                machineDtos.add(machineDto);
            }
        }
        return machineDtos;
    }
}
