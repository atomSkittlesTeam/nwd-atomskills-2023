package com.example.atom.services;

import com.example.atom.dto.ProductDto;
import com.example.atom.entities.Product;
import com.example.atom.readers.ProductReader;
import com.example.atom.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductReader productReader;

//    @Scheduled(fixedDelay = 1000 * 20)
    @Transactional
    public void getAllProducts() {
        //вычитаю из всех реквестов, которые пришли из сервиса те, которые уже были в бд, получил новые
        List<ProductDto> allProducts = productReader.getAllProducts();
        List<Product> products = getListEntitiesFromListDtos(allProducts);
        productRepository.saveAll(products);
        productRepository.flush();
        System.out.println("Перезаписал dict product");
    }

    public List<Product> getListEntitiesFromListDtos(List<ProductDto> productDtos) {
        List<Product> products = new ArrayList<>();
        productDtos.forEach(dto -> {
            products.add(new Product(
                    dto.getId(), dto.getCode(), dto.getCaption(), dto.getMillingTime(), dto.getLatheTime()));
        });
        return products;
    }

}
