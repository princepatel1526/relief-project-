-- Create disaster news feed tables.

CREATE TABLE IF NOT EXISTS news_updates (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    title               VARCHAR(220) NOT NULL,
    summary             VARCHAR(400) NOT NULL,
    content             TEXT NOT NULL,
    image_url           VARCHAR(500),
    disaster_type       VARCHAR(100),
    severity            VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status              VARCHAR(20) NOT NULL DEFAULT 'MONITORING',
    location            VARCHAR(200),
    latitude            DOUBLE,
    longitude           DOUBLE,
    source_incident_id  BIGINT,
    affected_people     INT,
    rescue_progress     INT,
    created_by          BIGINT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_news_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_news_status (status),
    INDEX idx_news_severity (severity),
    INDEX idx_news_created (created_at DESC),
    INDEX idx_news_source_incident (source_incident_id)
);

CREATE TABLE IF NOT EXISTS news_timeline_updates (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    news_id           BIGINT NOT NULL,
    update_text       TEXT NOT NULL,
    update_timestamp  DATETIME NOT NULL,
    CONSTRAINT fk_news_timeline_news FOREIGN KEY (news_id) REFERENCES news_updates(id) ON DELETE CASCADE,
    INDEX idx_news_timeline_news (news_id),
    INDEX idx_news_timeline_time (update_timestamp DESC)
);
