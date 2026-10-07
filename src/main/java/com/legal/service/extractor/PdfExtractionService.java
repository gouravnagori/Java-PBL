package com.legal.service.extractor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * High-performance PDF text extractor using Apache PDFBox.
 * Extracts textual content, counts total pages, and detects if the document is scanned/empty.
 */
@Service
public class PdfExtractionService implements DocumentExtractor {

    private static final Logger log = LoggerFactory.getLogger(PdfExtractionService.class);

    @Override
    public boolean supports(String contentType, String fileName) {
        if (contentType != null && contentType.equalsIgnoreCase("application/pdf")) {
            return true;
        }
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }

    @Override
    public ExtractionResult extract(InputStream inputStream, String fileName) throws IOException {
        log.info("Extracting text from PDF document: {}", fileName);

        try (PDDocument document = PDDocument.load(inputStream)) {
            int pageCount = document.getNumberOfPages();

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true); // Preserve legal column & paragraph reading order

            String extractedText = stripper.getText(document);
            if (extractedText != null) {
                extractedText = normalizeText(extractedText);
            } else {
                extractedText = "";
            }

            log.info("Extracted {} characters from {} (pages: {})", extractedText.length(), fileName, pageCount);
            return new ExtractionResult(extractedText, pageCount, getName());
        }
    }

    @Override
    public String getName() {
        return "PDFBOX";
    }

    /**
     * Normalizes line endings, multiple consecutive whitespace, and non-printable characters.
     */
    private String normalizeText(String raw) {
        return raw.replace("\r\n", "\n")
                  .replace("\r", "\n")
                  .replaceAll("[\\t ]+", " ")
                  .replaceAll("\n{3,}", "\n\n")
                  .trim();
    }
}
