# 📋 Weekly Project Report Submissions (Week 1 to Week 12)
### *Student Name:* **Harshvardhan Bhatt**
### *Module:* **Reports, Analytics & Integration**
### *Repository:* [gouravnagori/Java-PBL](https://github.com/gouravnagori/Java-PBL)
### *Branch:* [`harshvardhan-bhatt-reports-and-analytics`](https://github.com/gouravnagori/Java-PBL/tree/harshvardhan-bhatt-reports-and-analytics)

---

### 🔹 Week 1
* **Week \***: `Week 1`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/f1b7b623f62ee7fad6e713651ac95cdf2b0f6933`
* **Work Completed This Week \***:  
  - Authored `DashboardMetricsResponse.java` DTO encapsulating global and user-level dashboard metrics (processed document counts, clause totals, high-risk flags, average score).
  - Authored `RiskDistributionResponse.java` for discrete severity tier distributions (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).
  - Authored `ReportSummaryResponse.java` and `AuditReportResponse.java` defining payloads for executive audit summaries and PDF download links.
* **Problems Faced**:  
  Ambiguity in how risk metrics should be modeled—whether as qualitative ratings or continuous scores. Resolved by establishing a dual 0–100 numerical index mapped to a 4-tier categorical severity matrix.
* **Plan for Next Week**:  
  Implement persistent MongoDB document entities (`LegalReport` and `AnalyticsSummary`) to store compiled audit reports and analytical metrics snapshots.

---

### 🔹 Week 2
* **Week \***: `Week 2`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/57c88a14393f10eb5c090b1433198dc72888269b`
* **Work Completed This Week \***:  
  - Authored `LegalReport.java` entity annotated with `@Document(collection = "legal_reports")` for MongoDB persistence, storing document titles, risk categories, key findings lists, and PDF storage paths.
  - Authored `AnalyticsSummary.java` entity annotated with `@Document(collection = "analytics_summaries")` for pre-aggregated metrics snapshots.
* **Problems Faced**:  
  Direct push to `main` branch was blocked by repository rules. Created and checked out the dedicated member feature branch `harshvardhan-bhatt-reports-and-analytics`.
* **Plan for Next Week**:  
  Implement Spring Data Mongo repository interfaces for `LegalReport` and `AnalyticsSummary` with specialized lookup queries.

---

### 🔹 Week 3
* **Week \***: `Week 3`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/2a77db191f7885cfc8217d22b46d2e48036ca185`
* **Work Completed This Week \***:  
  - Created `ReportRepository.java` extending `MongoRepository` with custom derived query methods: `findByDocumentId`, `findByUserId`, and `findByRiskCategory`.
  - Created `AnalyticsRepository.java` extending `MongoRepository` with queries for top historical summaries and null-user global analytics lookups.
* **Problems Faced**:  
  Potential `NullPointerException` when querying non-existent report records by document ID. Wrapped lookup return types in `Optional<LegalReport>`.
* **Plan for Next Week**:  
  Design and implement the `AnalyticsService` layer and risk calculation algorithms.

---

### 🔹 Week 4
* **Week \***: `Week 4`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/c4461f30512e4214eefc58410ceb473875d23dfa`
* **Work Completed This Week \***:  
  - Created `AnalyticsService.java` interface defining `getGlobalDashboardMetrics`, `getUserDashboardMetrics`, and `getRiskDistribution`.
  - Authored `AnalyticsServiceImpl.java` to compute total documents, clauses segmented, critical risk flags, and document type distributions.
  - Implemented risk score categorization logic (scores >= 80 as CRITICAL, >= 50 as HIGH, >= 25 as MEDIUM, else LOW).
* **Problems Faced**:  
  Potential divide-by-zero `ArithmeticException` when computing average risk score on an empty database. Added guard condition `scoreCount > 0 ? (sumRiskScore / scoreCount) : 0.0`.
* **Plan for Next Week**:  
  Build the PDF export engine for executive legal audit reports using Apache PDFBox.

---

### 🔹 Week 5
* **Week \***: `Week 5`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/5baeacb480bf9face87773356547c47e94c9b31b`
* **Work Completed This Week \***:  
  - Authored `PdfReportExporter.java` using Apache PDFBox 2.0.30 (`PDDocument`, `PDPage`, `PDPageContentStream`, `PDType1Font`).
  - Programmed word-wrapping algorithm `wrapText(String text, int maxChars)` to prevent horizontal text clipping.
  - Designed report layout sections: Header Title Bar, Document Metadata Box, Risk Score Gauge, Executive Summary, Bulleted Findings, and Mandatory Legal Disclaimers.
* **Problems Faced**:  
  Standard Helvetica font threw encoding exceptions on non-ASCII characters (curly quotes, em-dashes). Added string sanitizer to normalize input text to standard ASCII prior to rendering.
* **Plan for Next Week**:  
  Implement `ReportService` orchestrator to compile analysis data into structured audit reports.

---

### 🔹 Week 6
* **Week \***: `Week 6`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/758fcb78a2882a266d27f249e90822a57c13479c`
* **Work Completed This Week \***:  
  - Created `ReportService.java` interface and authored `ReportServiceImpl.java` implementation.
  - Implemented `generateReportForDocument`: Merges document metadata with detected risk flags to produce structured findings.
  - Implemented `exportReportPdf`: Invocates `PdfReportExporter` to stream binary byte arrays.
  - Added automated recommendation generator (liability cap review, jurisdiction compliance).
