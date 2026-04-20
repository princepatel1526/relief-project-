package com.disasterrelief.repository;

import com.disasterrelief.entity.Victim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VictimRepository extends JpaRepository<Victim, Long> {
    Page<Victim> findByDisasterId(Long disasterId, Pageable pageable);
    Page<Victim> findByStatus(Victim.VictimStatus status, Pageable pageable);
    long countByDisasterId(Long disasterId);
}
