package com.disasterrelief.repository;

import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.NewsUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsUpdateRepository extends JpaRepository<NewsUpdate, Long> {

    Page<NewsUpdate> findByStatusOrderByCreatedAtDesc(NewsUpdate.NewsStatus status, Pageable pageable);

    Page<NewsUpdate> findBySeverityOrderByCreatedAtDesc(Disaster.Severity severity, Pageable pageable);

    Page<NewsUpdate> findByDisasterTypeContainingIgnoreCaseOrderByCreatedAtDesc(String disasterType, Pageable pageable);

    @Query("""
        SELECT n FROM NewsUpdate n
        WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(n.content) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(COALESCE(n.location, '')) LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY n.createdAt DESC
        """)
    Page<NewsUpdate> search(String q, Pageable pageable);

    boolean existsBySourceIncidentId(Long sourceIncidentId);

    long count();
}
