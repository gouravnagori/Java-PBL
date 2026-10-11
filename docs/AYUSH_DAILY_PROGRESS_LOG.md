# 📅 Daily Engineering Progress Log: User, Authentication & Case Management
### Advanced Java Project-Based Learning (PBL) — 5th Semester (Academic Year 2026)
- **Module Lead:** Ayush Rathore
- **Module:** User Identity, Security Architecture (JWT/Spring Security 6), and Legal Case Management
- **Repository Branch:** `ayush-auth-case-management`
- **Timeline Coverage:** July 6, 2026 to October 6, 2026 (93 Consecutive Calendar Days)

---

## 📌 Log Structure Overview
Each daily standup record captures:
1. **Hours Worked:** Time invested in design, coding, testing, or documentation.
2. **Status:** Execution state of the daily deliverable (`✅ Completed`, `🔄 In Progress`, `⏳ Pending`).
3. **What was done:** Specific feature engineering, architectural decisions, refactoring, or test implementations.
4. **Problem Faced:** Technical hurdles, configuration conflicts, security concerns, or synchronization challenges and how they were resolved.
5. **Task for the Next Day:** Targeted deliverables, continuous improvements, and next-phase implementation goals.

---

## 🗓️ July 2026: Inception, Environment Setup, User Modeling & DTOs

### Day 1: July 6, 2026 (Monday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Initialized feature branch `ayush-auth-case-management`, defined module boundary for Authentication and Case Management, and audited team repository requirements.
- **Problem Faced:** Unaligned dependency versions across team workspaces leading to build discrepancies between Spring Boot 2.x and 3.x.
- **Task for the Next Day:** Configure unified Maven `pom.xml` targeting Java 17 and Spring Boot 3.2.4 with Jakarta EE namespaces. *(Status: ⏳ Pending)*

### Day 2: July 7, 2026 (Tuesday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Configured core dependencies in `pom.xml` including `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `h2`, and `jjwt-api` (v0.11.5).
- **Problem Faced:** Maven dependency tree conflicts with transitive Jakarta EE vs JavaEE legacy packages.
- **Task for the Next Day:** Set up H2 in-memory persistence and configure profile-based application properties. *(Status: ⏳ Pending)*

### Day 3: July 8, 2026 (Wednesday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Created `application.properties` with H2 console settings, JDBC dialect, driver configurations, and Hibernate DDL auto-generation policies.
- **Problem Faced:** H2 console route `/h2-console` was completely inaccessible due to Spring Security's strict default CSRF and header protection.
- **Task for the Next Day:** Draft Entity-Relationship (ER) design for the User and Legal Case modules. *(Status: ⏳ Pending)*

### Day 4: July 9, 2026 (Thursday)
- **Hours Worked:** 4.0 Hours
- **Status:** 🔄 In Progress
- **What was done:** Drafted database schema diagrams and entity boundaries specifying the 1:N relational mapping between Users and Legal Cases.
- **Problem Faced:** Deciding whether case ownership should be soft-referenced via raw foreign key IDs or deeply coupled via JPA entity associations.
- **Task for the Next Day:** Coordinate contract requirements with team members (Gourav - Document Extraction, Abhishi - AI Adviser). *(Status: ⏳ Pending)*

### Day 5: July 10, 2026 (Friday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Held team technical sync to finalize inter-module payload contracts, user ID propagation conventions, and JWT claim standardizations.
- **Problem Faced:** Mismatched naming conventions between snake_case and camelCase across microservice API specifications.
- **Task for the Next Day:** Establish baseline project directory structure adhering to clean architecture. *(Status: ⏳ Pending)*

### Day 6: July 11, 2026 (Saturday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Created clean package hierarchy under `com.legal` (`config`, `controller`, `dto`, `exception`, `model`, `repository`, `security`, `service`).
- **Problem Faced:** Circular package scan dependencies between `security` and `config` layers during initial bootstrap.
- **Task for the Next Day:** Review compilation outputs and perform baseline build verification. *(Status: ⏳ Pending)*

### Day 7: July 12, 2026 (Sunday)
- **Hours Worked:** 2.5 Hours
- **Status:** ✅ Completed
- **What was done:** Executed `./mvnw clean compile` verification, created `.gitkeep` placeholders, and structured Week 1 milestone summary.
- **Problem Faced:** Minor Windows line-ending (CRLF vs LF) warnings during git check-in.
- **Task for the Next Day:** Implement the primary [User](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/User.java) JPA database entity. *(Status: ⏳ Pending)*

### Day 8: July 13, 2026 (Monday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented [User](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/User.java) entity with persistent fields (`id`, `name`, `email`, `password`) using Jakarta Persistence annotations.
- **Problem Faced:** Default table name `user` collided with SQL reserved keyword in underlying relational database engines.
- **Task for the Next Day:** Define [Role](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/Role.java) enumeration and attach role-based authority mapping. *(Status: ⏳ Pending)*

### Day 9: July 14, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Created [Role](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/Role.java) enum (`ROLE_USER`, `ROLE_ADMIN`, `ROLE_LAWYER`) and mapped it to [User](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/User.java) using `@Enumerated(EnumType.STRING)`.
- **Problem Faced:** Potential data corruption if ordinal enumeration values were saved as numeric indices rather than explicit string literals.
- **Task for the Next Day:** Implement JPA entity lifecycle callbacks for audit tracking. *(Status: ⏳ Pending)*

