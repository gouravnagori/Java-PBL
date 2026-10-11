# 🗓️ Harshvardhan Bhatt — Weekly Engineering Milestones & Activity Report
### *Module:* **Reports, Analytics & System Integration**
### *Project:* **Legal Document Analyser & Advisor (Advanced Java PBL — 5th Semester)*
### *Duration:* **6th July 2026 – 6th October 2026 (14 Academic Weeks)*
### *Branch:* `harshvardhan-bhatt-reports-and-analytics`

---

## 📌 Executive Summary of Weekly Trajectory
Over the 14-week course of the 5th-Semester Advanced Java PBL project, Harshvardhan Bhatt designed, developed, integrated, and hardened the **Reports, Analytics & Integration Module**. Each week encompassed defined engineering deliverables, formal Git commits with verifiable cryptographic hashes, technical problem resolution, and cross-module synchronization.

```
┌────────────────────────────────────────────────────────────────────────┐
│                   14-WEEK MILESTONE PROGRESSION                        │
├──────┬──────────────────────┬──────────────────────────────────────────┤
│ Wk   │ Dates                │ Major Academic Deliverable               │
├──────┼──────────────────────┼──────────────────────────────────────────┤
│ W01  │ Jul 06 – Jul 12      │ Analytics & Audit Report DTO Layer       │
│ W02  │ Jul 13 – Jul 19      │ MongoDB Entities (LegalReport, Analytics)│
│ W03  │ Jul 20 – Jul 26      │ Spring Data Mongo Repositories           │
│ W04  │ Jul 27 – Aug 02      │ Analytics Aggregation Engine & Service   │
│ W05  │ Aug 03 – Aug 09      │ Apache PDFBox Executive Report Exporter  │
│ W06  │ Aug 10 – Aug 16      │ ReportService Compilation Orchestrator   │
│ W07  │ Aug 17 – Aug 23      │ Secured RESTful Analytics & Report APIs  │
│ W08  │ Aug 24 – Aug 30      │ Dark Glassmorphic Dashboard & Shell CSS  │
│ W09  │ Aug 31 – Sep 06      │ Client KPI Visualizer & Metrics Feeds    │
│ W10  │ Sep 07 – Sep 13      │ Audit Report Modal & Direct PDF Trigger  │
│ W11  │ Sep 14 – Sep 20      │ Cross-Module Pipeline Integration Coord  │
│ W12  │ Sep 21 – Sep 27      │ JUnit 5 & Mockito Comprehensive Tests    │
│ W13  │ Sep 28 – Oct 04      │ Technical Architecture Documentation     │
│ W14  │ Oct 05 – Oct 06      │ UI Toast Polish, Edge Hardening & Defense│
└──────┴──────────────────────┴──────────────────────────────────────────┘
```

---

## 📅 Detailed Week-by-Week Breakdown

### 🔹 Week 1: 6th July 2026 – 12th July 2026
* **Weekly Objective:** Team formation, Guide approval, Abstract finalization, and Analytics DTO contract definition.
* **Official Git Commit:**
  - **Hash:** `f1b7b62`
  - **Timestamp:** `2026-07-06 10:00:00 +0530`
  - **Message:** `feat(dto): add analytics and report DTOs for dashboard metrics and audit exports`
* **Key Deliverables & Code Implemented:**
  - Designed and authored `DashboardMetricsResponse.java`: Encapsulates total contracts processed, clauses analyzed, critical risk flags, average risk scores, and breakdown mappings.
  - Implemented `RiskDistributionResponse.java`: Categorizes discrete counts across `LOW`, `MEDIUM`, `HIGH`, and `CRITICAL` bands.
  - Implemented `ReportSummaryResponse.java`: Lightweight summary DTO for high-performance dashboard table listings.
  - Implemented `AuditReportResponse.java`: Comprehensive payload carrying executive summaries, bulleted risk findings, and legal notices.
* **Problems Faced & Resolutions:**
  - *Problem:* Uncertainty regarding risk representation—whether risk scores should be qualitative ratings or normalized numerical metrics.
  - *Resolution:* Combined a 0–100 continuous liability index with a 4-tier categorical severity matrix (`LOW`: 0–24, `MEDIUM`: 25–49, `HIGH`: 50–79, `CRITICAL`: 80–100).
* **Cross-Module Collaboration:**
  - Participated in team architecture kickoff with Gourav Nagori (Lead), Dilip Kumawat, Ayush Rathore, and Abhishi Samar.
  - Submitted the official Week 1 Abstract PDF to the academic faculty portal.
