package com.legal.service.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Plain text (.txt, .rtf, .md) legal document extractor.
 */
@Service
public class TextExtractionService implements DocumentExtractor {

    private static final Logger log = LoggerFactory.getLogger(TextExtractionService.class);

    @Override
    public boolean supports(String contentType, String fileName) {
        if (contentType != null && (contentType.startsWith("text/") || contentType.equalsIgnoreCase("application/json"))) {
            return true;
        }
        return fileName != null && (
                fileName.toLowerCase().endsWith(".txt")
                || fileName.toLowerCase().endsWith(".md")
                || fileName.toLowerCase().endsWith(".rtf"));
    }

    @Override
    public ExtractionResult extract(InputStream inputStream, String fileName) throws IOException {
        log.info("Extracting text from plain text document: {}", fileName);

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }

        String content = sb.toString().trim();
        int estimatedPages = Math.max(1, (int) Math.ceil((double) content.split("\\s+").length / 500.0));

        return new ExtractionResult(content, estimatedPages, getName());
    }

    @Override
    public String getName() {
        return "TEXT_PLAIN";
    }
}
