package com.disasterrelief.repository;

import com.disasterrelief.entity.Volunteer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    Optional<Volunteer> findByUserId(Long userId);

    Page<Volunteer> findByAvailability(Volunteer.Availability availability, Pageable pageable);

    @Query("""
        SELECT v FROM Volunteer v
        WHERE v.availability = 'AVAILABLE'
        AND v.isVerified = true
        AND (:skill IS NULL OR v.skills LIKE %:skill%)
        ORDER BY (6371 * ACOS(
            COS(RADIANS(:lat)) * COS(RADIANS(v.latitude)) *
            COS(RADIANS(v.longitude) - RADIANS(:lng)) +
            SIN(RADIANS(:lat)) * SIN(RADIANS(v.latitude))
        )) ASC
        """)
    List<Volunteer> findAvailableVolunteersNearby(
        @Param("lat") double latitude,
        @Param("lng") double longitude,
        @Param("skill") String skill,
        Pageable pageable
    );

    @Query("""
        SELECT v FROM Volunteer v
        WHERE (6371 * ACOS(
            COS(RADIANS(:lat)) * COS(RADIANS(v.latitude)) *
            COS(RADIANS(v.longitude) - RADIANS(:lng)) +
            SIN(RADIANS(:lat)) * SIN(RADIANS(v.latitude))
        )) <= :radiusKm
        """)
    List<Volunteer> findWithinRadius(
        @Param("lat") double latitude,
        @Param("lng") double longitude,
        @Param("radiusKm") double radiusKm
    );
}