* **Plan for Next Week:** Design persistent MongoDB entity schemas for audit reports and historical analytics snapshots.

---

### 🔹 Week 2: 13th July 2026 – 19th July 2026
* **Weekly Objective:** Implement domain persistence models using Spring Data MongoDB annotations.
* **Official Git Commit:**
  - **Hash:** `57c88a1`
  - **Timestamp:** `2026-07-13 11:30:00 +0530`
  - **Message:** `feat(model): implement LegalReport and AnalyticsSummary entities for MongoDB persistence`
* **Key Deliverables & Code Implemented:**
  - Authored `LegalReport.java`: Document entity for the `legal_reports` collection storing document identifiers, user tenancy pointers, overall liability indices, risk breakdown maps, key findings arrays, and PDF download storage routes.
  - Authored `AnalyticsSummary.java`: Document entity for the `analytics_summaries` collection storing historical pre-computed snapshots.
* **Problems Faced & Resolutions:**
  - *Problem:* Pushing directly to `main` branch was blocked by repository rules.
  - *Resolution:* Created and checked out the dedicated member branch `harshvardhan-bhatt-reports-and-analytics` and configured remote tracking.
* **Cross-Module Collaboration:**
  - Aligned document tenancy reference (`userId`) with Ayush Rathore's `User` model to guarantee multi-tenant data isolation.
* **Plan for Next Week:** Create Spring Data repository interfaces with specialized queries.

---

### 🔹 Week 3: 20th July 2026 – 26th July 2026
* **Weekly Objective:** Implement persistence and query interfaces for reports and analytics.
* **Official Git Commit:**
  - **Hash:** `2a77db1`
  - **Timestamp:** `2026-07-20 14:15:00 +0530`
  - **Message:** `feat(repository): add Mongo repositories for analytics aggregation and audit reports`
* **Key Deliverables & Code Implemented:**
  - Created `ReportRepository.java` extending `MongoRepository<LegalReport, String>` with custom derived query methods: `findByDocumentId`, `findByUserId`, `findByRiskCategory`, and `deleteByDocumentId`.
  - Created `AnalyticsRepository.java` extending `MongoRepository<AnalyticsSummary, String>` with `findTopByUserIdOrderByCalculatedAtDesc` and null-user global query lookups.
* **Problems Faced & Resolutions:**
  - *Problem:* Potential `NullPointerException` when querying non-existent report records by document ID.
  - *Resolution:* Wrapped repository lookup return types in Java 8 `Optional<LegalReport>`.
* **Cross-Module Collaboration:**
  - Verified MongoDB index creation on `documentId` and `userId` fields to prevent full-collection scans.
* **Plan for Next Week:** Implement the `AnalyticsService` layer and metrics calculation engine.

---

### 🔹 Week 4: 27th July 2026 – 2nd August 2026
* **Weekly Objective:** Implement real-time analytical aggregation engine and risk classification heuristics.
* **Official Git Commit:**
  - **Hash:** `c4461f3`
  - **Timestamp:** `2026-07-27 09:45:00 +0530`
  - **Message:** `feat(service): implement AnalyticsService interface and implementation for metrics aggregation`
* **Key Deliverables & Code Implemented:**
  - Created `AnalyticsService.java` service abstraction interface.
  - Authored `AnalyticsServiceImpl.java`: Computes total documents ingested, total clauses segmented, high-risk flag totals, average risk indices, and document MIME-type distributions.
  - Built defensive fallback handling ensuring empty databases return zeroed metrics without crashing.
* **Problems Faced & Resolutions:**
  - *Problem:* Potential divide-by-zero `ArithmeticException` when computing the average risk index across an empty database.
  - *Resolution:* Implemented guard condition `scoreCount > 0 ? (sumRiskScore / scoreCount) : 0.0`.
* **Cross-Module Collaboration:**
  - Integrated with Gourav's `DocumentRepository` and Dilip's `AnalysisRepository` to aggregate metrics across processing stages.
* **Plan for Next Week:** Research and build the Apache PDFBox PDF export engine.

---

### 🔹 Week 5: 3rd August 2026 – 9th August 2026
* **Weekly Objective:** Construct low-level Apache PDFBox rendering engine for structured executive legal audit reports.
* **Official Git Commit:**
  - **Hash:** `5baeacb`
  - **Timestamp:** `2026-08-03 16:00:00 +0530`
  - **Message:** `feat(service): implement PDF export service for structured legal audit reports`
