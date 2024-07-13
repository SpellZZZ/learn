package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.repo.InventoryRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    final private InventoryRepo inventoryRepo;

    InventoryService(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode){
        return inventoryRepo.findBySkuCodeIn(skuCode)
                .stream()
                .map(inv ->
                    InventoryResponse.builder()
                            .skuCode(inv.getSkuCode())
                            .isInStock(inv.getQuantity() > 0)
                            .build()
                ).toList();
    }


}
