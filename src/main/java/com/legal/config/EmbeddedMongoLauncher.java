package com.legal.config;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * Utility to automatically initialize an in-memory MongoDB instance
 * if no external MongoDB server is listening on port 27017.
 * Guarantees zero-friction local execution for developers and evaluators.
 */
public class EmbeddedMongoLauncher {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedMongoLauncher.class);
    private static MongoServer server;

    public static synchronized void startIfOffline() {
        if (server != null) {
            return;
        }

        int port = 27017;
        if (!isPortInUse("localhost", port)) {
            try {
                log.info("Starting embedded in-memory MongoDB on localhost:{}...", port);
                server = new MongoServer(new MemoryBackend());
                server.bind("localhost", port);
                log.info("Embedded in-memory MongoDB successfully active on localhost:{}", port);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (server != null) {
                        log.info("Shutting down embedded MongoDB...");
                        server.shutdown();
                    }
                }));
            } catch (Exception ex) {
                log.warn("Could not bind in-memory MongoDB to localhost:{}: {}", port, ex.getMessage());
            }
        } else {
            log.info("MongoDB instance detected on localhost:{}. Using existing database.", port);
        }
    }

    private static boolean isPortInUse(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