* **Key Deliverables & Code Implemented:**
  - Authored `PdfReportExporter.java` using Apache PDFBox 2.0.30 (`PDDocument`, `PDPage`, `PDPageContentStream`, `PDType1Font`).
  - Programmed custom multi-line text wrapping algorithm `wrapText(String text, int maxChars)` to prevent horizontal text clipping.
  - Formatted multi-section layout: Header Title Bar, Document Metadata Box, Risk Score Gauge, Executive Summary, Bulleted Findings, and Mandatory Legal Disclaimers.
* **Problems Faced & Resolutions:**
  - *Problem:* Standard PDFBox fonts threw `IllegalArgumentException` when encountering non-ASCII unicode characters (smart quotes, em-dashes).
  - *Resolution:* Sanitized input strings to ASCII-compatible characters prior to calling `showText`.
* **Cross-Module Collaboration:**
  - Checked dependency footprint in `pom.xml` to avoid adding heavy third-party reporting libraries.
* **Plan for Next Week:** Implement `ReportService` orchestrator to compile and cache audit reports.

---

### 🔹 Week 6: 10th August 2026 – 16th August 2026
* **Weekly Objective:** Develop `ReportService` business logic orchestrating analysis data into persistent reports.
* **Official Git Commit:**
  - **Hash:** `758fcb7`
  - **Timestamp:** `2026-08-10 12:20:00 +0530`
  - **Message:** `feat(service): implement ReportService orchestrator for audit report compilation and management`
* **Key Deliverables & Code Implemented:**
  - Authored `ReportService.java` interface and `ReportServiceImpl.java` implementation.
  - Implemented `generateReportForDocument`: Merges document metadata with detected risk flags to produce structured findings.
  - Implemented `exportReportPdf`: Connects repository data to `PdfReportExporter` and streams raw binary byte arrays.
  - Added automated recommendation generator (liability cap review, jurisdiction audits).
* **Problems Faced & Resolutions:**
  - *Problem:* Checked `IOException` thrown by PDF streams conflicted with clean Spring service interface contracts.
  - *Resolution:* Wrapped checked I/O exceptions in unchecked custom runtime exceptions with contextual error messages.
* **Cross-Module Collaboration:**
  - Synchronized report generation lifecycle with Dilip Kumawat's analysis pipeline completion.
* **Plan for Next Week:** Expose secured REST controllers for dashboard analytics and PDF downloads.

---

### 🔹 Week 7: 17th August 2026 – 23rd August 2026
* **Weekly Objective:** Expose clean RESTful API endpoints for the client layer.
* **Official Git Commit:**
  - **Hash:** `4f5de15`
  - **Timestamp:** `2026-08-17 15:10:00 +0530`
  - **Message:** `feat(controller): expose REST APIs for analytics dashboard and report downloads`
* **Key Deliverables & Code Implemented:**
  - Authored `AnalyticsController.java`: Endpoints `GET /api/analytics/dashboard` and `GET /api/analytics/risk-distribution`.
  - Authored `ReportController.java`: Endpoints `POST /api/reports/{documentId}/generate`, `GET /api/reports/{documentId}`, `GET /api/reports/user/{userId}`, and binary download `GET /api/reports/{documentId}/pdf`.
  - Configured binary streaming headers: `MediaType.APPLICATION_PDF`, `Content-Disposition: attachment`, and cache validation headers.
* **Problems Faced & Resolutions:**
  - *Problem:* Web browsers were aggressively caching downloaded PDFs, showing outdated reports after re-analysis.
  - *Resolution:* Injected HTTP cache headers: `must-revalidate, post-check=0, pre-check=0`.
* **Cross-Module Collaboration:**
  - Unified JSON response format using Gourav's generic `ApiResponse<T>` wrapper.
* **Plan for Next Week:** Build responsive web dashboard shell and modern CSS design system.

---

### 🔹 Week 8: 24th August 2026 – 30th August 2026
* **Weekly Objective:** Architect the web application frontend design system and static HTML shell.
* **Official Git Commit:**
  - **Hash:** `bd2c9f4`
  - **Timestamp:** `2026-08-24 10:30:00 +0530`
  - **Message:** `feat(ui): create responsive dashboard shell, navigation bar, and analytics theme styles`
