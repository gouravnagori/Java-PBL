package com.legal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Main Entry Point for Legal Document Analyser & Advisor.
 * Unified Advanced Java PBL System (5th Semester).
 * Team: Gourav Nagori, Ayush Rathore, Dilip Kumawat, Harshvardhan Bhatt, Abhishi Samar.
 */
@SpringBootApplication
@EnableJpaRepositories(
    basePackages = "com.legal.repository",
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {com.legal.repository.LegalCaseRepository.class, com.legal.repository.UserRepository.class}
    )
)
@EnableMongoRepositories(
    basePackages = "com.legal.repository",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {com.legal.repository.LegalCaseRepository.class, com.legal.repository.UserRepository.class}
    )
)
public class LegalAnalyzerApplication {

    public static void main(String[] args) {
        com.legal.config.EmbeddedMongoLauncher.startIfOffline();
        SpringApplication.run(LegalAnalyzerApplication.class, args);
    }
}