* **Problems Faced**:  
  Checked `IOException` thrown by PDF streams conflicted with clean Spring service contracts. Wrapped checked I/O exceptions in unchecked runtime exceptions with clear messages.
* **Plan for Next Week**:  
  Expose REST API endpoints for analytics dashboards and PDF report downloads.

---

### 🔹 Week 7
* **Week \***: `Week 7`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/4f5de157d25652383f98613ba2cc9160723fe455`
* **Work Completed This Week \***:  
  - Authored `AnalyticsController.java`: Endpoints `GET /api/analytics/dashboard` and `GET /api/analytics/risk-distribution`.
  - Authored `ReportController.java`: Endpoints `POST /api/reports/{documentId}/generate`, `GET /api/reports/{documentId}`, `GET /api/reports/user/{userId}`, and binary streaming `GET /api/reports/{documentId}/pdf`.
  - Configured binary streaming headers: `MediaType.APPLICATION_PDF`, `Content-Disposition: attachment`, and cache validation headers.
* **Problems Faced**:  
  Web browsers aggressively cached downloaded PDFs, showing outdated reports after re-analysis. Added cache control headers: `must-revalidate, post-check=0, pre-check=0`.
* **Plan for Next Week**:  
  Design responsive web dashboard shell and modern glassmorphic CSS styling.

---

### 🔹 Week 8
* **Week \***: `Week 8`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/bd2c9f4170f3624ea1793a0ff4d550837f385099`
* **Work Completed This Week \***:  
  - Created `style.css`: Modern enterprise dark glassmorphic UI design system using CSS variables, translucent surfaces (`rgba(26, 34, 52, 0.65)`), indigo/purple gradients, and Google Fonts Outfit.
  - Built `index.html`: Responsive layout containing Top Navbar, KPI Scorecard Grid, Contract Upload Dropzone, and Recent Documents & Reports Table.
* **Problems Faced**:  
  Native browser file input buttons looked inconsistent with dark glassmorphism. Hid native input and triggered it via a custom styled `.btn-primary` button.
* **Plan for Next Week**:  
  Implement dynamic client-side JavaScript for live analytics fetching and visual scorecards.

---

### 🔹 Week 9
* **Week \***: `Week 9`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/eb8d4780ebb1770992f4aef6c2f37bdec7563458`
* **Work Completed This Week \***:  
  - Authored `script.js`: Uses Fetch API to retrieve live dashboard metrics from `/api/analytics/dashboard`.
  - Built dynamic DOM binding for KPI values: Documents Processed, Clauses Segmented, Critical Risk Flags, and Average Risk Index.
  - Built dynamic table renderer with severity badge coloring (`badge-low`, `badge-medium`, `badge-high`, `badge-critical`).
* **Problems Faced**:  
  Potential Cross-Site Scripting (XSS) vulnerability when rendering user-uploaded contract filenames. Implemented `escapeHtml()` sanitizer utility in JavaScript.
* **Plan for Next Week**:  
  Implement legal audit report generator modal and instant PDF download integration in the frontend.

---

### 🔹 Week 10
* **Week \***: `Week 10`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/014b4eea9ffd63bd4b3a61440bff09f7bbae1083`
* **Work Completed This Week \***:  
  - Authored `report-export.js`: Implemented `ReportExportManager` class creating a floating backdrop modal overlay.
  - Formatted Executive Summary cards and bulleted Key Risk Findings list inside preview modal.
  - Added direct trigger button: `"Download PDF Executive Audit"` opening `/api/reports/{documentId}/pdf`.
* **Problems Faced**:  
  Modal content overflowed viewports on smaller screens when long risk finding lists were rendered. Constrained modal maximum height to `85vh` with smooth internal scrolling (`overflow-y: auto`).
* **Plan for Next Week**:  
  Implement cross-module integration coordinator connecting Document, Analysis, Advisor, and Report engines.

---

### 🔹 Week 11
* **Week \***: `Week 11`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/e1ec438e152e291db25c26de0463ea5a35ffd36f`
* **Work Completed This Week \***:  
  - Authored `LegalIntegrationCoordinator.java` in `com.legal.service.integration`.
  - Injected `DocumentService`, `ReportService`, and `AnalyticsService`.
  - Implemented `processAndCompileReport`: Verifies document status, compiles audit reports, and refreshes global analytics caches.
* **Problems Faced**:  
  Risk of circular dependencies between services during Spring application context startup. Decoupled service invocations using unidirectional orchestration.
* **Plan for Next Week**:  
  Write comprehensive unit and integration tests using JUnit 5 and Mockito.

---

### 🔹 Week 12
* **Week \***: `Week 12`
* **GitHub / Report Link**:  
  `https://github.com/gouravnagori/Java-PBL/commit/4288f698cb74af115af8c1ea6d01b148ae627904`
* **Work Completed This Week \***:  
  - Authored `AnalyticsServiceTest.java`: Verified KPI aggregation, average scoring, and empty database handling.
  - Authored `ReportServiceTest.java`: Verified report compilation, repository persistence, and mock document lookups.
  - Authored `AnalyticsControllerTest.java`: Verified REST endpoint HTTP status codes and JSON envelope consistency.
* **Problems Faced**:  
  ByteBuddy agent experimental warnings on modern Java environments during test execution. Configured `maven-surefire-plugin` with `<argLine>-Dnet.bytebuddy.experimental=true</argLine>`.
* **Plan for Next Week**:  
  Author technical module documentation, architectural diagrams, and final evaluation presentation deck.
