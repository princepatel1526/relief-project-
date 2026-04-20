package com.disasterrelief.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RoleSchemaMigrationRunner implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V20260420_01__roles_name_to_varchar_and_standardize.sql"),
                new ClassPathResource("db/migration/V20260420_02__create_news_feed_tables.sql"),
                new ClassPathResource("db/migration/V20260420_03__extend_news_with_people_and_progress.sql")
            );
            populator.setContinueOnError(true);
            populator.execute(dataSource);
            log.info("Applied startup migrations for role standardization and news feed schema");
        } catch (Exception ex) {
            log.warn("Role schema migration skipped: {}", ex.getMessage());
        }
    }
}
