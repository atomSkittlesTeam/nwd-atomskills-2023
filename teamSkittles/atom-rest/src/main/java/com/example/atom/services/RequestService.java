package com.example.atom.services;

import com.example.atom.dto.*;
import com.example.atom.entities.Message;
import com.example.atom.entities.ProductionPlan;
import com.example.atom.entities.Request;
import com.example.atom.readers.RequestReader;
import com.example.atom.repositories.MessageRepository;
import com.example.atom.repositories.ProductionPlanRepository;
import com.example.atom.repositories.RequestRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestService {

    @Value("${api.url}")
    private String url;

    @Autowired
    private RequestReader requestReader;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductionPlanService productionPlanService;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedDelayString = "${scheduled.new-requests}")
    @Transactional
    public void getAllRequestsAndFindNew() {
        //вычитаю из всех реквестов, которые пришли из сервиса те, которые уже были в бд, получил новые
        List<RequestDto> allRequests = requestReader.getRequests();
        List<Request> requestsFromDB = requestRepository.findAll();
        List<Long> newIds = CollectionUtils.subtract(
                allRequests.stream().map(RequestDto::getId).toList(),
                requestsFromDB.stream().map(Request::getId).toList()
        ).stream().toList();

        //формирую лист из новых реквестов
        Map<Long, RequestDto> mapRequestsByIds = allRequests.stream().collect(Collectors.toMap(RequestDto::getId, Function.identity()));
        List<RequestDto> newRequestDtos = new ArrayList<>();
        newIds.forEach(e -> newRequestDtos.add(mapRequestsByIds.get(e)));

        //сохраняю новые реквесты
        List<Request> requests = getListEntitiesFromListDtos(newRequestDtos);
        requestRepository.saveAll(requests);
        requestRepository.flush();
        if (newRequestDtos.size() > 0) {
            //если они есть - сохраняю сообщения в базу
            saveMessagesOfNewRequests(newRequestDtos);
            //если они есть - отсылаю емейл
            sendMessageOfNewRequests();
        }
        List<String> emails = userService.getEmailsByRole("chief");
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
        List<String> emails = userService.getEmailsByRole("chief");
        emails.forEach(email -> {
            emailService.sendSimpleMessage(email,
                    "Новые реквесты пришли",
                    ("Пришли новые заказы, количество: " + newMessages.size() + "\n"
                            + "вот такие номера у новых заказов: " + numbers));
        });
        newMessages.forEach(e -> e.setEmailSign(true));
        messageRepository.saveAll(newMessages);
        System.out.println("Отправил сообщение о новых реквестах");
    }

    public List<Request> getListEntitiesFromListDtos(List<RequestDto> requestDtos) {
        List<Request> requests = new ArrayList<>();
        requestDtos.forEach(dto -> {
            requests.add(new Request(
                    dto.getId(), dto.getNumber(), dto.getDate(), dto.getDescription(), dto.getReleaseDate(), null));
        });
        return requests;
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

    public List<RequestDto> getOrderedRequests(List<Long> requestIds) {
        List<RequestDto> result = new ArrayList<>();
        List<Request> requestExtensionList = requestRepository.findAllById(requestIds);

        List<ProductionPlan> planInProduction = productionPlanRepository
                .findAllByProductionPlanStatusEqualsOrderByPriority(ProductionPlanStatus.IN_PRODUCTION);
        List<ProductionPlan> planApproved = productionPlanRepository
                .findAllByProductionPlanStatusEqualsOrderByPriority(ProductionPlanStatus.APPROVED);

        List<Request> requestsInProduction = requestRepository.findAllById(
                planInProduction.stream().map(e -> e.getRequestId()).toList()
        );
        List<Request> requestsApproved = requestRepository.findAllById(
                planApproved.stream().map(e -> e.getRequestId()).toList()
        );


        Map<Long, Request> mapRequestExtensionById = requestExtensionList.stream()
                .collect(Collectors.toMap(Request::getId, Function.identity()));
        Map<Long, List<RequestPositionDto>> mapItemsByRequestId = new HashMap<>();
        List<Long> idsOverTwoDays = new ArrayList<>();
        List<Request> requestOverTwoDays = new ArrayList<>();
        List<Long> idsUnderTwoDays = new ArrayList<>();
        List<Request> requestUnderTwoDays = new ArrayList<>();
        requestExtensionList.forEach(requestExtension -> {
            List<RequestPositionDto> positionItemsDto = requestReader.getRequestPositionById(requestExtension.getId());
            mapItemsByRequestId.put(requestExtension.getId(), positionItemsDto);
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
            mapRequestExtensionById.get(entry.getKey()).setTime(requestTime);
        }
        idsOverTwoDays.forEach(id -> {
            requestOverTwoDays.add(mapRequestExtensionById.get(id));
        });
        idsUnderTwoDays.forEach(id -> {
            requestUnderTwoDays.add(mapRequestExtensionById.get(id));
        });
        requestRepository.saveAll(mapRequestExtensionById.values().stream().toList());
        requestRepository.flush();
        sortList(requestOverTwoDays);
        sortList(requestUnderTwoDays);
        List<Request> helpList = new ArrayList<>();
        helpList.addAll(requestsInProduction);
        helpList.addAll(requestsApproved);
        helpList.addAll(requestOverTwoDays);
        helpList.addAll(requestUnderTwoDays);
        long priority = 0;
        Map<Long, ProductionPlanDto> partRequestMap = this.getPartIdsForRequestIds(helpList.stream()
                .map(Request::getId).toList())
                .stream()
                .collect(Collectors.toMap(ProductionPlanDto::getRequestId, Function.identity()));
        for (Request res : helpList) {
            RequestDto dto = new RequestDto();
            priority++;
            dto.setPriority(priority);
            dto.setRequestDtoFromEntity(res);
            if (!partRequestMap.isEmpty() && dto.getRequestId() != null) {
                ProductionPlanDto plan = partRequestMap.get(dto.getRequestId());
                if (plan != null) {
                    dto.setId(plan.getId());
                    StateDto stateDto = new StateDto();
                    dto.setState(stateDto);
                    switch (plan.getProductionPlanStatus()) {
                        case BLANK -> {
                            dto.getState().setCaption("Сохранено");
                            dto.getState().setCode("BLANK");
                        }
                        case APPROVED -> {
                            dto.getState().setCaption("Утверждено");
                            dto.getState().setCode("APPROVED");
                        }
                        case IN_PRODUCTION -> {
                            dto.getState().setCaption("В производствек");
                            dto.getState().setCode("IN_PRODUCTION");
                        }
                    }
                }
            }
            result.add(dto);
        }

        return result;
    }

    public List<ProductionPlanDto> getPartIdsForRequestIds(List<Long> requestIds) {

        SqlParameterSource p = new MapSqlParameterSource()
                .addValue("requestIds", requestIds);

        String query = "select id, request_id, production_plan_status from production_plan where request_id in (:requestIds)";

        List<ProductionPlanDto> productionPlanDtos = namedParameterJdbcTemplate.query(
                query, p, new BeanPropertyRowMapper<>(ProductionPlanDto.class, false));

        return productionPlanDtos;
    }

    private void sortList(List<Request> list) {
        Comparator<Request> c = Comparator.comparing(Request::getDate)
                .thenComparing(Request::getReleaseDate);
        list.sort(c);
    }

    public void changeRequestStatusInProductionCrm(Long requestId) {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/crm/requests/" + requestId
                + "/set-state/in-production"
                )
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<?> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            System.out.println("impossible");
        }
    }
}
