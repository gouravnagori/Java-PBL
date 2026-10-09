package com.legal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.dto.AdvisorRequest;
import com.legal.dto.AdvisorResponse;
import com.legal.model.ChatHistory;
import com.legal.model.LegalDocument;
import com.legal.repository.ChatHistoryRepository;
import com.legal.repository.DocumentRepository;
import com.legal.service.impl.AdvisorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test suite for AI Legal Advisor / Q&A Service (Abhishi's Module).
 * Tests question asking, context retrieval, fallback generation, chat history persistence,
 * and error/timeout resilience.
 */
@ExtendWith(MockitoExtension.class)
class AdvisorServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private ChatHistoryRepository chatHistoryRepository;

    @InjectMocks
    private AdvisorServiceImpl advisorService;

    private LegalDocument mockDocument;
    private ChatHistory mockHistory;

    @BeforeEach
    void setUp() {
        mockDocument = new LegalDocument("Test Rental Agreement", "rental.pdf", "user-123");
        mockDocument.setId("doc-1001");
        mockDocument.setExtractedText("Tenant agrees to pay rent on 1st of every month. Notice period is 30 days.");

        mockHistory = new ChatHistory("user-123", "doc-1001", "What is the notice period?", "The notice period is 30 days.", AdvisorResponse.DEFAULT_DISCLAIMER, "Notice period is 30 days.");
        mockHistory.setId("chat-500");
    }

    @Test
    @DisplayName("Should successfully generate explanation using fallback engine when API key is missing")
    void testAskQuestion_FallbackEngine() {
        AdvisorRequest request = new AdvisorRequest("doc-1001", "What are the notice period terms?");
        when(documentRepository.findById("doc-1001")).thenReturn(Optional.of(mockDocument));
        when(chatHistoryRepository.save(any(ChatHistory.class))).thenReturn(mockHistory);

        AdvisorResponse response = advisorService.askQuestion(request, "user-123");

        assertNotNull(response);
        assertEquals("doc-1001", response.getDocumentId());
        assertEquals("What are the notice period terms?", response.getQuestion());
        assertTrue(response.getAnswer().contains("Plain-Language Explanation"));
        assertNotNull(response.getDisclaimer());
        assertTrue(response.getDisclaimer().contains("Disclaimer"));

        verify(documentRepository, times(1)).findById("doc-1001");
        verify(chatHistoryRepository, times(1)).save(any(ChatHistory.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when question is empty")
    void testAskQuestion_EmptyQuestion_ThrowsException() {
        AdvisorRequest request = new AdvisorRequest("doc-1001", "   ");

        assertThrows(IllegalArgumentException.class, () -> advisorService.askQuestion(request, "user-123"));
        verifyNoInteractions(documentRepository);
        verifyNoInteractions(chatHistoryRepository);
    }

    @Test
    @DisplayName("Should retrieve Q&A chat history for a specific document")
    void testGetChatHistory_ByDocumentId() {
        when(chatHistoryRepository.findByUserIdAndDocumentIdOrderByCreatedAtAsc("user-123", "doc-1001"))
                .thenReturn(List.of(mockHistory));

        List<ChatHistory> history = advisorService.getChatHistory("doc-1001", "user-123");

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals("What is the notice period?", history.get(0).getQuestion());
        verify(chatHistoryRepository, times(1)).findByUserIdAndDocumentIdOrderByCreatedAtAsc("user-123", "doc-1001");
    }

    @Test
    @DisplayName("Should retrieve user Q&A chat history across documents")
    void testGetUserChatHistory() {
        when(chatHistoryRepository.findByUserIdOrderByCreatedAtDesc("user-123"))
                .thenReturn(List.of(mockHistory));

        List<ChatHistory> userHistory = advisorService.getUserChatHistory("user-123");

        assertNotNull(userHistory);
        assertEquals(1, userHistory.size());
        verify(chatHistoryRepository, times(1)).findByUserIdOrderByCreatedAtDesc("user-123");
    }

    @Test
    @DisplayName("Should delete chat history entries for a given document and user")
    void testClearChatHistory() {
        advisorService.clearChatHistory("doc-1001", "user-123");
        verify(chatHistoryRepository, times(1)).deleteByDocumentIdAndUserId("doc-1001", "user-123");
    }
}
