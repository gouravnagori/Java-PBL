# 🛡️ Module Engineering Specification: User, Authentication & Case Management
### Advanced Java Project-Based Learning (PBL) — 5th Semester
**Module Lead:** Ayush Rathore  
**Academic Year:** 2026  
**Repository Branch:** `ayush-auth-case-management`  

---

## 1. Executive Summary & Module Purpose

The **User, Authentication & Case Management Module** serves as the principal security gatekeeper, identity authority, and organizational foundation for the **Legal Document Analyser & Advisor** platform.

The module solves three primary architectural requirements in enterprise software:
1. **Stateless Security Perimeter:** Secure user signup, credential hashing with BCrypt, and JSON Web Token (JWT) generation.
2. **Multi-Tenant Data Isolation:** Enforcing absolute data separation such that users can only view, mutate, or associate documents with legal cases they own.
3. **Legal Case Folder Hierarchies:** Structuring legal contracts and clause evaluations under persistent legal case records categorized by legal domain (e.g., NDA Compliance, Real Estate Leases, Employment Agreements).

---

## 2. Technical Stack & Standards

| Component | Standard / Technology | Justification |
| :--- | :--- | :--- |
| **Language** | Java 17 LTS | Enterprise language standard adhering to academic PBL curriculum. |
| **Framework** | Spring Boot 3.2.4 | Modern Spring Boot release with native Jakarta EE namespace support. |
| **Security Layer** | Spring Security 6.x | Filter chain model with method-level authorization (`@EnableMethodSecurity`). |
| **Token Specification** | JJWT (io.jsonwebtoken 0.11.5) | Cryptographically signed HMAC-SHA256 stateless tokens. |
| **Persistence Engine** | Spring Data JPA / Hibernate | High-productivity ORM mapping with transactional boundary guarantees. |
| **Relational Storage** | H2 (Dev) / PostgreSQL (Prod) | In-memory zero-configuration testing and production-ready PostgreSQL compatibility. |
| **Testing** | JUnit 5 & MockMvc | Slice testing of HTTP endpoints, token validation, and service isolation guards. |

---

## 3. Cryptographic & Security Architecture

### 3.1 Password Cryptography (BCrypt)
User credentials are protected using `BCryptPasswordEncoder` utilizing a secure 10-round salted hash. Passwords are never persisted or logged in plain text.

### 3.2 Stateless JWT Lifecycle
```
[Client] ─── POST /api/auth/login ───> [AuthController]
                                              │
                                              ▼
                                       [AuthService]
                                              │ Authenticate credentials
                                              ▼
                                    [AuthenticationManager]
                                              │
                                              ▼
                                    [JwtTokenProvider]
                                              │ Issue HMAC-SHA256 Token
[Client] <─── JSON { token, expiresIn } ──────┘
```

1. **Token Composition:**
   - **Header:** Algorithm (`HS256`), Type (`JWT`).
   - **Payload Claims:** Subject (`user.email`), `userId`, `name`, `issuedAt`, `expiration`.
   - **Signature:** 256-bit secret key hash preventing tampering.
2. **Per-Request Verification:**
   - Intercepted by `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`.
   - Validated against token tampering, expiry, and signature corruption.
   - Sets populated `UserPrincipal` into Spring's `SecurityContextHolder`.

---

## 4. Multi-Tenant Data Isolation Architecture

To prevent cross-tenant data leaks (OWASP API3: Broken Object Level Authorization):
- Every `LegalCase` entity possesses a strict relational foreign key (`@ManyToOne`) referencing `User`.
- `CaseService` executes ownership assertion prior to returning, updating, or deleting any case:
```java
if (!legalCase.getUser().getId().equals(user.getId())) {
    throw new AccessDeniedException("Unauthorized: You do not own case #" + caseId);
}
```

---

## 5. REST API Specifications

### 5.1 Authentication Endpoints
- `POST /api/auth/register` — Register a new account and receive initial JWT.
- `POST /api/auth/login` — Authenticate credentials and receive JWT.
- `GET /api/auth/me` — Retrieve current authenticated user profile.
- `POST /api/auth/logout` — Clear client authentication context.

### 5.2 Legal Case Management Endpoints
- `POST /api/cases` — Create a new legal case folder.
- `GET /api/cases` — List all cases owned by the authenticated user.
- `GET /api/cases/{id}` — Fetch case details with ownership check.
- `PATCH /api/cases/{id}/status` — Update case workflow state (`ACTIVE`, `PENDING_REVIEW`, `FLAGGED_RISK`, `ARCHIVED`).
- `DELETE /api/cases/{id}` — Soft/hard delete user-owned case.

---

## 6. Verification & Test Evidence

The module features comprehensive unit and integration tests:
- `JwtTokenProviderTest`: Validates HMAC-SHA256 generation, subject retrieval, and expired token rejection.
- `AuthControllerTest`: MockMvc integration testing validating registration HTTP 201 response and login 200 response with valid Bearer token.
- `CaseServiceTest`: Unit testing verifying successful case creation and verifying that cross-tenant access triggers `AccessDeniedException`.

---

## 7. Viva & Academic Defense Q&A

**Q1: Why choose stateless JWT over HTTP Sessions for this PBL architecture?**  
*Ayush's Answer:* Stateless JWT allows horizontally scalable microservices. Other team modules (such as Gourav's extraction service or Abhishi's AI advisor) can authenticate incoming requests independently using the signed token without requiring shared session replication or sticky sessions.

**Q2: How does your module prevent cross-user document access?**  
*Ayush's Answer:* Every document and case entity enforces a strict foreign key mapping to the User entity. The service layer extracts the authenticated principal directly from the validated JWT and rejects any request where the target resource ID does not belong to that user ID with an `AccessDeniedException`.