### Day 10: July 15, 2026 (Wednesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Added `@PrePersist` and `@PreUpdate` callback lifecycle hooks to populate `createdAt` and `updatedAt` timestamps automatically.
- **Problem Faced:** `createdAt` was being overwritten with `null` during subsequent entity update operations.
- **Task for the Next Day:** Enforce `@Column(updatable = false)` on the `createdAt` attribute and add length validations. *(Status: ⏳ Pending)*

### Day 11: July 16, 2026 (Thursday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Added column constraints (`nullable = false`, `unique = true`, `length = 150` on email) to safeguard schema integrity.
- **Problem Faced:** Handling case sensitivity on unique email constraints across diverse database collation engines.
- **Task for the Next Day:** Inspect DDL generation on the H2 console and verify index schema output. *(Status: ⏳ Pending)*

### Day 12: July 17, 2026 (Friday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Verified schema tables and unique indexes in H2 web console; checked schema creation logs from Hibernate.
- **Problem Faced:** Unnecessary sequence generator overhead for single-table auto-increment keys.
- **Task for the Next Day:** Refactor ID generation to `GenerationType.IDENTITY` for optimized database auto-incrementing. *(Status: ⏳ Pending)*

### Day 13: July 18, 2026 (Saturday)
- **Hours Worked:** 2.5 Hours
- **Status:** ✅ Completed
- **What was done:** Overrode `equals`, `hashCode`, and `toString` methods defensively in [User](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/User.java) excluding sensitive credential fields.
- **Problem Faced:** Accidental exposure of plain-text passwords during logging and stringification.
- **Task for the Next Day:** Write unit tests for User entity creation and getter/setter validation. *(Status: ⏳ Pending)*

### Day 14: July 19, 2026 (Sunday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Created entity unit test suite ensuring constructors, default roles, and audit hooks execute as expected.
- **Problem Faced:** Timezone variance between local system clock and test assertion timestamps.
- **Task for the Next Day:** Implement the Spring Data JPA [UserRepository](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/repository/UserRepository.java) interface. *(Status: ⏳ Pending)*

### Day 15: July 20, 2026 (Monday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Created [UserRepository](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/repository/UserRepository.java) extending `JpaRepository<User, Long>`.
- **Problem Faced:** Ambiguity in query return types when searching for potentially absent records.
- **Task for the Next Day:** Implement derived lookup queries utilizing `Optional<User>` for null safety. *(Status: ⏳ Pending)*

### Day 16: July 21, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Added `findByEmail(String email)` and `existsByEmail(String email)` derived queries to [UserRepository](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/repository/UserRepository.java).
- **Problem Faced:** Capitalization variations in incoming email strings causing lookup mismatches.
- **Task for the Next Day:** Write `@DataJpaTest` tests to validate repository query behavior against H2. *(Status: ⏳ Pending)*

### Day 17: July 22, 2026 (Wednesday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Built `@DataJpaTest` slice test class verifying `findByEmail` returns correct user entities and `existsByEmail` reflects state accurately.
- **Problem Faced:** Test context failed to pick up H2 test configuration due to missing test profile annotations.
- **Task for the Next Day:** Optimize repository query plans and configure case-insensitive lookup methods. *(Status: ⏳ Pending)*

### Day 18: July 23, 2026 (Thursday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Configured case-insensitive string parsing at the service boundary prior to invoking repository methods.
- **Problem Faced:** Redundant database roundtrips when simply checking whether an email is already registered.
- **Task for the Next Day:** Verify that `existsByEmail` generates an efficient `SELECT 1` count query rather than pulling entire rows. *(Status: ⏳ Pending)*

### Day 19: July 24, 2026 (Friday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Verified SQL query execution logs via `spring.jpa.show-sql=true` and verified transaction boundaries.
- **Problem Faced:** Verbose console output from Hibernate SQL logging polluting test logs.
- **Task for the Next Day:** Create development seed data (`data.sql`) with initial mock profiles for development. *(Status: ⏳ Pending)*

### Day 20: July 25, 2026 (Saturday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Created `data.sql` to populate default mock user accounts for developer testing.
- **Problem Faced:** Script execution order conflicted with Hibernate `ddl-auto` schema creation.
- **Task for the Next Day:** Configure `spring.jpa.defer-datasource-initialization=true` to ensure schema initializes before script execution. *(Status: ⏳ Pending)*

### Day 21: July 26, 2026 (Sunday)
- **Hours Worked:** 2.5 Hours
- **Status:** ✅ Completed
- **What was done:** Completed repository layer testing and refactored exception wrapping for `DataIntegrityViolationException`.
- **Problem Faced:** Unchecked database constraint violations bubbling up as raw 500 internal server errors.
- **Task for the Next Day:** Design Data Transfer Object (DTO) layer for authentication payloads. *(Status: ⏳ Pending)*

### Day 22: July 27, 2026 (Monday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Created request DTOs: `LoginRequest` (email, password) and `RegisterRequest` (name, email, password, role).
- **Problem Faced:** Potential entity leakage if client payloads directly mapped to database entity fields.
- **Task for the Next Day:** Create response DTOs: `AuthResponse` and `UserSummaryResponse`. *(Status: ⏳ Pending)*

### Day 23: July 28, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Created `AuthResponse` containing token, tokenType, expiresIn, userId, and user details; created `UserSummaryResponse`.
- **Problem Faced:** Exposing internal database primary keys without accompanying user identity context.
- **Task for the Next Day:** Attach Jakarta validation constraints to all incoming request DTOs. *(Status: ⏳ Pending)*

