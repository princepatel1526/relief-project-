package com.disasterrelief.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "news_timeline_updates", indexes = {
        @Index(name = "idx_news_timeline_news", columnList = "news_id"),
        @Index(name = "idx_news_timeline_time", columnList = "update_timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsTimelineUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private NewsUpdate news;

    @Column(name = "update_text", nullable = false, columnDefinition = "TEXT")
    private String updateText;

    @Column(name = "update_timestamp", nullable = false)
    private LocalDateTime timestamp;
}
