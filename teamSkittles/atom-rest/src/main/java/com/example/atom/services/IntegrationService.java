package com.example.atom.services;

import com.example.atom.dto.DemoDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntegrationService {

    private String serverUrl = "server";

    private final RestTemplate restTemplate = new RestTemplate();

    public List<DemoDto> getDataFromServer() {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl + "/integration/get-data")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<List<DemoDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
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
        return null;
    }

    public List<DemoDto> sendIdToServer(Long id) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl + "/integration/send-id/" + id.toString())
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);
            //HttpEntity<?> entity = new HttpEntity<>(id, headers); //как вариант, id здесь может быть

            ResponseEntity<List<DemoDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
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
        return null;
    }

    public List<DemoDto> sendDemoToServer(DemoDto demoDto) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl + "/integration/send-dto")
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
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl + "/integration/send-dto")
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

}
