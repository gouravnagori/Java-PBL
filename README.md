# ⚖️ Legal Document Analyser & Advisor

**Academic Program:** Advanced Java Project-Based Learning (PBL) — 5th Semester  
**Architecture:** Distributed Modular Spring Boot 3 & Enterprise Architecture  
**Unified Integration Branch:** `main`

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Database](https://img.shields.io/badge/Database-MongoDB%20%7C%20H2%20JPA-47A248?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![Build](https://img.shields.io/badge/Build-Passing-10B981?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

---

## 👥 Project Team & Module Ownership

| Member | Module | Key Deliverables |
| :--- | :--- | :--- |
| **Gourav Nagori** | **Document Ingestion & Processing** | Apache PDFBox, Apache POI DOCX, Plaintext parsers, Groq Multimodal Vision OCR, File storage lifecycle |
| **Ayush Rathore** | **User, Auth & Case Management** | Spring Security 6, Stateless JWT Provider, BCrypt hashing, Multi-tenant Case Management & ownership isolation |
| **Dilip Kumawat** | **Legal Analysis & Risk Detection Engine** | 13-Rule Risk Detection Engine, Clause segmentation, Severity Matrix (LOW/MED/HIGH/CRITICAL), Risk score scoring |
| **Harshvardhan Bhatt** | **Audit Reports & Analytics** | Audit Report Compiler, Real-time Dashboard KPI Aggregator, Apache PDFBox Legal Audit Report Exporter |
| **Abhishi Samar** | **System Architecture & UML Modeling** | UML Class & Component Diagrams, State Transition models, SRS System flow verification |

---

## 🏛️ System Architecture & Workflow

```
[Client Web Browser (HTML5 / Glassmorphic UI)]
         │
         ├──► /api/auth/**      ──► [AuthService & JWT Token Provider] ──► [H2 / JPA User Repository]
         ├──► /api/cases/**     ──► [CaseService (Multi-tenant)]       ──► [H2 / JPA Case Repository]
         ├──► /api/documents/** ──► [DocumentService & Groq OCR]       ──► [MongoDB Document Repository]
         ├──► /api/analysis/**  ──► [Dilip's Risk Scoring Engine]      ──► [MongoDB Analysis & Risk Repositories]
         └──► /api/reports/**   ──► [ReportService & PDF Exporter]     ──► [MongoDB Report & Analytics Repositories]
```

### Key System Specifications
- **UML Diagram:** [`Legal Document Analysis System UML Diagram.png`](./Legal%20Document%20Analysis%20System%20UML%20Diagram.png)
- **Software Requirements Specification:** [`docs/Software_Requirements_Specification.md`](./docs/Software_Requirements_Specification.md)
- **Ayush Module Documentation:** [`docs/AYUSH_MODULE_SPECIFICATION.md`](./docs/AYUSH_MODULE_SPECIFICATION.md)
- **Reports & Analytics Specification:** [`docs/REPORTS_AND_ANALYTICS_MODULE.md`](./docs/REPORTS_AND_ANALYTICS_MODULE.md)

---

## 🚀 Getting Started & Local Execution

### Prerequisites
- **JDK 21** installed and configured
- **Apache Maven 3.9+**

### Running the Application

```bash
# Clone the repository
git clone https://github.com/gouravnagori/Java-PBL.git
cd Java-PBL

# Build the project
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run
```

Once started, the application will be live at:
- **Web Application Portal:** [http://localhost:8080](http://localhost:8080)
- **Authentication & Security Portal:** [http://localhost:8080/auth.html](http://localhost:8080/auth.html)
- **Case Management Console:** [http://localhost:8080/cases.html](http://localhost:8080/cases.html)
- **H2 In-Memory Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  *(JDBC URL: `jdbc:h2:mem:legaldb`, User: `sa`, Password: `password`)*

---

## 🛡️ API Endpoints Summary

### 1. Authentication & Security (`/api/auth`)
- `POST /api/auth/register` - Create user account with BCrypt password hashing
- `POST /api/auth/login` - Authenticate user credentials and return stateless JWT token
- `GET /api/auth/me` - Fetch profile of currently authenticated user

### 2. Case Management (`/api/cases`)
- `POST /api/cases` - Create legal case folder (Isolated by user ownership)
- `GET /api/cases` - Retrieve all cases belonging to authenticated user
- `GET /api/cases/{id}` - Retrieve case details with document counts
- `PATCH /api/cases/{id}/status` - Update status (`ACTIVE`, `PENDING_REVIEW`, `CLOSED`, `ARCHIVED`)

### 3. Document Management & Ingestion (`/api/documents`)
- `POST /api/documents/upload` - Upload PDF, DOCX, TXT, or scanned image contract
- `GET /api/documents/{id}/content` - Stream extracted contractual text
- `GET /api/documents/{id}/metadata` - Fetch file metadata and character statistics

### 4. Legal Risk Analysis Engine (`/api/analysis`)
- `POST /api/analysis/{documentId}` - Execute clause extraction & 13-rule risk scoring
- `GET /api/analysis/{documentId}` - Retrieve complete analysis and clause breakdown
- `GET /api/analysis/{documentId}/risks` - Retrieve HIGH and CRITICAL risk alerts

### 5. Audit Reports & Analytics (`/api/reports` & `/api/analytics`)
- `GET /api/analytics/dashboard` - Global KPI scorecard & risk distribution metrics
- `GET /api/reports/user/{userId}` - List compiled legal audit reports
- `GET /api/reports/{documentId}/pdf` - Instant downloadable PDF audit report
