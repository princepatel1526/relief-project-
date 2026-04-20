package com.disasterrelief.repository;

import com.disasterrelief.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByLocationTypeAndIsActive(Location.LocationType locationType, Boolean isActive);

    @Query("""
        SELECT l FROM Location l
        WHERE l.isActive = true
        AND (:type IS NULL OR l.locationType = :type)
        ORDER BY (6371 * ACOS(
            COS(RADIANS(:lat)) * COS(RADIANS(l.latitude)) *
            COS(RADIANS(l.longitude) - RADIANS(:lng)) +
            SIN(RADIANS(:lat)) * SIN(RADIANS(l.latitude))
        )) ASC
        """)
    List<Location> findNearbyLocations(
        @Param("lat") double latitude,
        @Param("lng") double longitude,
        @Param("type") Location.LocationType type
    );
}