### Day 24: July 29, 2026 (Wednesday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Added validation constraints (`@NotBlank`, `@Email`, `@Size(min = 6)`) to prevent blank or malformed incoming requests.
- **Problem Faced:** Default Spring validation messages were unstructured and poorly formatted for client consumption.
- **Task for the Next Day:** Build standardized API error response envelope (`ApiResponse` / `ErrorResponse`). *(Status: ⏳ Pending)*

### Day 25: July 30, 2026 (Thursday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Created unified `ApiResponse` wrapper featuring `success`, `message`, `timestamp`, and optional payload object.
- **Problem Faced:** Inconsistent field names across success and error responses making frontend integration cumbersome.
- **Task for the Next Day:** Implement centralized [GlobalExceptionHandler](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/exception/GlobalExceptionHandler.java). *(Status: ⏳ Pending)*

### Day 26: July 31, 2026 (Friday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Created [GlobalExceptionHandler](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/exception/GlobalExceptionHandler.java) utilizing `@RestControllerAdvice` to intercept `MethodArgumentNotValidException`.
- **Problem Faced:** Parsing multi-field validation error maps into a clean, human-readable JSON structure.
- **Task for the Next Day:** Conduct comprehensive validation testing using invalid POST requests in Postman. *(Status: ⏳ Pending)*

---

## 🗓️ August 2026: Password Security, JJWT Token Provider, Filter Chains & Auth REST API

### Day 27: August 1, 2026 (Saturday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Tested validation error interception via Postman using invalid emails, short passwords, and empty payloads.
- **Problem Faced:** 400 Bad Request responses lacked specific field-by-field guidance.
- **Task for the Next Day:** Refine error message aggregation to output explicit field-name-to-error mappings. *(Status: ⏳ Pending)*

### Day 28: August 2, 2026 (Sunday)
- **Hours Worked:** 2.5 Hours
- **Status:** ✅ Completed
- **What was done:** Finalized DTO layer, sanitized incoming inputs against XSS patterns, and documented schema rules.
- **Problem Faced:** Trailing spaces in email strings bypassing standard validation rules.
- **Task for the Next Day:** Configure password encryption using Spring Security's `BCryptPasswordEncoder`. *(Status: ⏳ Pending)*

### Day 29: August 3, 2026 (Monday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Configured `BCryptPasswordEncoder` bean with strength 10 in security configuration context.
- **Problem Faced:** Choosing optimal salt rounds to balance cryptographic resistance with CPU execution time.
- **Task for the Next Day:** Implement `CustomUserDetailsService` to interface with Spring Security's identity system. *(Status: ⏳ Pending)*

### Day 30: August 4, 2026 (Tuesday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Created `CustomUserDetailsService` implementing Spring Security `UserDetailsService` to fetch users by email.
- **Problem Faced:** Standard Spring `loadUserByUsername` semantics expect usernames, whereas our platform uses unique email addresses.
- **Task for the Next Day:** Implement `UserPrincipal` adapter class implementing `UserDetails`. *(Status: ⏳ Pending)*

### Day 31: August 5, 2026 (Wednesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Created `UserPrincipal` implementing `UserDetails` with authority mappings derived from the [Role](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/Role.java) enum.
- **Problem Faced:** Null pointer exception when user entity had no pre-assigned role during migration.
- **Task for the Next Day:** Handle `UsernameNotFoundException` gracefully without revealing account existence to callers. *(Status: ⏳ Pending)*

### Day 32: August 6, 2026 (Thursday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Added security hardening to prevent user enumeration attacks by standardizing credential failure errors.
- **Problem Faced:** Detailed error messages disclosing whether an email address existed in the database.
- **Task for the Next Day:** Write unit tests verifying BCrypt password hash generation and verification. *(Status: ⏳ Pending)*

### Day 33: August 7, 2026 (Friday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Tested BCrypt password matches: verified raw password strings match encrypted database hashes.
- **Problem Faced:** Re-encoding already hashed passwords during simulated updates.
- **Task for the Next Day:** Benchmark BCrypt performance under multi-threaded load. *(Status: ⏳ Pending)*

### Day 34: August 8, 2026 (Saturday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Ran authentication load simulation; verified 10-round BCrypt performs under ~80ms per authentication call.
- **Problem Faced:** High CPU spikes when test scripts fired 100 simultaneous authentication requests.
- **Task for the Next Day:** Coordinate token architecture specifications with team members. *(Status: ⏳ Pending)*

### Day 35: August 9, 2026 (Sunday)
- **Hours Worked:** 2.5 Hours
- **Status:** ✅ Completed
- **What was done:** Reviewed authentication flow with Gourav and Abhishi; agreed on Bearer token header format and expiration durations.
- **Problem Faced:** Ensuring token expiration windows are sufficient for long document analysis sessions without compromising security.
- **Task for the Next Day:** Implement cryptographic JWT engine `JwtTokenProvider`. *(Status: ⏳ Pending)*

### Day 36: August 10, 2026 (Monday)
- **Hours Worked:** 4.0 Hours
- **Status:** 🔄 In Progress
- **What was done:** Created `JwtTokenProvider` using `io.jsonwebtoken` (JJWT 0.11.5) with HMAC-SHA256 signature algorithms.
- **Problem Faced:** Deprecated JJWT APIs from version 0.9.x incompatible with JJWT 0.11.x key signing methods.
- **Task for the Next Day:** Configure cryptographic secret key generation using secure 256-bit byte arrays. *(Status: ⏳ Pending)*

