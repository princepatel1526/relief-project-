package com.disasterrelief.repository;

import com.disasterrelief.entity.Disaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DisasterRepository extends JpaRepository<Disaster, Long> {

    Page<Disaster> findByStatus(Disaster.DisasterStatus status, Pageable pageable);

    Page<Disaster> findBySeverity(Disaster.Severity severity, Pageable pageable);

    Page<Disaster> findByStatusAndSeverity(Disaster.DisasterStatus status, Disaster.Severity severity, Pageable pageable);

    List<Disaster> findByStatusIn(List<Disaster.DisasterStatus> statuses);

    @Query("""
        SELECT d FROM Disaster d
        WHERE d.status IN ('REPORTED', 'ACTIVE')
        AND (6371 * ACOS(
            COS(RADIANS(:lat)) * COS(RADIANS(d.latitude)) *
            COS(RADIANS(d.longitude) - RADIANS(:lng)) +
            SIN(RADIANS(:lat)) * SIN(RADIANS(d.latitude))
        )) <= :radiusKm
        ORDER BY d.severity DESC, d.createdAt DESC
        """)
    List<Disaster> findActiveDisastersNearby(
        @Param("lat") double latitude,
        @Param("lng") double longitude,
        @Param("radiusKm") double radiusKm
    );

    long countByStatus(Disaster.DisasterStatus status);

    long countBySeverity(Disaster.Severity severity);

    @Query("SELECT d.disasterType.name, COUNT(d) FROM Disaster d GROUP BY d.disasterType.name")
    List<Object[]> countGroupByType();

    @Query("SELECT d.severity, COUNT(d) FROM Disaster d GROUP BY d.severity")
    List<Object[]> countGroupBySeverity();
}
