package com.example.atom.readers;

import com.example.atom.dto.DemoDto;
import com.example.atom.dto.MachineDto;
import com.example.atom.dto.RequestDto;
import com.example.atom.dto.RequestPositionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Repository
public class MachineReader {
    @Value("${api.url}")
    private String url;

    @Value("${api.cut-url}")
    private String cutUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public LinkedHashMap<String, LinkedHashMap<String, Integer>> getAllMachines() {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/mnf/machines")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<LinkedHashMap<String, LinkedHashMap<String, Integer>>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<LinkedHashMap<String, LinkedHashMap<String, Integer>>>() {
                    });

            LinkedHashMap<String, LinkedHashMap<String, Integer>> result = responseEntity.getBody();
            if (result == null)
                System.out.println("Null");
            return result;
        } catch (Exception e) {
            System.out.println("impossible");
        }
        return null;
    }

    public MachineDto getMachineStatusByPort(Integer port) {
        String url = UriComponentsBuilder.fromHttpUrl(this.cutUrl + port + "/status")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<MachineDto> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<MachineDto>() {
                    });

            MachineDto result = responseEntity.getBody();
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

    public List<DemoDto> sendIdToServer(Long id) {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/integration/send-id/" + id.toString())
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
}