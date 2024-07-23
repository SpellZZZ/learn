package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.repo.InventoryRepo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class InventoryService {

    final private InventoryRepo inventoryRepo;

    InventoryService(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode){
        log.info("wait started");
        Thread.sleep(10);
        log.info("wait ended");
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
