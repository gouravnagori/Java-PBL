package com.legal.controller;

import com.legal.dto.AdvisorRequest;
import com.legal.dto.AdvisorResponse;
import com.legal.model.ChatHistory;
import com.legal.service.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

/**
 * Unit Test suite for AdvisorController (Abhishi's REST APIs).
 */
@ExtendWith(MockitoExtension.class)
class AdvisorControllerTest {

    @Mock
    private AIService aiService;

    @InjectMocks
    private AdvisorController advisorController;

    private AdvisorResponse mockResponse;
    private ChatHistory mockHistory;

    @BeforeEach
    void setUp() {
        mockResponse = new AdvisorResponse("chat-1", "doc-100", "What are the risks?", "The risks include penalty fees.", "Snippet");
        mockHistory = new ChatHistory("user-1", "doc-100", "What are the risks?", "The risks include penalty fees.", AdvisorResponse.DEFAULT_DISCLAIMER, "Snippet");
    }

    @Test
    @DisplayName("POST /api/advisor/ask should return 200 OK with AdvisorResponse")
    void testAskQuestion_Success() {
        AdvisorRequest req = new AdvisorRequest("doc-100", "What are the risks?");
        when(aiService.askQuestion(any(AdvisorRequest.class), nullable(String.class))).thenReturn(mockResponse);

        ResponseEntity<?> response = advisorController.askQuestion(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(aiService, times(1)).askQuestion(any(AdvisorRequest.class), nullable(String.class));
    }

    @Test
    @DisplayName("POST /api/advisor/ask with empty question should return 400 Bad Request")
    void testAskQuestion_EmptyQuestion_ReturnsBadRequest() {
        AdvisorRequest req = new AdvisorRequest("doc-100", "");

        ResponseEntity<?> response = advisorController.askQuestion(req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(aiService);
    }

    @Test
    @DisplayName("GET /api/advisor/history/{documentId} should return Q&A history")
    void testGetHistoryByDocument() {
        when(aiService.getChatHistory(eq("doc-100"), nullable(String.class))).thenReturn(List.of(mockHistory));

        ResponseEntity<?> response = advisorController.getHistoryByDocument("doc-100");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(aiService, times(1)).getChatHistory(eq("doc-100"), nullable(String.class));
    }
}
