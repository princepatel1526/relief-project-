package com.disasterrelief.repository;

import com.disasterrelief.entity.ReliefRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReliefRequestRepository extends JpaRepository<ReliefRequest, Long> {

    Page<ReliefRequest> findByDisasterId(Long disasterId, Pageable pageable);

    Page<ReliefRequest> findByStatus(ReliefRequest.RequestStatus status, Pageable pageable);

    @Query("""
        SELECT r FROM ReliefRequest r
        WHERE r.status IN ('PENDING', 'SUBMITTED', 'PENDING_VERIFICATION')
        ORDER BY r.priorityScore DESC, r.urgencyLevel DESC, r.createdAt ASC
        """)
    List<ReliefRequest> findPendingRequestsByPriority(Pageable pageable);

    @Query("""
        SELECT r FROM ReliefRequest r
        WHERE r.disaster.id = :disasterId
        AND r.status IN ('PENDING', 'SUBMITTED', 'PENDING_VERIFICATION', 'VERIFIED', 'ASSIGNED', 'EN_ROUTE', 'IN_PROGRESS')
        ORDER BY r.priorityScore DESC, r.urgencyLevel DESC, r.createdAt ASC
        """)
    List<ReliefRequest> findActiveRequestsByDisaster(@Param("disasterId") Long disasterId, Pageable pageable);

    List<ReliefRequest> findByAssignedToId(Long volunteerId);

    long countByStatus(ReliefRequest.RequestStatus status);

    long countByDisasterIdAndStatus(Long disasterId, ReliefRequest.RequestStatus status);

    @Query("SELECT r.requestType, COUNT(r) FROM ReliefRequest r GROUP BY r.requestType")
    List<Object[]> countGroupByType();

    @Query("SELECT r.status, COUNT(r) FROM ReliefRequest r GROUP BY r.status")
    List<Object[]> countGroupByStatus();

    @Query("SELECT AVG(r.priorityScore) FROM ReliefRequest r WHERE r.priorityScore > 0")
    Double avgPriorityScore();

    @Query("""
        SELECT r FROM ReliefRequest r
        WHERE r.status IN ('PENDING', 'SUBMITTED', 'PENDING_VERIFICATION')
        ORDER BY r.priorityScore DESC, r.urgencyLevel DESC, r.createdAt ASC
        """)
    List<ReliefRequest> findByPriorityScore(Pageable pageable);
}
