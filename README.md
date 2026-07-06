# ⚖️ Legal Document Analyser & Advisor
### 🌿 Feature Branch: `ayush-auth-case-management` — User, Authentication & Case Management Module
**Module Owner:** Ayush Rathore  
**Academic Program:** Advanced Java Project-Based Learning (PBL) — 5th Semester  

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![JWT](https://img.shields.io/badge/Security-Stateless_JWT-black?style=for-the-badge&logo=jsonwebtokens)](https://jwt.io/)
[![Database](https://img.shields.io/badge/Database-H2%20%7C%20PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)

---

## 🔗 Main Repository & Central Documentation Link

> 📌 **Central Repository Link:**  
> **👉 [gouravnagori/Java-PBL (Main Branch)](https://github.com/gouravnagori/Java-PBL/tree/main)**  
> 📄 **Main Architecture & Full Roadmap:** **[View main README.md](https://github.com/gouravnagori/Java-PBL/blob/main/README.md)**

### 📝 Module Ownership & Mission: Ayush Rathore
As part of the 5th-Semester Advanced Java PBL team, **Ayush Rathore** is responsible for the **User, Authentication & Case Management Module**. This module acts as the core security perimeter and organizing layer for the entire application:

1. **Enterprise Identity & Access Management:** User registration, credential authentication, and profile lifecycle with Spring Security 6.
2. **Stateless JWT Security Architecture:** HMAC-SHA256 token issuance, cryptographic verification, role claims, and custom filter interceptors.
3. **Multi-Tenant Case Management:** Grouping legal documents under case categories with strict ownership boundaries preventing cross-tenant data leakage.
4. **Defensive API Standards:** Global exception handling, standardized JSON error envelopes, and comprehensive JUnit 5/MockMvc security test suite.

---

## 📅 Ayush's 14-Week Development Progression (July 6, 2026 – October 6, 2026)

| Week | Date | Milestone & Deliverable | Status |
| :---: | :---: | :--- | :---: |
| **W01** | 2026-07-06 | Feature branch setup, Maven Spring Boot 3 configuration & module roadmap | ✅ Completed |
| **W02** | 2026-07-13 | User JPA entity, Role enum, and audit timestamps | 🔄 In Progress |
| **W03** | 2026-07-20 | Spring Data JPA UserRepository with custom query methods | ⏳ Planned |
| **W04** | 2026-07-27 | DTO layer for authentication requests/responses with validation constraints | ⏳ Planned |
| **W05** | 2026-08-03 | BCrypt password hashing & CustomUserDetailsService | ⏳ Planned |
| **W06** | 2026-08-10 | Stateless JwtTokenProvider implementation & claims extraction | ⏳ Planned |
| **W07** | 2026-08-17 | JwtAuthenticationFilter & JwtAuthenticationEntryPoint | ⏳ Planned |
| **W08** | 2026-08-24 | Complete Spring Security 6 filter chain, CORS & stateless session policy | ⏳ Planned |
| **W09** | 2026-08-31 | AuthService & AuthController REST endpoints (`/register`, `/login`, `/me`) | ⏳ Planned |
| **W10** | 2026-09-07 | LegalCase JPA entity, CaseCategory enum & LegalCaseRepository | ⏳ Planned |
| **W11** | 2026-09-14 | CaseService & CaseController with strict multi-tenant ownership isolation | ⏳ Planned |
| **W12** | 2026-09-21 | Automated JUnit 5, MockMvc & security penetration test suite | ⏳ Planned |
| **W13** | 2026-09-28 | Modern Auth & Case Management frontend UI with glassmorphic design | ⏳ Planned |
| **W14** | 2026-10-06 | Final IEEE Module Specification document, Swagger docs & viva sign-off | ⏳ Planned |