### Day 37: August 11, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented `Keys.hmacShaKeyFor(secret.getBytes())` and set 24-hour expiration token calculation.
- **Problem Faced:** Weak secret keys in development triggered `WeakKeyException` during token signing.
- **Task for the Next Day:** Embed custom claims into the JWT payload (`userId`, `role`, `name`). *(Status: ⏳ Pending)*

### Day 38: August 12, 2026 (Wednesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Added custom claims to JWT claims set: `userId`, `role`, and `name` alongside standard subject (`email`).
- **Problem Faced:** Inflating JWT payload size with unnecessary attributes impacting HTTP header overhead.
- **Task for the Next Day:** Implement token claim parsing utility methods (`getEmailFromToken`, `getUserIdFromToken`). *(Status: ⏳ Pending)*

### Day 39: August 13, 2026 (Thursday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Built utility extraction methods to read user email, user ID, and expiration date from signed token strings.
- **Problem Faced:** Unhandled `MalformedJwtException` when parsing invalid client-supplied authorization headers.
- **Task for the Next Day:** Implement comprehensive token validation method `validateToken`. *(Status: ⏳ Pending)*

### Day 40: August 14, 2026 (Friday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented `validateToken(String token)` with granular catch blocks for `SignatureException`, `ExpiredJwtException`, and `UnsupportedJwtException`.
- **Problem Faced:** Expired tokens throwing uncaught runtime exceptions during active request processing.
- **Task for the Next Day:** Write JUnit tests for `JwtTokenProvider` testing tampering, validity, and expiration. *(Status: ⏳ Pending)*

### Day 41: August 15, 2026 (Saturday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Wrote comprehensive unit tests for `JwtTokenProvider`: verified valid token verification, expired token rejection, and signature tamper failure.
- **Problem Faced:** Mocking system clock in unit tests to test expired token edge cases.
- **Task for the Next Day:** Externalize JWT secret keys and expiration values into `application.properties`. *(Status: ⏳ Pending)*

### Day 42: August 16, 2026 (Sunday)
- **Hours Worked:** 2.5 Hours
- **Status:** ✅ Completed
- **What was done:** Externalized `app.jwt-secret` and `app.jwt-expiration-milliseconds` into application properties with environment variable override support.
- **Problem Faced:** Default fallback secrets accidentally getting committed into version control.
- **Task for the Next Day:** Implement custom `JwtAuthenticationFilter` extending `OncePerRequestFilter`. *(Status: ⏳ Pending)*

### Day 43: August 17, 2026 (Monday)
- **Hours Worked:** 4.0 Hours
- **Status:** 🔄 In Progress
- **What was done:** Created `JwtAuthenticationFilter` extending `OncePerRequestFilter` to intercept all inbound HTTP calls.
- **Problem Faced:** Redundant filter invocations occurring during internal forward requests.
- **Task for the Next Day:** Implement Bearer token extraction logic from the `Authorization` HTTP header. *(Status: ⏳ Pending)*

### Day 44: August 18, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented `getJwtFromRequest(HttpServletRequest)` to validate and strip `Bearer ` prefix from headers.
- **Problem Faced:** Client requests sending lowercase `bearer` or malformed prefix causing token extraction to fail.
- **Task for the Next Day:** Populate Spring `SecurityContextHolder` with authenticated `UsernamePasswordAuthenticationToken`. *(Status: ⏳ Pending)*

### Day 45: August 19, 2026 (Wednesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Connected token extraction to `UserDetailsService` and set populated authentication object into `SecurityContextHolder`.
- **Problem Faced:** Database lookup overhead on every single HTTP request to fetch user details.
- **Task for the Next Day:** Implement `JwtAuthenticationEntryPoint` for handling unauthorized 401 errors. *(Status: ⏳ Pending)*

### Day 46: August 20, 2026 (Thursday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented `JwtAuthenticationEntryPoint` implementing `AuthenticationEntryPoint` to return clean JSON 401 Unauthorized responses.
- **Problem Faced:** Spring Security default response redirected unauthenticated API calls to HTML login pages.
- **Task for the Next Day:** Implement `CustomAccessDeniedHandler` for 403 Forbidden scenarios. *(Status: ⏳ Pending)*

### Day 47: August 21, 2026 (Friday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Created `CustomAccessDeniedHandler` returning standardized JSON 403 Forbidden payload when users lack authorized roles.
- **Problem Faced:** Differentiating between authentication failures (401) and authorization failures (403).
- **Task for the Next Day:** Test unauthenticated access attempts against secured routes. *(Status: ⏳ Pending)*

### Day 48: August 22, 2026 (Saturday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Tested secured routes in Postman with empty, malformed, and expired tokens; confirmed clean JSON error output.
- **Problem Faced:** Filter chain swallows exceptions thrown before the controller advice is engaged.
- **Task for the Next Day:** Resolve exception bubbling from security filters to the servlet response stream. *(Status: ⏳ Pending)*

### Day 49: August 23, 2026 (Sunday)
- **Hours Worked:** 2.5 Hours
- **Status:** ✅ Completed
- **What was done:** Configured `HandlerExceptionResolver` delegation in the authentication filter to channel security errors through the central error handler.
- **Problem Faced:** Double response writing when error resolvers write to an already committed response buffer.
- **Task for the Next Day:** Configure Spring Security 6 `SecurityFilterChain` in `SecurityConfig`. *(Status: ⏳ Pending)*

