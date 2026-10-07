package com.legal.service;

import com.legal.service.extractor.ExtractionResult;
import com.legal.service.extractor.PdfExtractionService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PdfExtractionServiceTest {

    private PdfExtractionService pdfExtractionService;

    @BeforeEach
    void setUp() {
        pdfExtractionService = new PdfExtractionService();
    }

    @Test
    void testSupports() {
        assertTrue(pdfExtractionService.supports("application/pdf", "contract.pdf"));
        assertTrue(pdfExtractionService.supports("application/octet-stream", "agreement.PDF"));
        assertFalse(pdfExtractionService.supports("application/msword", "document.doc"));
    }

    @Test
    void testExtractTextFromPdf() throws IOException {
        // Create an in-memory sample legal PDF using PDFBox
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("NON-DISCLOSURE AGREEMENT");
                contentStream.newLineAtOffset(0, -20);
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.showText("1. Confidential Information shall remain the property of Disclosing Party.");
                contentStream.endText();
            }
            doc.save(out);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ExtractionResult result = pdfExtractionService.extract(in, "nda.pdf");

        assertNotNull(result);
        assertEquals(1, result.getPageCount());
        assertTrue(result.getText().contains("NON-DISCLOSURE AGREEMENT"));
        assertTrue(result.getText().contains("Confidential Information"));
        assertTrue(result.getWordCount() > 5);
        assertEquals("PDFBOX", result.getExtractionMethod());
    }
}
