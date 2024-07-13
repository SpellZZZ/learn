package com.example.orderservice.dto;



import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    List<OrderLineItemsDto> orderLineItems;
}
