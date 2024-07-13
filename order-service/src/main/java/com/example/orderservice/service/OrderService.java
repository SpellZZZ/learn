package com.example.orderservice.service;


import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repo.OrderRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    final private OrderRepo orderRepo;

    OrderService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }


    public void placeOrder(OrderRequest orderRequest) {
        Order order1 = new Order();
        order1.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItems()
                .stream()
                .map(this::mapToDto)
                .toList();

        order1.setOrderLineItems(orderLineItemsList);

        orderRepo.save(order1);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