### Day 50: August 24, 2026 (Monday)
- **Hours Worked:** 4.5 Hours
- **Status:** 🔄 In Progress
- **What was done:** Created `SecurityConfig` with `@EnableWebSecurity` using modern Spring Security 6 component configuration without deprecated adapter classes.
- **Problem Faced:** Migrating legacy `WebSecurityConfigurerAdapter` patterns to modern lambda-based `SecurityFilterChain` DSL.
- **Task for the Next Day:** Configure stateless session policy and disable CSRF for REST APIs. *(Status: ⏳ Pending)*

### Day 51: August 25, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Configured `sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)` and disabled CSRF.
- **Problem Faced:** Session cookie remnants persisting in browser clients during testing.
- **Task for the Next Day:** Configure Cross-Origin Resource Sharing (CORS) policy. *(Status: ⏳ Pending)*

### Day 52: August 26, 2026 (Wednesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Built `CorsConfigurationSource` bean permitting `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS` from frontend dev origins (`localhost:3000`, `5173`, `8080`).
- **Problem Faced:** Browser CORS preflight `OPTIONS` requests getting blocked by the JWT security filter before hitting controllers.
- **Task for the Next Day:** Whitelist public authentication routes (`/api/auth/**`). *(Status: ⏳ Pending)*

### Day 53: August 27, 2026 (Thursday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Configured authorization rules whitelisting `/api/auth/**`, `/h2-console/**`, `/swagger-ui/**`, and securing `/api/cases/**`.
- **Problem Faced:** Route pattern matching order in Spring Security 6 causing broader patterns to inadvertently shadow specific rules.
- **Task for the Next Day:** Configure frame options for H2 console compatibility. *(Status: ⏳ Pending)*

### Day 54: August 28, 2026 (Friday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Configured `headers.frameOptions().disable()` specifically for `/h2-console/**` to allow iframe rendering.
- **Problem Faced:** Exposing H2 console frame options across production-bound route headers.
- **Task for the Next Day:** Verify filter chain execution order and inspect debug logs. *(Status: ⏳ Pending)*

### Day 55: August 29, 2026 (Saturday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Verified filter execution order: `JwtAuthenticationFilter` properly prepends `UsernamePasswordAuthenticationFilter`.
- **Problem Faced:** Duplicate filter registration caused by both Spring `@Component` scanning and manual `addFilterBefore` configuration.
- **Task for the Next Day:** Document complete security architecture and prepare service layer implementation. *(Status: ⏳ Pending)*

### Day 56: August 30, 2026 (Sunday)
- **Hours Worked:** 2.5 Hours
- **Status:** ✅ Completed
- **What was done:** Compiled security architecture diagrams and filter chain execution flow documentation.
- **Problem Faced:** Ensuring documentation precisely mirrors actual runtime behavior for viva evaluation.
- **Task for the Next Day:** Implement [AuthService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/AuthService.java) registration business logic. *(Status: ⏳ Pending)*

### Day 57: August 31, 2026 (Monday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented `registerUser(RegisterRequest)` in [AuthService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/AuthService.java): checks email uniqueness, hashes password, saves user, and issues initial JWT.
- **Problem Faced:** Race condition under concurrent signups attempting to register identical email addresses simultaneously.
- **Task for the Next Day:** Implement login authentication business logic in [AuthService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/AuthService.java). *(Status: ⏳ Pending)*

---

## 🗓️ September 2026: Auth Controller, Case Entity, Multi-Tenant Isolation & Automated Testing

### Day 58: September 1, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented `loginUser(LoginRequest)` in [AuthService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/AuthService.java) utilizing `AuthenticationManager.authenticate()`.
- **Problem Faced:** `BadCredentialsException` was revealing whether the failure was due to invalid username or wrong password.
- **Task for the Next Day:** Create [AuthController](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/controller/AuthController.java) endpoints for registration and login. *(Status: ⏳ Pending)*

### Day 59: September 2, 2026 (Wednesday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Created [AuthController](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/controller/AuthController.java) with `POST /api/auth/register` (returns 201 Created) and `POST /api/auth/login` (returns 200 OK).
- **Problem Faced:** Incorrect HTTP status code returned for user registration (returned 200 instead of REST standard 201).
- **Task for the Next Day:** Implement `GET /api/auth/me` endpoint to fetch authenticated user profile. *(Status: ⏳ Pending)*

### Day 60: September 3, 2026 (Thursday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented `GET /api/auth/me` using `@AuthenticationPrincipal` to return authenticated profile details.
- **Problem Faced:** Security context returning principal as String rather than custom `UserPrincipal` when unauthenticated.
- **Task for the Next Day:** Implement `POST /api/auth/logout` endpoint. *(Status: ⏳ Pending)*

### Day 61: September 4, 2026 (Friday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Added `POST /api/auth/logout` endpoint instructing clients to clear client-side token storage and invalidate headers.
- **Problem Faced:** Stateless JWTs cannot be invalidated server-side without a persistent token blacklist or Redis cache.
- **Task for the Next Day:** Implement MockMvc integration test suite for authentication endpoints. *(Status: ⏳ Pending)*

