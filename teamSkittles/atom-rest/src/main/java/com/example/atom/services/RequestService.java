package com.example.atom.services;

import com.example.atom.dto.*;
import com.example.atom.entities.Message;
import com.example.atom.entities.RequestExtension;
import com.example.atom.readers.RequestReader;
import com.example.atom.repositories.MessageRepository;
import com.example.atom.repositories.RequestRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestService {

    @Autowired
    private RequestReader requestReader;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Scheduled(fixedDelay = 1000 * 20)
    @Transactional
    public void getAllRequestsAndFindNew() {
        //вычитаю из всех реквестов, которые пришли из сервиса те, которые уже были в бд, получил новые
        List<RequestDto> allRequests = requestReader.getRequests();
        List<RequestExtension> requestsFromDB = requestRepository.findAll();
        List<Long> newIds = CollectionUtils.subtract(
                allRequests.stream().map(RequestDto::getId).toList(),
                requestsFromDB.stream().map(RequestExtension::getRequestId).toList()
        ).stream().toList();

        //формирую лист из новых реквестов
        Map<Long, RequestDto> mapRequestsByIds = allRequests.stream().collect(Collectors.toMap(RequestDto::getId, Function.identity()));
        List<RequestDto> newRequestDtos = new ArrayList<>();
        newIds.forEach(e -> newRequestDtos.add(mapRequestsByIds.get(e)));

        //сохраняю новые реквесты
        List<RequestExtension> requestExtensions = getListEntitiesFromListDtos(newRequestDtos);
        requestRepository.saveAll(requestExtensions);
        requestRepository.flush();
        if (newRequestDtos.size() > 0) {
            //если они есть - сохраняю сообщения в базу
            saveMessagesOfNewRequests(newRequestDtos);
            //если они есть - отсылаю емейл
            sendMessageOfNewRequests();
        }
        System.out.println("Дернул все реквесты");
    }

    public void saveMessagesOfNewRequests(List<RequestDto> newRequestDtos) {
        List<Message> listMessages = new ArrayList<>();
        newRequestDtos.forEach(request -> {
            Message message = new Message(Types.newRequests, false,
                    false, request.getId(), request.getNumber(), null);
            listMessages.add(message);
        });
        messageRepository.saveAll(listMessages);
        messageRepository.flush();
    }

    @Transactional
    public void sendMessageOfNewRequests() {
        List<Message> messages = messageRepository.findAll();
        List<Message> newMessages = messages.stream().filter(e -> e.getEmailSign().equals(false)
                && e.getType().equals(Types.newRequests)).toList();
        String numbers = String.join(",", newMessages.stream().map(Message::getObjectName).toList());
        emailService.sendSimpleMessage("sergej.davidyuk@yandex.ru",
                "Новые реквесты пришли",
                ("Пришли новые заказы, количество: " + newMessages.size() + "\n"
                        + "вот такие номера у новых заказов: " + numbers));
        newMessages.forEach(e -> e.setEmailSign(true));
        messageRepository.saveAll(newMessages);
        System.out.println("Отправил сообщение о новых реквестах");
    }

    public List<RequestExtension> getListEntitiesFromListDtos(List<RequestDto> requestDtos) {
        List<RequestExtension> requestExtensions = new ArrayList<>();
        requestDtos.forEach(dto -> {
            requestExtensions.add(new RequestExtension(
                    dto.getId(), dto.getNumber(), dto.getDate(), dto.getReleaseDate()));
        });
        return requestExtensions;
    }

    public List<MessageDto> getNewMessages() {
        List<Message> newMessages = messageRepository.findAll().stream()
                .filter(e -> e.getFrontSign().equals(false)).toList();
        List<MessageDto> dtos = new ArrayList<>();
        newMessages.forEach(message -> {
            dtos.add(new MessageDto(message));
        });
        return dtos;
    }

    public void messageSetFrontSing(List<Long> messageIds) {
        List<Message> messages = messageRepository.findAllById(messageIds);
        messages.forEach(e -> e.setFrontSign(true));
        messageRepository.saveAll(messages);
        messageRepository.flush();
    }

    public List<RequestExtension> getOrderedRequests(List<Long> requestIds) {
        List<RequestExtension> result = new ArrayList<>();
        List<RequestExtension> requestExtensionList = requestRepository.findAllById(requestIds);
        Map<Long, RequestExtension> mapRequestExtensionById = requestExtensionList.stream()
                .collect(Collectors.toMap(RequestExtension::getRequestId, Function.identity()));
        Map<Long, List<RequestPositionDto>> mapItemsByRequestId = new HashMap<>();
        List<Long> idsOverTwoDays = new ArrayList<>();
        List<RequestExtension> requestOverTwoDays = new ArrayList<>();
        List<Long> idsUnderTwoDays = new ArrayList<>();
        List<RequestExtension> requestUnderTwoDays = new ArrayList<>();
        requestExtensionList.forEach(requestExtension -> {
            List<RequestPositionDto> positionItemsDto = requestReader.getRequestPositionById(requestExtension.getRequestId());
            mapItemsByRequestId.put(requestExtension.getRequestId(), positionItemsDto);
        });
        for (var entry : mapItemsByRequestId.entrySet()) {
            int requestTime = 0;
            for(RequestPositionDto dto : entry.getValue()) {
                int requestPositionTime = dto.getQuantity() * (dto.getProduct().getMillingTime() + dto.getProduct().getLatheTime());
                requestTime += requestPositionTime;
            }
            if(requestTime < 60 * 60 * 24 * 2) {
                idsUnderTwoDays.add(entry.getKey());
            } else {
                idsOverTwoDays.add(entry.getKey());
            }
        }
        idsOverTwoDays.forEach(id -> {
            requestOverTwoDays.add(mapRequestExtensionById.get(id));
        });
        idsUnderTwoDays.forEach(id -> {
            requestUnderTwoDays.add(mapRequestExtensionById.get(id));
        });
        sortList(requestOverTwoDays);
        sortList(requestUnderTwoDays);
        result.addAll(requestOverTwoDays);
        result.addAll(requestUnderTwoDays);
        return result;
    }

    private void sortList(List<RequestExtension> list) {
        Comparator<RequestExtension> c = Comparator.comparing(RequestExtension::getDate)
                .thenComparing(RequestExtension::getReleaseDate);
        list.sort(c);
    }


}
