package com.example.atom.readers;

import com.example.atom.dto.DemoDto;
import com.example.atom.dto.MachineDto;
import com.example.atom.dto.RequestDto;
import com.example.atom.dto.RequestPositionDto;
import com.example.atom.entities.MachineType;
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

    public LinkedHashMap<MachineType, LinkedHashMap<String, Integer>> getAllMachines() {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/mnf/machines")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<LinkedHashMap<MachineType, LinkedHashMap<String, Integer>>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<LinkedHashMap<MachineType, LinkedHashMap<String, Integer>>>() {
                    });

            LinkedHashMap<MachineType, LinkedHashMap<String, Integer>> result = responseEntity.getBody();
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

    public void setStatusToMachine(Integer port, String status) {
        String url = UriComponentsBuilder
                .fromHttpUrl(this.cutUrl + port + "/set/" + status)
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);
            //HttpEntity<?> entity = new HttpEntity<>(id, headers); //как вариант, id здесь может быть

            ResponseEntity<List<DemoDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<DemoDto>>() {
                    });

            List<DemoDto> result = responseEntity.getBody();
            if (result == null)
                System.out.println("Null");
//            return result;
        } catch (Exception e) {
            System.out.println("impossible");
        }
//        return null;
    }

}
