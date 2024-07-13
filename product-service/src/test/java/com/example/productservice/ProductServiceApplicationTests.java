package com.example.productservice;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.model.Product;
import com.example.productservice.repo.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    ProductRepository productRepository;


    @BeforeEach
    void clearDatabase() {
        productRepository.deleteAll();
    }
    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest pr = getProductRequest();
        String prString = objectMapper.writeValueAsString(pr);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(prString))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void shouldReturnProducts() throws Exception {

        List<ProductRequest> prList = getProductsRequestList();
        prList.forEach(pr -> productRepository.save(
                Product.builder()
                        .name(pr.getName())
                        .description(pr.getDescription())
                        .price(pr.getPrice())
                        .build()
        ));


        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

        Assertions.assertEquals(prList.size(), productRepository.findAll().size());
    }

    private List<ProductRequest> getProductsRequestList() {

        List<ProductRequest> prList = new ArrayList<>(){{
            add(
                    ProductRequest.builder()
                    .name("test 1")
                    .description("desc 1")
                    .price(BigDecimal.valueOf(1222))
                    .build()
            );
            add(    ProductRequest.builder()
                    .name("test 2")
                    .description("desc 2")
                    .price(BigDecimal.valueOf(33))
                    .build()
            );
            add(    ProductRequest.builder()
                    .name("test 3")
                    .description("desc 3")
                    .price(BigDecimal.valueOf(1234))
                    .build()
            );


        }};
        return prList;
    }


    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("test 1")
                .description("desc 1")
                .price(BigDecimal.valueOf(1222))
                .build();
    }

}
