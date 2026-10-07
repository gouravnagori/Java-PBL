package com.legal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Main Entry Point for Legal Document Analyser & Advisor.
 * Module: Document Management & Processing (Gourav Nagori)
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.legal.repository")
public class LegalAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegalAnalyzerApplication.class, args);
    }
}
