package com.disasterrelief.repository;

import com.disasterrelief.entity.DisasterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DisasterTypeRepository extends JpaRepository<DisasterType, Long> {
    Optional<DisasterType> findByName(String name);
}
