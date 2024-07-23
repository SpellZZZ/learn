package com.example.orderservice.service;


import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repo.OrderRepo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    final private OrderRepo orderRepo;
    final private WebClient.Builder webClientBuilder;
    final private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    OrderService (OrderRepo orderRepo, WebClient.Builder webClientBuilder, KafkaTemplate kafkaTemplate) {
        this.orderRepo  = orderRepo;
        this.webClientBuilder = webClientBuilder;
        this.kafkaTemplate = kafkaTemplate;
    }




    public String placeOrder(OrderRequest orderRequest) {
        Order order1 = new Order();
        order1.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItems()
                .stream()
                .map(this::mapToDto)
                .toList();

        order1.setOrderLineItems(orderLineItemsList);


        List<String> skuCodes = order1.getOrderLineItems().stream()
                .map(OrderLineItems::getSkuCode).toList();



        /*InventoryResponse [] inventoryResponsesArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();*/



        InventoryResponse [] inventoryResponsesArray = webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("inventory-service")
                        .path("/api/inventory")
                        .queryParam("skuCode", skuCodes)
                        .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductInStock = Arrays.stream(inventoryResponsesArray)
                .allMatch(InventoryResponse::isInStock);

        if(allProductInStock){
            orderRepo.save(order1);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order1.getOrderNumber()));
            return "Order Placed Successfully";
        } else {
            throw new IllegalArgumentException("product not in the stock");
        }



    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
