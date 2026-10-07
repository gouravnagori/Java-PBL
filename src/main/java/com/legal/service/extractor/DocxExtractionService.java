package com.legal.service.extractor;

import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Microsoft Word (.docx) document extractor using Apache POI.
 * Extracts text from paragraphs, bullet points, headers, and tables.
 */
@Service
public class DocxExtractionService implements DocumentExtractor {

    private static final Logger log = LoggerFactory.getLogger(DocxExtractionService.class);

    @Override
    public boolean supports(String contentType, String fileName) {
        if (contentType != null && (
                contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || contentType.equalsIgnoreCase("application/msword"))) {
            return true;
        }
        return fileName != null && (fileName.toLowerCase().endsWith(".docx") || fileName.toLowerCase().endsWith(".doc"));
    }

    @Override
    public ExtractionResult extract(InputStream inputStream, String fileName) throws IOException {
        log.info("Extracting text from DOCX document: {}", fileName);

        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder sb = new StringBuilder();

            // 1. Extract paragraphs and headers
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                String text = paragraph.getText();
                if (text != null && !text.isBlank()) {
                    sb.append(text.trim()).append("\n");
                }
            }

            // 2. Extract tables (often critical in legal contracts for payment & clause schedules)
            List<XWPFTable> tables = document.getTables();
            for (XWPFTable table : tables) {
                for (XWPFTableRow row : table.getRows()) {
                    StringBuilder rowText = new StringBuilder();
                    for (XWPFTableCell cell : row.getTableCells()) {
                        String cellText = cell.getText();
                        if (cellText != null && !cellText.isBlank()) {
                            rowText.append(cellText.trim()).append(" | ");
                        }
                    }
                    if (rowText.length() > 0) {
                        sb.append(rowText.toString()).append("\n");
                    }
                }
            }

            String fullText = sb.toString().trim();
            // Estimate page count based on ~500 words per legal page
            int estimatedPages = Math.max(1, (int) Math.ceil((double) fullText.split("\\s+").length / 500.0));

            log.info("Extracted {} characters from DOCX {}", fullText.length(), fileName);
            return new ExtractionResult(fullText, estimatedPages, getName());
        }
    }

    @Override
    public String getName() {
        return "POI_DOCX";
    }
}
