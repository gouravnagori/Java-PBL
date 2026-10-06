# ⚖️ Legal Document Analyser & Advisor
### 🌿 Feature Branch: `dilip` — Legal Analysis & Risk Detection Module
**Module Owner:** Dilip Kumawat  
**Academic Program:** Advanced Java Project-Based Learning (PBL) — 5th Semester  

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-Hibernate-6DB33F?style=for-the-badge)](https://spring.io/projects/spring-data-jpa)
[![Testing](https://img.shields.io/badge/Testing-JUnit_5%20%7C%20Mockito-25A162?style=for-the-badge)](https://junit.org/junit5/)
[![SRS Document](https://img.shields.io/badge/Docs-SRS_Specification-1E40AF?style=for-the-badge)](#-software-requirements-specification-srs)

---

## 🔗 Main Repository & Central Documentation Link

> 📌 **Central Repository Link:**  
> **👉 [gouravnagori/Java-PBL (Main Branch)](https://github.com/gouravnagori/Java-PBL/tree/main)**  
> 📄 **Main Architecture & Full Roadmap:** **[View main README.md](https://github.com/gouravnagori/Java-PBL/blob/main/README.md)**

### 📝 Brief Summary of Main Branch Updates
The **`main`** branch coordinates the complete 5-member team architecture and project lifecycle:
- **Global Architecture:** Layered Spring Boot 3 enterprise application integrating Document Processing, Risk Analysis, JWT Authentication, Contextual AI Advisor, and Analytics Reporting.
- **6-Day Accelerated Roadmap:** Simulating a 12-week semester sprint across 5 individual feature branches (`gourav`, `dilip`, `ayush`, `abhishi`, `harsh`).
- **Isolation Policy:** All feature branches maintain independent deliverables and test suites without direct unauthorized merging into release branches.

---

## 🎯 Dilip's Module: Legal Analysis & Risk Detection Engine

This branch houses the core analytical and risk evaluation engine for the Legal Document Analyser & Advisor system.

### 📌 Module Responsibilities & Scope
1. **Clause Ingestion & Segmentation:** Receives cleaned, extracted legal text payloads from the Document Processing module (Gourav Nagori) and identifies clause boundaries.
2. **Clause Classification:** Recognizes critical contractual clauses including:
   - *Confidentiality & Non-Disclosure*
   - *Indemnification & Liability Limitation*
   - *Termination & Severance Clauses*
   - *Non-Compete & Non-Solicitation*
   - *Governing Law & Dispute Jurisdiction*
   - *Payment Terms, Penalties & Auto-Renewal*
3. **Rule-Based Risk Scoring:** Applies regex heuristics and deterministic rule evaluation to detect unfair, one-sided, or missing protections.
4. **Severity Matrix:** Classifies detected contractual risks into four distinct severity levels:
   - 🟢 `LOW` — Informational notes, standard boilerplate clauses.
   - 🟡 `MEDIUM` — Ambiguous timelines, non-standard notice periods.
   - 🟠 `HIGH` — One-sided indemnification, broad non-competes.
   - 🔴 `CRITICAL` — Unlimited liability, unilateral termination without cause.
5. **Explainable Composite Risk Score:** Computes a normalized 0–100 risk index with explicit citations to source clauses.

---

## 📑 Software Requirements Specification (SRS)

The complete academic Software Requirements Specification has been authored and committed to this branch:

- 📕 **Official SRS PDF Document:** [`docs/Legal Document Analyser & Advisor - SRS.pdf`](./docs/Legal%20Document%20Analyser%20&%20Advisor%20-%20SRS.pdf)
- 📄 **Full Markdown Specification:** [`docs/Software_Requirements_Specification.md`](./docs/Software_Requirements_Specification.md)

### Key Functional Requirements Owned by this Module:
| Feature / Req ID | Specification Summary |
| :--- | :--- |
| **F4 / FR6** | **Document Classification:** Detect legal document category (NDA, Employment, Rental, Loan, Service Agreement). |
| **F6 / FR8** | **Clause Extraction:** Identify parties, key dates, obligations, liability limits, and governing jurisdiction. |
| **F7 / FR9** | **Risk Detection:** Flag unfair, ambiguous, or predatory clauses with assigned severity and plain-language explanation. |
| **F8 / FR10** | **Legal Term Explainer:** Identify complex legal jargon within context and provide simplified definitions. |
| **F10 / FR12** | **Actionable Suggestions:** Recommend negotiation points, missing protective clauses, and safety disclaimers. |

---

## 🏛️ Module Architecture & Data Model

### Suggested Package Layout (`dilip` module)
```
com.pbl.legal
├── ai/
│   ├── ChunkingService.java
│   └── PromptInjectionGuard.java
├── model/
│   ├── Analysis.java             # JPA Entity: summary, risk score, status
│   ├── Clause.java               # JPA Entity: type, raw text, page reference
│   ├── Risk.java                 # JPA Entity: severity, explanation, suggestion
│   └── enums/
│       ├── DocType.java          # NDA, EMPLOYMENT, RENTAL, LOAN, etc.
│       ├── Severity.java         # LOW, MEDIUM, HIGH, CRITICAL
│       └── AnalysisStatus.java   # PENDING, ANALYSING, COMPLETED, FAILED
├── repository/
│   ├── AnalysisRepository.java
│   ├── ClauseRepository.java
│   └── RiskRepository.java
├── service/
│   ├── ClassificationService.java
│   ├── AnalysisService.java
│   └── RiskService.java
└── controller/
    └── AnalysisController.java
```

### Core API Contracts
- `POST /api/analysis/{documentId}` — Execute legal clause parsing and risk scoring pipeline.
- `GET /api/analysis/{documentId}` — Retrieve complete analysis report (clauses, risk items, composite score).
- `GET /api/analysis/{documentId}/risks` — Query high and critical risk highlights.

---

## 🗓️ Dilip's 6-Day Development Deliverables

| Academic Phase | Sprint Day | Deliverable & Milestone |
| :--- | :---: | :--- |
| **Weeks 1–2** | **Day 1** | Schema design (`Analysis`, `Clause`, `Risk` JPA entities), SRS documentation, controller/service skeletons. |
| **Weeks 3–4** | **Day 2** | Regex and rule-based clause segmenter, severity weighting algorithm, unit tests with Mockito. |
| **Weeks 5–6** | **Day 3** | Integration with Document Processing payload, analysis persistence in PostgreSQL/H2. |
| **Weeks 7–8** | **Day 4** | Boundary & edge-case testing (unstructured contracts, zero-risk documents, extreme indemnification). |
| **Weeks 9–10** | **Day 5** | Accuracy benchmarking against standard NDA and SaaS contract datasets. |
| **Weeks 11–12** | **Day 6** | Final algorithmic defense, live demo walkthrough, and submission package. |

---

## 🚀 Local Development & Testing

### Build & Run Tests
```bash
# Clone and switch to Dilip's branch
git clone https://github.com/gouravnagori/Java-PBL.git
cd Java-PBL
git checkout dilip

# Run module unit tests
mvn test

# Package project JAR
mvn clean package -DskipTests
```

---

## ⚠️ Important Legal & Safety Notice
> **Disclaimer:** This tool provides automated textual analysis and risk heuristics for educational and productivity purposes. It does not constitute formal legal counsel. Users must verify all contractual terms with a licensed attorney.
