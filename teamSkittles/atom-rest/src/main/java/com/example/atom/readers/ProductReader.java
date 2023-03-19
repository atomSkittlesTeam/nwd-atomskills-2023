package com.example.atom.readers;

import com.example.atom.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Repository
public class ProductReader {
    @Value("${api.url}")
    private String url;

    @Value("${api.cut-url}")
    private String cutUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<ProductDto> getAllProducts() {
        String url = UriComponentsBuilder.fromHttpUrl(this.url + "/dict/products")
                .build(false)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(null, headers);

            ResponseEntity<List<ProductDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ProductDto>>() {
                    });

            List<ProductDto> result = responseEntity.getBody();
            if (result == null)
                System.out.println("Null");
            return result;
        } catch (Exception e) {
            System.out.println("impossible");
        }
        return null;
    }
}