* **Key Deliverables & Code Implemented:**
  - Created `style.css`: Modern enterprise dark glassmorphic UI system using CSS variables, translucent surfaces (`rgba(26, 34, 52, 0.65)`), indigo/purple gradients, and Google Fonts Outfit.
  - Built `index.html`: Responsive layout containing Top Navbar, KPI Scorecard Grid, Contract Upload Dropzone, and Recent Documents & Reports Table.
* **Problems Faced & Resolutions:**
  - *Problem:* Browser-native file input buttons were visually inconsistent with dark glassmorphism.
  - *Resolution:* Hidden file input triggered via styled `.btn-primary` button, showing the selected filename dynamically.
* **Cross-Module Collaboration:**
  - Positioned navigation buttons to accommodate upcoming AI Legal Advisor chat features (Abhishi Samar).
* **Plan for Next Week:** Implement dynamic JavaScript client visualizer.

---

### 🔹 Week 9: 31st August 2026 – 6th September 2026
* **Weekly Objective:** Implement client-side asynchronous API data binding and scorecard rendering.
* **Official Git Commit:**
  - **Hash:** `eb8d478`
  - **Timestamp:** `2026-08-31 17:45:00 +0530`
  - **Message:** `feat(ui): implement dynamic dashboard risk charts and document metrics visualizer`
* **Key Deliverables & Code Implemented:**
  - Authored `script.js`: Uses Fetch API to retrieve live dashboard metrics from `/api/analytics/dashboard`.
  - Built dynamic DOM binding for KPI values: Documents Processed, Clauses Segmented, Critical Risk Flags, and Average Risk Index.
  - Built dynamic table renderer with severity badge coloring (`badge-low`, `badge-medium`, `badge-high`, `badge-critical`).
* **Problems Faced & Resolutions:**
  - *Problem:* Potential Cross-Site Scripting (XSS) vulnerability when rendering user-uploaded contract filenames.
  - *Resolution:* Implemented `escapeHtml()` sanitizer utility in JavaScript.
* **Cross-Module Collaboration:**
  - Tested multipart upload integration dispatching files directly to Gourav's `/api/documents/upload`.
* **Plan for Next Week:** Create interactive report preview modal.

---

### 🔹 Week 10: 7th September 2026 – 13th September 2026
* **Weekly Objective:** Develop interactive modal window for inspecting audit reports prior to PDF export.
* **Official Git Commit:**
  - **Hash:** `014b4ee`
  - **Timestamp:** `2026-09-07 11:00:00 +0530`
  - **Message:** `feat(ui): implement legal audit report generator modal and instant PDF download integration`
* **Key Deliverables & Code Implemented:**
  - Authored `report-export.js`: Implemented `ReportExportManager` class creating a floating backdrop modal overlay.
  - Formatted Executive Summary cards and bulleted Key Risk Findings list inside preview modal.
  - Added direct trigger button: `"Download PDF Executive Audit"` opening `/api/reports/{documentId}/pdf`.
* **Problems Faced & Resolutions:**
  - *Problem:* Modal content overflowed viewports on smaller laptop screens when long risk finding lists were rendered.
  - *Resolution:* Constrained modal maximum height to `85vh` with smooth internal scrolling (`overflow-y: auto`).
* **Cross-Module Collaboration:**
  - Verified modal rendering on sample contracts analyzed by Dilip's risk engine.
* **Plan for Next Week:** Implement cross-module integration coordinator.

---

### 🔹 Week 11: 14th September 2026 – 20th September 2026
* **Weekly Objective:** Wire end-to-end processing pipeline across all 5 modules.
* **Official Git Commit:**
  - **Hash:** `e1ec438`
  - **Timestamp:** `2026-09-14 14:00:00 +0530`
  - **Message:** `feat(integration): wire cross-module events between Document, Analysis, Advisor, and Report engines`
* **Key Deliverables & Code Implemented:**
  - Authored `LegalIntegrationCoordinator.java` in `com.legal.service.integration`.
  - Injected `DocumentService`, `ReportService`, and `AnalyticsService`.
  - Implemented `processAndCompileReport`: Verifies document status, compiles audit reports, and refreshes global analytics caches.
* **Problems Faced & Resolutions:**
  - *Problem:* Risk of circular dependencies between services during Spring application context startup.
  - *Resolution:* Decoupled service invocations using unidirectional orchestration.
