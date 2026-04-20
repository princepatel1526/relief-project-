package com.disasterrelief.repository;

import com.disasterrelief.entity.NewsTimelineUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsTimelineUpdateRepository extends JpaRepository<NewsTimelineUpdate, Long> {
    List<NewsTimelineUpdate> findByNewsIdOrderByTimestampDesc(Long newsId);
}
