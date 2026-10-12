package com.legal.config;

import com.legal.model.*;
import com.legal.model.enums.AnalysisStatus;
import com.legal.model.enums.DocType;
import com.legal.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Startup Data Initializer for Legal Document Analyser & Advisor.
 * Ensures the platform always has permanent accounts (Admin & Standard User)
 * and rich seeded legal audit contracts & reports, eliminating empty dashboards
 * and repeated registration requirements upon restart.
 */
@Component
@Order(10)
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private LegalCaseRepository legalCaseRepository;

    @Autowired(required = false)
    private DocumentRepository documentRepository;

    @Autowired(required = false)
    private ReportRepository reportRepository;

    @Autowired(required = false)
    private AnalysisRepository analysisRepository;

    @Autowired(required = false)
    private AnalysisResultRepository analysisResultRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Checking LexAdvisor system seed data...");
        seedUsers();
        seedCases();
        seedDocumentsAndReports();
        log.info("LexAdvisor system data initialization complete.");
    }

    private void seedUsers() {
        // 1. Administrator Account
        if (!userRepository.existsByEmail("admin@lexadvisor.com")) {
            User admin = new User(
                    "System Administrator",
                    "admin@lexadvisor.com",
                    passwordEncoder.encode("admin123"),
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
            log.info("Seeded default Administrator account: admin@lexadvisor.com / admin123");
        }

        // 2. Standard Reviewer Account
        if (!userRepository.existsByEmail("user@lexadvisor.com")) {
            User reviewer = new User(
                    "Legal Reviewer",
                    "user@lexadvisor.com",
                    passwordEncoder.encode("user123"),
                    Role.ROLE_USER
            );
            userRepository.save(reviewer);
            log.info("Seeded default User account: user@lexadvisor.com / user123");
        }

        // 3. Academic Evaluator Account
        if (!userRepository.existsByEmail("evaluator@legalpbl.edu")) {
            User evaluator = new User(
                    "Project Evaluator",
                    "evaluator@legalpbl.edu",
                    passwordEncoder.encode("password123"),
                    Role.ROLE_USER
            );
            userRepository.save(evaluator);
            log.info("Seeded Evaluator demo account: evaluator@legalpbl.edu / password123");
        }
    }

    private void seedCases() {
        if (legalCaseRepository == null) return;

        try {
            User reviewer = userRepository.findByEmail("user@lexadvisor.com").orElse(null);
            if (reviewer != null && legalCaseRepository.countByUserId(reviewer.getId()) == 0) {
                LegalCase demoCase = new LegalCase(
                        "CASE-2026-001",
                        "Acme Vendor Contract Audit",
                        "Comprehensive contractual risk inspection for master vendor services agreement.",
                        "Acme Global Technologies",
                        CaseCategory.GENERAL_CONTRACT,
                        reviewer
                );
                legalCaseRepository.save(demoCase);
                log.info("Seeded sample legal case for user: {}", reviewer.getEmail());
            }
        } catch (Exception ex) {
            log.warn("Non-fatal: could not seed sample legal cases: {}", ex.getMessage());
        }
    }

    private void seedDocumentsAndReports() {
        if (documentRepository == null || reportRepository == null) return;

        try {
            long existingReports = reportRepository.count();
            if (existingReports > 0) {
                log.info("Existing audit reports found (count: {}). Skipping sample contract seed.", existingReports);
                return;
            }

            log.info("No audit reports found. Pre-seeding 4 verified legal audit contracts for immediate admin visibility...");

            // -------------------------------------------------------------
            // Sample 1: Enterprise Master Services Agreement (HIGH RISK)
            // -------------------------------------------------------------
            createContractBundle(
                    "doc_seed_msa_01",
                    "user@lexadvisor.com",
                    "Enterprise Master Services Agreement (MSA)",
                    "Vendor_Master_Services_Agreement_2026.pdf",
                    "PDF",
                    DocType.EMPLOYMENT,
                    78.0,
                    "HIGH",
                    "Critical indemnification exposure with unlimited financial liability and unilateral immediate termination clauses detected. Renegotiation recommended prior to execution.",
                    List.of(
                            "[HIGH] INDEMNIFICATION: Unlimited indemnification liability without reciprocal indemnification protection.",
                            "[HIGH] TERMINATION: Client may terminate for convenience with 24-hour notice without compensatory payout.",
                            "[MEDIUM] GOVERNING_LAW: Foreign jurisdiction dispute resolution with mandatory unilateral arbitration.",
                            "[MEDIUM] AUTO_RENEWAL: Automatic 24-month contract renewal without written reminder window."
                    ),
                    Map.of("LOW", 1, "MEDIUM", 2, "HIGH", 2, "CRITICAL", 0),
                    List.of(
                            "Cap indemnification liability to aggregate fees paid in the preceding 12 months.",
                            "Require 30 days prior written notice for convenience termination with pro-rata settlement.",
                            "Align jurisdiction to mutual domestic forum."
                    )
            );

            // -------------------------------------------------------------
            // Sample 2: Commercial Office Lease Deed (CRITICAL RISK)
            // -------------------------------------------------------------
            createContractBundle(
                    "doc_seed_lease_02",
                    "admin@lexadvisor.com",
                    "Commercial Real Estate Lease Agreement",
                    "Commercial_Property_Lease_Agreement.pdf",
                    "PDF",
                    DocType.RENTAL,
                    86.0,
                    "CRITICAL",
                    "Severe landlord bias with unrestricted security deposit forfeiture and compounding escalation clauses. DO NOT SIGN in current form.",
                    List.of(
                            "[CRITICAL] LIABILITY: Landlord holds zero liability for premises defects or business disruption.",
                            "[CRITICAL] PAYMENT_TERMS: 18% annual compounding rent escalation with discretionary maintenance surcharges.",
                            "[HIGH] TERMINATION: Immediate re-entry and forfeiture of tenant property upon 3-day notice."
                    ),
                    Map.of("LOW", 0, "MEDIUM", 1, "HIGH", 1, "CRITICAL", 2),
                    List.of(
                            "Enforce 15-day cure notice period prior to any default declaration.",
                            "Cap rent escalation to CPI or maximum 5% annually.",
                            "Establish escrow requirement for security deposit refund."
                    )
            );

            // -------------------------------------------------------------
            // Sample 3: Mutual Non-Disclosure Agreement (LOW RISK / SAFE)
            // -------------------------------------------------------------
            createContractBundle(
                    "doc_seed_nda_03",
                    "user@lexadvisor.com",
                    "Bilateral Mutual Non-Disclosure Agreement (MNDA)",
                    "Mutual_Non_Disclosure_Agreement.docx",
                    "DOCX",
                    DocType.NDA,
                    18.0,
                    "LOW",
                    "Balanced bilateral confidentiality terms conforming to standard trade secret protections. Standard safe contract.",
                    List.of(
                            "[LOW] CONFIDENTIALITY: Standard 3-year confidentiality term with customary statutory carve-outs.",
                            "[LOW] GOVERNING_LAW: Standard local state court jurisdiction."
                    ),
                    Map.of("LOW", 4, "MEDIUM", 0, "HIGH", 0, "CRITICAL", 0),
                    List.of(
                            "Ensure marking requirements are followed for verbal confidential disclosures.",
                            "Execute bilateral counterparts."
                    )
            );

            // -------------------------------------------------------------
            // Sample 4: Cloud SaaS Enterprise SLA (MEDIUM RISK)
            // -------------------------------------------------------------
            createContractBundle(
                    "doc_seed_sla_04",
                    "evaluator@legalpbl.edu",
                    "Cloud SaaS Enterprise Service Level Agreement",
                    "SaaS_Enterprise_Service_Level_Agreement.pdf",
                    "PDF",
                    DocType.COMMERCIAL,
                    45.0,
                    "MEDIUM",
                    "Service level agreement with standard uptime commitments but constrained SLA rebate remedies and automatic renewal.",
                    List.of(
                            "[MEDIUM] LIABILITY_LIMITATION: Service credit is sole and exclusive remedy for system unavailability.",
                            "[MEDIUM] AUTO_RENEWAL: 60-day non-renewal notification deadline.",
                            "[LOW] PAYMENT_TERMS: Net-30 invoice settlement with 1.5% late payment interest."
                    ),
                    Map.of("LOW", 2, "MEDIUM", 2, "HIGH", 0, "CRITICAL", 0),
                    List.of(
                            "Include right of termination if uptime dips below 98% for three consecutive billing cycles.",
                            "Set calendar reminders 75 days ahead of renewal deadline."
                    )
            );

            log.info("Successfully seeded 4 initial legal documents and compiled audit reports!");

        } catch (Exception ex) {
            log.warn("Non-fatal: could not seed sample documents/reports: {}", ex.getMessage(), ex);
        }
    }

    private void createContractBundle(
            String docId,
            String userId,
            String title,
            String fileName,
            String format,
            DocType docType,
            double riskScore,
            String riskCategory,
            String executiveSummary,
            List<String> keyRiskFindings,
            Map<String, Integer> severityBreakdown,
            List<String> recommendedNextSteps
    ) {
        // 1. LegalDocument
        LegalDocument doc = new LegalDocument();
        doc.setId(docId);
        doc.setUserId(userId);
        doc.setTitle(title);
        doc.setOriginalFileName(fileName);
        doc.setStatus(DocumentStatus.PROCESSED);
        doc.setExtractedText(executiveSummary);

        DocumentMetadata meta = new DocumentMetadata();
        meta.setFileName(fileName);
        meta.setContentType(format.equals("PDF") ? "application/pdf" : "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        meta.setFileSize(45000L);
        meta.setFileHash(UUID.randomUUID().toString().replace("-", ""));
        meta.setCharacterCount(executiveSummary.length());
        meta.setPageCount(4);
        meta.setWordCount(350);
        meta.setExtractionMethod("PRELOADED_INSPECTION_SEED");
        doc.setMetadata(meta);
        documentRepository.save(doc);

        // 2. LegalReport
        LegalReport report = new LegalReport();
        report.setId("rep_" + docId);
        report.setDocumentId(docId);
        report.setUserId(userId);
        report.setDocumentTitle(title);
        report.setDocumentType(docType != null ? docType.name() : "LEGAL_CONTRACT");
        report.setOverallRiskScore(riskScore);
        report.setRiskCategory(riskCategory);
        report.setExecutiveSummary(executiveSummary);
        report.setKeyRiskFindings(keyRiskFindings);
        report.setSeverityBreakdown(severityBreakdown);
        report.setRecommendedNextSteps(recommendedNextSteps);
        report.setDisclaimerNotice("INFORMATIONAL NOTICE: Legal audit report produced by automated NLP analysis. Consult licensed legal counsel for disputes.");
        report.setPdfStoragePath("/api/reports/" + docId + "/pdf");
        reportRepository.save(report);

        // 3. AnalysisRecord
        if (analysisRepository != null) {
            AnalysisRecord ar = new AnalysisRecord();
            ar.setId("rec_" + docId);
            ar.setDocumentId(docId);
            ar.setUserId(userId);
            ar.setFileName(fileName);
            ar.setDocumentType(docType != null ? docType.name() : "LEGAL_CONTRACT");
            ar.setOverallRiskScore(riskScore);
            ar.setSummary(executiveSummary);
            ar.setExecutiveSummary(executiveSummary);
            ar.setAnalyzedAt(LocalDateTime.now().minusHours(3));
            ar.setClauses(List.of(
                    "Clause 1: Scope of Services and Mutual Obligations.",
                    "Clause 2: Indemnification and Liability Allocation.",
                    "Clause 3: Termination for Convenience and Breach.",
                    "Clause 4: Governing Law and Dispute Forum."
            ));
            List<AnalysisRecord.RiskFlag> flags = new ArrayList<>();
            for (String finding : keyRiskFindings) {
                flags.add(new AnalysisRecord.RiskFlag("CONTRACT_CLAUSE", finding, riskCategory));
            }
            ar.setRiskFlags(flags);
            analysisRepository.save(ar);
        }

        // 4. Analysis
        if (analysisResultRepository != null) {
            Analysis a = new Analysis();
            a.setId("analysis_" + docId);
            a.setUserId(userId);
            a.setDocumentId(docId);
            a.setDocType(docType);
            a.setStatus(AnalysisStatus.COMPLETED);
            a.setCompositeRiskScore(riskScore);
            a.setRiskSummary(executiveSummary);
            a.setTotalClausesDetected(4);
            a.setTotalRisksFound(keyRiskFindings.size());
            a.setStartedAt(LocalDateTime.now().minusHours(3));
            a.setCompletedAt(LocalDateTime.now().minusHours(3).plusMinutes(2));
            analysisResultRepository.save(a);
        }
    }
}