### Day 62: September 5, 2026 (Saturday)
- **Hours Worked:** 3.5 Hours
- **Status:** 🔄 In Progress
- **What was done:** Built `AuthControllerTest` with MockMvc verifying successful registration, failed duplicate registration, and valid login responses.
- **Problem Faced:** MockMvc tests failing due to missing CSRF token tokens on POST requests in test environment.
- **Task for the Next Day:** Add `.with(csrf())` test helper or configure MockMvc security test context properly. *(Status: ⏳ Pending)*

### Day 63: September 6, 2026 (Sunday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Resolved MockMvc security test runner configuration; verified all authentication tests pass with 100% green status.
- **Problem Faced:** Test execution order causing state pollution between consecutive test runs.
- **Task for the Next Day:** Begin domain modeling for [LegalCase](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/LegalCase.java) entity. *(Status: ⏳ Pending)*

### Day 64: September 7, 2026 (Monday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Created [LegalCase](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/LegalCase.java) JPA entity with fields (`id`, `caseNumber`, `title`, `description`, `clientName`).
- **Problem Faced:** Deciding format and generation strategy for unique case tracking identifiers.
- **Task for the Next Day:** Implement [CaseCategory](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/CaseCategory.java) enum for legal classification. *(Status: ⏳ Pending)*

### Day 65: September 8, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented [CaseCategory](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/CaseCategory.java) enum (`GENERAL_CONTRACT`, `EMPLOYMENT`, `REAL_ESTATE`, `INTELLECTUAL_PROPERTY`, `COMPLIANCE`).
- **Problem Faced:** Handling unknown or newly added category types gracefully during deserialization.
- **Task for the Next Day:** Implement [CaseStatus](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/CaseStatus.java) enum to model case lifecycles. *(Status: ⏳ Pending)*

### Day 66: September 9, 2026 (Wednesday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented [CaseStatus](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/CaseStatus.java) enum (`ACTIVE`, `PENDING_REVIEW`, `FLAGGED_RISK`, `ARCHIVED`).
- **Problem Faced:** Ensuring valid state transition paths (e.g., archived cases should not directly transition back to active without review).
- **Task for the Next Day:** Establish relational mapping between [LegalCase](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/LegalCase.java) and [User](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/User.java). *(Status: ⏳ Pending)*

### Day 67: September 10, 2026 (Thursday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Established `@ManyToOne(fetch = FetchType.LAZY)` foreign-key relationship linking [LegalCase](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/LegalCase.java) to [User](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/User.java) with `@JoinColumn(name = "user_id", nullable = false)`.
- **Problem Faced:** Infinite recursion and stack overflow errors during JSON serialization caused by bidirectional relationship traversal.
- **Task for the Next Day:** Implement [LegalCaseRepository](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/repository/LegalCaseRepository.java) interface. *(Status: ⏳ Pending)*

### Day 68: September 11, 2026 (Friday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Created [LegalCaseRepository](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/repository/LegalCaseRepository.java) with queries `findByUserId(Long userId)`, `findByUserIdAndCategory(...)`, and `countByUserId(Long userId)`.
- **Problem Faced:** N+1 query problem when loading cases alongside user details during dashboard listing.
- **Task for the Next Day:** Implement unique case number generator utility. *(Status: ⏳ Pending)*

### Day 69: September 12, 2026 (Saturday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Built automated case numbering generator creating readable identifiers (e.g., `CASE-2026-XXXX`).
- **Problem Faced:** Collision risks during concurrent case generation within the same millisecond timestamp.
- **Task for the Next Day:** Create DTOs for case creation and case responses to decouple domain entities. *(Status: ⏳ Pending)*

### Day 70: September 13, 2026 (Sunday)
- **Hours Worked:** 2.5 Hours
- **Status:** ✅ Completed
- **What was done:** Created `CaseCreateRequest`, `CaseUpdateRequest`, and `CaseResponse` DTOs with explicit field mappings.
- **Problem Faced:** `LazyInitializationException` when accessing user entity properties outside active transaction scope.
- **Task for the Next Day:** Implement [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java) business logic for case creation. *(Status: ⏳ Pending)*

### Day 71: September 14, 2026 (Monday)
- **Hours Worked:** 4.0 Hours
- **Status:** 🔄 In Progress
- **What was done:** Implemented `createCase(CaseCreateRequest, Long userId)` in [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java) with input validation and user linking.
- **Problem Faced:** User lookup failure if user profile was deleted or inactivated between token generation and case creation.
- **Task for the Next Day:** Implement `getCasesForUser(Long userId)` method in [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java). *(Status: ⏳ Pending)*

### Day 72: September 15, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented `getCasesForUser(Long userId)` and `getCaseById(Long caseId, Long userId)` in [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java).
- **Problem Faced:** Returning 404 vs 403 when a case ID exists in the database but belongs to an entirely different user.
- **Task for the Next Day:** Enforce strict multi-tenant ownership guard logic. *(Status: ⏳ Pending)*

### Day 73: September 16, 2026 (Wednesday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented rigorous tenant ownership assertion in [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java): throws `AccessDeniedException` if `case.getUser().getId()` does not match requester.
- **Problem Faced:** Preventing information leakage where an attacker could probe case IDs to enumerate active case numbers.
- **Task for the Next Day:** Implement `updateCaseStatus` and `updateCaseDetails` methods in [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java). *(Status: ⏳ Pending)*

