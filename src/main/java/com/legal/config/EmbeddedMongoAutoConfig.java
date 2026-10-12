package com.legal.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring Auto-Configuration guaranteeing that the embedded MongoDB server
 * is actively listening on localhost:27017 before Spring Data Mongo clients connect,
 * ensuring seamless execution in both live application and test contexts.
 */
@Configuration
public class EmbeddedMongoAutoConfig {

    static {
        EmbeddedMongoLauncher.startIfOffline();
    }
}
