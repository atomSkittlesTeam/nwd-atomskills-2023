package com.example.atom.services;

import com.example.atom.dto.MessageDto;
import com.example.atom.dto.RequestDto;
import com.example.atom.dto.Types;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Scheduled(fixedDelay = 1000 * 5)
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

}
