package com.example.inventoryservice.service;

import com.example.inventoryservice.repo.InventoryRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    final private InventoryRepo inventoryRepo;

    InventoryService(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode){
        return inventoryRepo.findByskuCode(skuCode).size() == 1;
    }


}
