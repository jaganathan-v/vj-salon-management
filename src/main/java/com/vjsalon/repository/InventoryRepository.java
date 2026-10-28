package com.vjsalon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vjsalon.model.Models.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findAllByOrderByItemNameAsc();

    // Native SQL used because Hibernate 6's HQL parser cannot resolve static
    // nested class entities (Models$Inventory) by simple name in JPQL strings.
    @Query(value = "SELECT * FROM inventory WHERE current_count <= low_threshold", nativeQuery = true)
    List<Inventory> findLowStockItems();
}

