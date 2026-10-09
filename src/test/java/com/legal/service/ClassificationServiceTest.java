package com.legal.service;

import com.legal.model.enums.DocType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ClassificationService}.
 *
 * Verifies that the keyword-based heuristic correctly identifies
 * each supported document category (DocType).
 *
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
@DisplayName("ClassificationService — DocType detection tests")
class ClassificationServiceTest {

    private ClassificationService classificationService;

    @BeforeEach
    void setUp() {
        classificationService = new ClassificationService();
    }

    // -------------------------------------------------------------------------
    // Happy Path — Each DocType
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should classify NDA document correctly")
    void shouldClassifyNda() {
        String text = "This Non-Disclosure Agreement ('NDA') is entered into between the parties. "
            + "The receiving party agrees not to disclose any confidential information, "
            + "trade secrets, or proprietary information shared under this agreement.";

        DocType result = classificationService.classify(text);
        assertEquals(DocType.NDA, result, "Expected NDA classification");
    }

    @Test
    @DisplayName("Should classify Employment contract correctly")
    void shouldClassifyEmployment() {
        String text = "This Employment Agreement sets out the terms of employment including "
            + "salary, annual leave, notice period, employee benefits, and probationary period. "
            + "The employer agrees to provide a competitive wages package.";

        DocType result = classificationService.classify(text);
        assertEquals(DocType.EMPLOYMENT, result, "Expected EMPLOYMENT classification");
    }

    @Test
    @DisplayName("Should classify Rental agreement correctly")
    void shouldClassifyRental() {
        String text = "This Rental Agreement is signed between the Landlord and the Tenant. "
            + "The monthly rent is ₹15,000 and a security deposit of ₹30,000 is required. "
            + "The tenancy shall commence on the 1st of next month.";

        DocType result = classificationService.classify(text);
        assertEquals(DocType.RENTAL, result, "Expected RENTAL classification");
    }

    @Test
    @DisplayName("Should classify Loan agreement correctly")
    void shouldClassifyLoan() {
        String text = "This Loan Agreement is between the Lender and the Borrower. "
            + "The principal amount of ₹5,00,000 shall be repaid at an interest rate "
            + "of 12% per annum. Collateral: property deed as promissory note.";

        DocType result = classificationService.classify(text);
        assertEquals(DocType.LOAN, result, "Expected LOAN classification");
    }

    @Test
    @DisplayName("Should classify Service Agreement correctly")
    void shouldClassifyServiceAgreement() {
        String text = "This Service Agreement defines the scope of work and deliverables. "
            + "The service provider agrees to deliver all milestones per the statement of work. "
            + "The service level agreement guarantees 99.9% uptime for professional services.";

        DocType result = classificationService.classify(text);
        assertEquals(DocType.SERVICE_AGREEMENT, result, "Expected SERVICE_AGREEMENT classification");
    }

    @Test
    @DisplayName("Should classify SaaS agreement correctly")
    void shouldClassifySaas() {
        String text = "This Software as a Service (SaaS) subscription agreement grants you "
            + "a license key and API access to the cloud service. The data processing agreement "
            + "governs acceptable use of the platform.";

        DocType result = classificationService.classify(text);
        assertEquals(DocType.SAAS, result, "Expected SAAS classification");
    }

    // -------------------------------------------------------------------------
    // Edge / Boundary Cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return GENERAL for blank text")
    void shouldReturnGeneralForBlankText() {
        DocType result = classificationService.classify("   ");
        assertEquals(DocType.GENERAL, result, "Blank text should default to GENERAL");
    }

    @Test
    @DisplayName("Should return GENERAL for null text")
    void shouldReturnGeneralForNullText() {
        DocType result = classificationService.classify(null);
        assertEquals(DocType.GENERAL, result, "Null text should default to GENERAL");
    }

    @Test
    @DisplayName("Should return GENERAL for unrecognised contract text")
    void shouldReturnGeneralForUnrecognisedText() {
        String text = "This agreement is hereby made between the parties as described herein.";
        DocType result = classificationService.classify(text);
        assertEquals(DocType.GENERAL, result, "Generic text should default to GENERAL");
    }

    @Test
    @DisplayName("Should handle mixed-case keyword matching correctly")
    void shouldHandleMixedCaseMatching() {
        String text = "NON-DISCLOSURE AGREEMENT. All CONFIDENTIAL INFORMATION and TRADE SECRETS "
            + "disclosed under this NDA shall not be shared.";

        DocType result = classificationService.classify(text);
        assertEquals(DocType.NDA, result, "Classification should be case-insensitive");
    }
}