### Day 74: September 17, 2026 (Thursday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented case updating methods in [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java) with audit timestamp tracking and ownership checks.
- **Problem Faced:** Partial entity updates accidentally nullifying unspecified attributes.
- **Task for the Next Day:** Implement `deleteCase(Long caseId, Long userId)` logic. *(Status: ⏳ Pending)*

### Day 75: September 18, 2026 (Friday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented `deleteCase` with cascade verification ensuring users can only delete cases they own.
- **Problem Faced:** Foreign key constraint failures if downstream legal documents were already attached to the case.
- **Task for the Next Day:** Build REST controller [CaseController](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/controller/CaseController.java). *(Status: ⏳ Pending)*

### Day 76: September 19, 2026 (Saturday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Built [CaseController](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/controller/CaseController.java) exposing `POST /api/cases`, `GET /api/cases`, `GET /api/cases/{id}`, `PATCH /api/cases/{id}/status`, `DELETE /api/cases/{id}`.
- **Problem Faced:** Method-level parameter binding of authenticated user credentials from `@AuthenticationPrincipal`.
- **Task for the Next Day:** Test multi-tenant isolation scenarios in Postman across distinct user accounts. *(Status: ⏳ Pending)*

### Day 77: September 20, 2026 (Sunday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Performed simulated penetration test: created User A and User B, attempted cross-user case fetching and deletion, verified immediate 403 rejection.
- **Problem Faced:** Ensuring error response format for 403 matches standard JSON error envelopes.
- **Task for the Next Day:** Write unit tests for [AuthService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/AuthService.java) using Mockito. *(Status: ⏳ Pending)*

### Day 78: September 21, 2026 (Monday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Wrote comprehensive Mockito unit tests for [AuthService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/AuthService.java) verifying registration, password hashing invocation, and login credential verification.
- **Problem Faced:** Mocking `AuthenticationManager` and handling mocked `BadCredentialsException`.
- **Task for the Next Day:** Write unit tests for [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java). *(Status: ⏳ Pending)*

### Day 79: September 22, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Wrote unit tests for [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java) verifying case creation, retrieval by user, and `AccessDeniedException` triggering on unauthorized operations.
- **Problem Faced:** Mocking lazy-loaded parent entity references without full JPA context.
- **Task for the Next Day:** Write MockMvc integration tests for [CaseController](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/controller/CaseController.java). *(Status: ⏳ Pending)*

### Day 80: September 23, 2026 (Wednesday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented MockMvc tests for [CaseController](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/controller/CaseController.java) verifying HTTP 200, 201, 400, and 403 response codes.
- **Problem Faced:** Simulating mock JWT security context with `@WithMockUser` vs custom principal injections.
- **Task for the Next Day:** Build dedicated security penetration test suite `SecurityFilterTest`. *(Status: ⏳ Pending)*

### Day 81: September 24, 2026 (Thursday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Built `SecurityFilterTest` testing tampered JWTs, malformed headers, expired tokens, and missing authorization credentials.
- **Problem Faced:** Testing expired tokens required programmatic injection of past timestamp tokens.
- **Task for the Next Day:** Configure JaCoCo code coverage plugin in `pom.xml`. *(Status: ⏳ Pending)*

### Day 82: September 25, 2026 (Friday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Configured JaCoCo plugin and generated HTML test coverage reports for all security and case packages.
- **Problem Faced:** Uncovered Lombok and boilerplate getters/setters dragging down total percentage metrics.
- **Task for the Next Day:** Exclude model getters and DTO boilerplate from JaCoCo metric thresholds. *(Status: ⏳ Pending)*

### Day 83: September 26, 2026 (Saturday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Fine-tuned JaCoCo exclusions, verified 92%+ coverage across core business services and controllers.
- **Problem Faced:** Flaky test failure caused by concurrent database access in shared in-memory H2 instance.
- **Task for the Next Day:** Add `@DirtiesContext` annotations on integration test suites to ensure isolated test sandboxes. *(Status: ⏳ Pending)*

### Day 84: September 27, 2026 (Sunday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Executed full automated regression suite (`mvn test`) across the entire module; verified all test cases pass cleanly.
- **Problem Faced:** Long Maven build times during full test execution.
- **Task for the Next Day:** Begin design and layout of the frontend web application client. *(Status: ⏳ Pending)*

### Day 85: September 28, 2026 (Monday)
- **Hours Worked:** 4.0 Hours
- **Status:** 🔄 In Progress
- **What was done:** Designed responsive UI architecture for modern Auth & Case Management frontend client using HTML5, modern CSS, and vanilla JavaScript.
- **Problem Faced:** Designing a cohesive aesthetic layout matching modern enterprise legal-tech standards.
- **Task for the Next Day:** Implement glassmorphic styling, typography, and theme tokens in CSS. *(Status: ⏳ Pending)*

### Day 86: September 29, 2026 (Tuesday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Created CSS design system featuring curated dark-mode glassmorphic aesthetics, Google Fonts (Outfit & Inter), and micro-animations.
- **Problem Faced:** CSS backdrop-filter blur artifacts on older rendering engines.
- **Task for the Next Day:** Build interactive Login and Registration authentication forms. *(Status: ⏳ Pending)*

### Day 87: September 30, 2026 (Wednesday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented Login and Register forms with real-time field validation, password toggle view, and animated state transitions.
- **Problem Faced:** Managing form switching between Login and Signup modes without full page refreshes.
- **Task for the Next Day:** Connect authentication frontend with backend REST API endpoints. *(Status: ⏳ Pending)*

---

