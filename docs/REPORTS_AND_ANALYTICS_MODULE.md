# 📊 Reports, Analytics & PDF Export Module Documentation
### *Author & Module Lead:* **Harshvardhan Bhatt**

---

## 📌 Module Overview

The **Reports, Analytics & Integration Module** provides global and tenant-level business intelligence, real-time KPI metrics, contract risk distribution aggregation, and executive PDF audit report exports for the **Legal Document Analyser & Advisor** platform.

---

## 🏛️ Module Architecture

```mermaid
flowchart TD
    Client[Web UI Dashboard] -->|GET /api/analytics/dashboard| AnalyticsCtrl[AnalyticsController]
    Client -->|GET /api/reports/{id}/pdf| ReportCtrl[ReportController]
    
    AnalyticsCtrl --> AnalyticsSvc[AnalyticsService / AnalyticsServiceImpl]
    ReportCtrl --> ReportSvc[ReportService / ReportServiceImpl]
    
    ReportSvc --> PdfExporter[PdfReportExporter - Apache PDFBox Engine]
    
    AnalyticsSvc --> DocRepo[(DocumentRepository)]
    AnalyticsSvc --> AnalysisRepo[(AnalysisRepository)]
    ReportSvc --> ReportRepo[(ReportRepository)]
```

---

## 🔌 API Endpoints Specification

### 1. Analytics Endpoints

| Method | Endpoint | Description | Response DTO |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/analytics/dashboard` | Returns system-wide or user-specific KPI scorecard metrics and risk distributions | `DashboardMetricsResponse` |
| `GET` | `/api/analytics/risk-distribution` | Returns severity counts (Low, Medium, High, Critical) and average risk score | `RiskDistributionResponse` |

### 2. Audit Report Endpoints

| Method | Endpoint | Description | Response DTO / Output |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/reports/{documentId}/generate` | Compiles risk analysis into a persistent `LegalReport` entity | `LegalReport` |
| `GET` | `/api/reports/{documentId}` | Fetches legal audit report details | `AuditReportResponse` |
| `GET` | `/api/reports/user/{userId}` | Retrieves list of audit report summaries for user dashboard | `List<ReportSummaryResponse>` |
| `GET` | `/api/reports/{documentId}/pdf` | Generates and streams executive PDF audit report | `application/pdf` (binary stream) |

---

## 📄 PDF Export Engine Specification

The PDF export engine (`PdfReportExporter.java`) utilizes **Apache PDFBox 2.0.30** to generate multi-section legal audit reports:
1. **Header & Title Bar**: Project branding and report timestamping.
2. **Document Metadata**: File title, type, ID, and ownership mapping.
3. **Risk Score Evaluation**: Numerical score (0-100) and severity classification badge.
4. **Executive Summary**: Multiline wrapped plain-language clause summary.
5. **Key Risk Findings**: Bulleted breakdown of flagged predatory clauses and severity levels.
6. **Legal Disclaimer**: Informational tool notice and attorney consultation advice.