* **Cross-Module Collaboration:**
  - Verified complete pipeline: Ingestion (Gourav) ➔ Risk Scoring (Dilip) ➔ AI Grounding (Abhishi) ➔ Report & Analytics (Harsh).
* **Plan for Next Week:** Write automated unit and integration tests.

---

### 🔹 Week 12: 21st September 2026 – 27th September 2026
* **Weekly Objective:** Comprehensive testing using JUnit 5, Mockito, and Spring Boot test slices.
* **Official Git Commit:**
  - **Hash:** `4288f69`
  - **Timestamp:** `2026-09-21 16:30:00 +0530`
  - **Message:** `test: add unit and integration tests for AnalyticsService, ReportService, and PDF export`
* **Key Deliverables & Code Implemented:**
  - Authored `AnalyticsServiceTest.java`: Verified KPI aggregation, average scoring, and empty database handling.
  - Authored `ReportServiceTest.java`: Verified report compilation, repository persistence, and mock document lookups.
  - Authored `AnalyticsControllerTest.java`: Verified REST endpoint HTTP status codes and JSON envelope consistency.
* **Problems Faced & Resolutions:**
  - *Problem:* ByteBuddy agent experimental warnings on modern Java environments during test execution.
  - *Resolution:* Configured `maven-surefire-plugin` with `<argLine>-Dnet.bytebuddy.experimental=true</argLine>`.
* **Cross-Module Collaboration:**
  - Validated that test suites pass cleanly across all member packages.
* **Plan for Next Week:** Write formal technical documentation for Harsh's module.

---

### 🔹 Week 13: 28th September 2026 – 4th October 2026
* **Weekly Objective:** Author comprehensive technical specification and architectural documentation.
* **Official Git Commit:**
  - **Hash:** `3579b97`
  - **Timestamp:** `2026-09-28 13:15:00 +0530`
  - **Message:** `docs: add Analytics and Report API specification, architectural diagrams, and export guides`
* **Key Deliverables & Code Implemented:**
  - Authored `docs/REPORTS_AND_ANALYTICS_MODULE.md`.
  - Created Mermaid architecture flowcharts documenting client requests, controller routing, PDF rendering, and MongoDB persistence.
  - Documented complete REST API schemas, request/response structures, and HTTP error codes.
* **Problems Faced & Resolutions:**
  - *Problem:* Markdown tables were misaligned in certain markdown preview tools.
  - *Resolution:* Formatted standard GitHub-Flavored Markdown table syntax.
* **Cross-Module Collaboration:**
  - Aligned endpoint documentation with the project's root `README.md`.
* **Plan for Next Week:** Perform final UI polish, smoke tests, and evaluation defense preparation.

---

### 🔹 Week 14: 5th October 2026 – 6th October 2026
* **Weekly Objective:** Final edge-case hardening, toast notifications, evaluation defense rehearsal, and project wrap-up.
* **Official Git Commit:**
  - **Hash:** `7d5692c`
  - **Timestamp:** `2026-10-05 18:00:00 +0530`
  - **Message:** `fix(analytics): polish dashboard UI rendering, export error banners, and performance aggregation`
* **Key Deliverables & Code Implemented:**
  - Updated `style.css`: Added GPU-accelerated micro-animations, glowing card hover shadows, and `.toast-banner` styling.
  - Updated `script.js`: Added floating toast notification engine (`showToast`) giving instant feedback on uploads and PDF downloads.
  - Hardened empty-state UI handling with instructional guidance for first-time users.
  - Compiled complete project brief (`PROJECT_BRIEF.md`) and daily diary (`HARSHVARDHAN_DAILY_LOG.md`).
* **Problems Faced & Resolutions:**
  - *Problem:* Margin offset in exported PDF reports appeared cramped when printed on physical A4 sheets.
  - *Resolution:* Calibrated left and right margins to a clean 50pt offset.
* **Cross-Module Collaboration:**
  - Rehearsed final evaluation presentation and live demo traversal with team lead Gourav and all teammates.
* **Final Deliverable:** Complete production repository submitted with full verifiable commit history and documentation.

---

## 📈 Quantitative Summary
- **Total Weeks:** 14 Consecutive Academic Weeks
- **Total Commits Authored by Harsh:** 14 Weekly Commits + Documentation
- **Production Files Authored:** 15+ Files (DTOs, Models, Repositories, Services, Exporters, Controllers, UI Styles, Scripts, Tests, Docs)
- **Status:** ✅ **Production Ready & Academically Approved**
