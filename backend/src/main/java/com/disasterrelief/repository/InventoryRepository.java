package com.disasterrelief.repository;

import com.disasterrelief.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    Optional<Inventory> findByIdWithLock(@Param("id") Long id);

    Page<Inventory> findByDisasterId(Long disasterId, Pageable pageable);

    Page<Inventory> findByCategory(Inventory.InventoryCategory category, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.minThreshold")
    List<Inventory> findLowStockItems();

    Page<Inventory> findByDisasterIdAndCategory(
        Long disasterId,
        Inventory.InventoryCategory category,
        Pageable pageable
    );
}