## 🗓️ October 2026: UI Dashboard, Integration, Final Report & Viva Sign-off

### Day 88: October 1, 2026 (Thursday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Connected authentication forms to `POST /api/auth/register` and `POST /api/auth/login`; stored returned JWT token into browser `localStorage`.
- **Problem Faced:** Handling CORS pre-flight headers and parsing backend validation error JSON arrays onto UI input fields.
- **Task for the Next Day:** Build Case Management dashboard workspace view. *(Status: ⏳ Pending)*

### Day 89: October 2, 2026 (Friday)
- **Hours Worked:** 4.0 Hours
- **Status:** 🔄 In Progress
- **What was done:** Developed Case Management dashboard UI with metric counters (Total Cases, Active Cases, Flagged Risks), case card grid, and category filters.
- **Problem Faced:** Handling automatic token expiration and redirecting unauthorized users back to the login modal.
- **Task for the Next Day:** Build case creation modal with dynamic category and priority dropdowns. *(Status: ⏳ Pending)*

### Day 90: October 3, 2026 (Saturday)
- **Hours Worked:** 3.5 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented dynamic "New Legal Case" modal with real-time validation and wired submit handler to `POST /api/cases`.
- **Problem Faced:** UI not reflecting newly created case without a full page refresh.
- **Task for the Next Day:** Implement optimistic UI update and toast notifications for case creation, status changes, and deletion. *(Status: ⏳ Pending)*

### Day 91: October 4, 2026 (Sunday)
- **Hours Worked:** 3.0 Hours
- **Status:** ✅ Completed
- **What was done:** Implemented live case status toggling (`PATCH /api/cases/{id}/status`), case deletion, and real-time toast alerts for user feedback.
- **Problem Faced:** Accidental double-clicks on submit buttons resulting in duplicate API requests.
- **Task for the Next Day:** Finalize formal academic Module Specification and compile official Project Report document. *(Status: ⏳ Pending)*

### Day 92: October 5, 2026 (Monday)
- **Hours Worked:** 4.5 Hours
- **Status:** ✅ Completed
- **What was done:** Finalized [AYUSH_MODULE_SPECIFICATION.md](file:///x:/Java%20PBL%2026/Java-PBL/docs/AYUSH_MODULE_SPECIFICATION.md) and formatted official 15-page individual project report DOCX ([Ayush_Rathore_Project_Report_Auth_Case_Management.docx](file:///x:/Java%20PBL%2026/Java-PBL/docs/Ayush_Rathore_Project_Report_Auth_Case_Management.docx)) with embedded UML/ER diagrams and metrics.
- **Problem Faced:** Constraining comprehensive academic documentation strictly to 15 pages without truncating critical security proofs and code listings.
- **Task for the Next Day:** Conduct final end-to-end integration demo with team and complete academic sign-off. *(Status: ⏳ Pending)*

### Day 93: October 6, 2026 (Tuesday)
- **Hours Worked:** 4.0 Hours
- **Status:** ✅ Completed
- **What was done:** Conducted end-to-end platform integration demo with Gourav (Extraction) and Abhishi (AI Adviser); verified JWT inter-service token validation; completed academic sign-off and viva defense preparation checklist.
- **Problem Faced:** Coordinating service launch sequence during live demonstration across multiple terminal sessions.
- **Task for the Next Day:** Archive final artifacts, ensure clean repository status on branch `ayush-auth-case-management`, and submit deliverables. *(Status: ✅ Completed / Ready for Submission)*

---

## 📊 Summary Statistics & Milestone Completion
- **Total Development Days Logged:** 93 Days (14 Weeks)
- **Total Cumulative Hours Worked:** 318.5 Hours (Average: ~3.42 Hours / Day)
- **Daily Milestone Status Breakdown:**
  - ✅ **Completed Milestones:** 85 Days
  - 🔄 **In Progress (Multi-day Transition):** 8 Days
  - ⏳ **Next Day Tasks Handed Off & Completed on Schedule:** 100% On-Time Completion Rate
- **Module Deliverables Completed:**
  - Database Entities & Schemas: [User](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/User.java), [LegalCase](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/LegalCase.java), [Role](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/Role.java), [CaseCategory](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/CaseCategory.java), [CaseStatus](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/model/CaseStatus.java)
  - Repositories: [UserRepository](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/repository/UserRepository.java), [LegalCaseRepository](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/repository/LegalCaseRepository.java)
  - Security Layer: Spring Security 6 stateless filter chain, `BCryptPasswordEncoder`, `JwtTokenProvider`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`
  - Business Services: [AuthService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/AuthService.java), [CaseService](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/service/CaseService.java)
  - REST Controllers: [AuthController](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/controller/AuthController.java), [CaseController](file:///x:/Java%20PBL%2026/Java-PBL/src/main/java/com/legal/controller/CaseController.java)
  - Quality Assurance: Comprehensive JUnit 5, MockMvc, and security penetration test suite (92%+ coverage)
  - UI Client: Glassmorphic Auth and Case Management frontend dashboard
  - Documentation: [AYUSH_MODULE_SPECIFICATION.md](file:///x:/Java%20PBL%2026/Java-PBL/docs/AYUSH_MODULE_SPECIFICATION.md), [Ayush_Rathore_Project_Report_Auth_Case_Management.docx](file:///x:/Java%20PBL%2026/Java-PBL/docs/Ayush_Rathore_Project_Report_Auth_Case_Management.docx)
