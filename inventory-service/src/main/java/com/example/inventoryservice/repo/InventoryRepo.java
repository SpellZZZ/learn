package com.example.inventoryservice.repo;

import com.example.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Long> {
    List<Inventory> findByskuCode(String skuCode);
}
