package com.example.atom.services;

import com.example.atom.dto.MachineDto;
import com.example.atom.dto.MachineHistoryDto;
import com.example.atom.dto.Types;
import com.example.atom.entities.MachineState;
import com.example.atom.entities.MachineType;
import com.example.atom.entities.Message;
import com.example.atom.readers.MachineReader;
import com.example.atom.repositories.MessageRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;

@Service
public class MachineService {

    @Value("${api.url}")
    private String url;

    @Value("${api.cut-url}")
    private String cutUrl;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MachineReader machineReader;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private UserService userService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedDelayString = "${scheduled.broken-machines}")
    @Transactional
    public void getAllBrokenMachines() {
        LinkedHashMap<MachineType, LinkedHashMap<String, Integer>> allMachines = machineReader.getAllMachines();
        List<LinkedHashMap<String, Integer>> listOfMaps = allMachines.values().stream().toList();
        List<Integer> allMachinesPorts = new ArrayList<>();
        listOfMaps.forEach(map -> allMachinesPorts.addAll(map.values().stream().toList()));
        for (Integer port : allMachinesPorts) {
            MachineDto machineDto = machineReader.getMachineStatusByPort(port);
            if (machineDto.getState() != null && machineDto.getState().getCode().equals("unknown")) {
                machineReader.setStatusToMachine(port, MachineState.WAITING, null);
            }
            if (machineDto.getState() != null && machineDto.getState().getCode().equals(MachineState.BROKEN.toString())) {
                saveMessageOfMachine(machineDto.getId(), machineDto.getCode(), machineDto.getBeginDateTime(), Types.machineBroke);
            }
        }
        sendEmailOfBrokenMachines();
        System.out.println("Дернул все станки на поломку");
    }

    public void saveMessageOfMachine(Long id, String code, Date beginDate, Types type) {
        List<Message> previousMessagesWithSameMachineId = messageRepository.findAllByObjectId(id);
        if(previousMessagesWithSameMachineId.isEmpty() ||
                //список совпадений - нулевой - записали
                (!previousMessagesWithSameMachineId.isEmpty()
        && previousMessagesWithSameMachineId.stream().filter(e ->
                e.getMachineDate().equals(beginDate.toInstant())).toList().isEmpty())) //список совпадений есть, но
            //нет совпадений - записали
        {
            Message message = new Message(type, false,
                    false, id, code, beginDate.toInstant());
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

    @Transactional
    public void sendEmailOfRepairedMachines() {
        List<Message> messages = messageRepository.findAll();
        List<Message> newMessagesOfRepaired = messages.stream().filter(e -> e.getEmailSign().equals(false)
                && e.getType().equals(Types.machineRepair)).toList();
        if (newMessagesOfRepaired.size() > 0) {
            String numbers = String.join(",", newMessagesOfRepaired.stream().map(Message::getObjectName).toList());
            List<String> emails = userService.getEmailsByRole("chief");
            emails.forEach(email -> {
                emailService.sendSimpleMessage(email,
                        "Станок снова в строю!",
                        ("Станки с кодами: " + numbers + " восстановлены"));
            });
            newMessagesOfRepaired.forEach(e -> e.setEmailSign(true));
            messageRepository.saveAll(newMessagesOfRepaired);
            messageRepository.flush();
            System.out.println("Отправил сообщение о ремонте сообщений");
        }
    }

    public List<MachineDto> getAllWaitingMachines() {
        return this.getMachinesByStatus(MachineState.WAITING);
    }

    public MachineDto getAllBrokenMachinesByStatusAndId(String code) { //мне приходит ид сломанного, я его верну
        MachineDto result = new MachineDto();
        List<MachineDto> allBrokenMachines = this.getMachinesByStatus(MachineState.BROKEN);
        allBrokenMachines = allBrokenMachines.stream().filter(e -> e.getCode().equals(code)).toList();
        if(!allBrokenMachines.isEmpty()) {
            result = allBrokenMachines.get(0);
        }
        return result;
    }

    public List<MachineDto> getMachinesByStatus(MachineState status) {
        System.out.println("Получение простаивающих станков...");
        LinkedHashMap<MachineType, LinkedHashMap<String, Integer>> allMachines = machineReader.getAllMachines();
        List<LinkedHashMap<String, Integer>> listOfMaps = allMachines.values().stream().toList();
        List<Integer> allMachinesPorts = new ArrayList<>();
        listOfMaps.forEach(map -> allMachinesPorts.addAll(map.values().stream().toList()));
        List<MachineDto> machineDtos = new ArrayList<>();
        for (Integer port : allMachinesPorts) {
            MachineDto machineDto = machineReader.getMachineStatusByPort(port);
            machineDto.setMachineType(this.getType(allMachines, machineDto.getCode()));
            machineDto.setPort(port);
            if (status == null || (machineDto.getState() != null && machineDto.getState().getCode().equals(status.toString()))) {
                machineDtos.add(machineDto);
            }
        }
        return machineDtos;
    }

    private MachineType getType(LinkedHashMap<MachineType, LinkedHashMap<String, Integer>> allMachines, String machineName) {
        MachineType type = null;
        for (Map.Entry<MachineType, LinkedHashMap<String, Integer>> stringLinkedHashMapEntry : allMachines.entrySet()) {
            type = stringLinkedHashMapEntry.getKey(); // type
            LinkedHashMap<String, Integer> machinesMap = stringLinkedHashMapEntry.getValue();
            if (machinesMap.containsKey(machineName)) {
                return type;
            } else {
                continue;
            }
        }
        if (type == null) {
            throw new RuntimeException("Невозможно определить тип станка");
        }
        return type;
    }

    public List<MachineHistoryDto> getHistoryForMachines(int port) {
        String url = UriComponentsBuilder.fromHttpUrl(this.cutUrl + port + "/status/all")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<List<MachineHistoryDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<MachineHistoryDto>>() {
                    });

            List<MachineHistoryDto> result = responseEntity.getBody();
            if (result == null)
                System.out.println("Null");
            return result;
        } catch (Exception e) {
            System.out.println("impossible");
        }
        return null;
    }
}
