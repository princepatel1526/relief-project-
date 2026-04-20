package com.disasterrelief.repository;

import com.disasterrelief.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Page<Assignment> findByVolunteerId(Long volunteerId, Pageable pageable);
    Page<Assignment> findByVolunteerUserId(Long userId, Pageable pageable);

    Page<Assignment> findByDisasterId(Long disasterId, Pageable pageable);

    boolean existsByVolunteerIdAndReliefRequestIdAndStatusIn(
        Long volunteerId, Long reliefRequestId, List<Assignment.AssignmentStatus> statuses
    );

    @Query("""
        SELECT COUNT(a) > 0 FROM Assignment a
        WHERE a.volunteer.id = :volunteerId
        AND a.status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')
        """)
    boolean isVolunteerActivelyAssigned(@Param("volunteerId") Long volunteerId);

    List<Assignment> findByVolunteerIdAndStatusIn(Long volunteerId, List<Assignment.AssignmentStatus> statuses);

    long countByStatus(Assignment.AssignmentStatus status);
}
