package com.example.atom.readers;

import com.example.atom.dto.*;
import com.example.atom.entities.ProductionPlan;
import com.example.atom.entities.Request;
import com.example.atom.repositories.ProductionPlanRepository;
import com.example.atom.repositories.RequestRepository;
import com.example.atom.services.ProductionPlanService;
import com.example.atom.services.RequestService;
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
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RequestReader {
    @Value("${api.url}")
    private String url;


    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<RequestDto> getRequests() {
        List<RequestDto> requestDtos = this.getRequestsFromApi();

        List<ProductionPlanDto> productionPlanDtoIdsList = this.getPartIdsForRequestIds(
                requestDtos.stream()
                        .map(RequestDto::getId)
                        .collect(Collectors.toList())
        );

        if (!productionPlanDtoIdsList.isEmpty()) {
            Map<Long, Long> partRequestMap = productionPlanDtoIdsList
                    .stream()
                    .collect(Collectors.toMap(ProductionPlanDto::getRequestId, ProductionPlanDto::getId));

            for (RequestDto requestDto : requestDtos) {
                Long prodId = partRequestMap.get(requestDto.getId());
                if (prodId != null) {
                    requestDto.getState().setCode("IN_PLAN");
                    requestDto.getState().setCaption("Включено в план");
                }
            }
        }
        return requestDtos;
    }

    public List<RequestDto> getPlan() {
        List<RequestDto> result = new ArrayList<>();
        List<Request> list = requestRepository.findAll();

        List<ProductionPlan> listPlans = productionPlanRepository.findAll();
        Map<Long, ProductionPlan> map = listPlans.stream()
                .collect(Collectors.toMap(ProductionPlan::getRequestId, c -> c));

        for (Request request : list) {
            if(map.containsKey(request.getId())) {
                RequestDto dto = new RequestDto();
                dto.setRequestDtoFromEntity(request);
                ProductionPlan plan = map.get(dto.getRequestId());
                dto.setId(plan.getId());
                dto.setPriority(plan.getPriority());
                dto.setState(new StateDto());
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
                result.add(dto);
            }
        }

        result = result.stream().sorted(Comparator.comparing(RequestDto::getPriority)).
                collect(Collectors.toList());

        return result;
    }

    private List<RequestDto> getRequestsFromApi() {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/crm/requests")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<List<RequestDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<RequestDto>>() {
                    });

            List<RequestDto> result = responseEntity.getBody();
            if (result == null)
                System.out.println("Null");
            return result;
        } catch (Exception e) {
            System.out.println("impossible");
        }
        return null;
    }

    public RequestDto getRequestById(Long id) {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/crm/requests/" + id.toString())
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<RequestDto> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<RequestDto>() {
                    });

            RequestDto result = responseEntity.getBody();
            if (result == null)
                System.out.println("Null");
            return result;
        } catch (Exception e) {
            System.out.println("impossible");
        }
        return null;
    }

    public List<RequestPositionDto> getRequestPositionById(Long id) {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/crm/requests/" + id.toString() + "/items")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<List<RequestPositionDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<RequestPositionDto>>() {
                    });

            List<RequestPositionDto> result = responseEntity.getBody();
            if (result == null)
                System.out.println("Null");
            return result;
        } catch (Exception e) {
            System.out.println("impossible");
        }
        return null;
    }



    ////////свалка///////

//    public void sendIdToServer(Long id) {
//        String url = UriComponentsBuilder
//                .fromHttpUrl(this.url + "/crm/requests/" + id.toString() + "/set-state/in-production")
//                .build(false)
//                .toUriString();
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            HttpEntity<?> entity = new HttpEntity<>(null, headers);
//            //HttpEntity<?> entity = new HttpEntity<>(id, headers); //как вариант, id здесь может быть
//
//            ResponseEntity<List<DemoDto>> responseEntity = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    entity,
//                    new ParameterizedTypeReference<List<DemoDto>>() {
//                    });
//
//            List<DemoDto> result = responseEntity.getBody();
//            if (result == null)
//                System.out.println("Null");
//            return result;
//        } catch (Exception e) {
//            System.out.println("impossible");
//        }
//        return null;
//    }

    public List<DemoDto> sendDemoToServer(DemoDto demoDto) {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/integration/send-dto")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(demoDto, headers);

            ResponseEntity<List<DemoDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<DemoDto>>() {
                    });

            List<DemoDto> result = responseEntity.getBody();
            if (result == null)
                System.out.println("Null");
            return result;
        } catch (Exception e) {
            System.out.println("impossible");
        }
        return new ArrayList<>();
    }

    public List<DemoDto> sendDemoListToServer(List<DemoDto> demoDtoList) {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/integration/send-dto")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(demoDtoList, headers);

            ResponseEntity<List<DemoDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<DemoDto>>() {
                    });

            List<DemoDto> result = responseEntity.getBody();
            if (result == null)
                System.out.println("Null");
            return result;
        } catch (Exception e) {
            System.out.println("impossible");
        }
        return new ArrayList<>();
    }

    private List<ProductionPlanDto> getPartIdsForRequestIds(List<Long> requestIds) {

        SqlParameterSource p = new MapSqlParameterSource()
                .addValue("requestIds", requestIds);

        String query = "select id, request_id from production_plan where request_id in (:requestIds)";

        List<ProductionPlanDto> productionPlanDtos = namedParameterJdbcTemplate.query(
                query, p, new BeanPropertyRowMapper<>(ProductionPlanDto.class, false));

        return productionPlanDtos;
    }

}
