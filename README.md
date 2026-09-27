# HDFC Life Policy Ledger

A Spring Boot REST API for managing HDFC Life customers, insurance policies, claims, and policy riders.

The application uses Spring Data JPA for persistence, Flyway for database schema management, H2 for development, PostgreSQL for production, Bean Validation for request validation, and SpringDoc OpenAPI for API documentation.

---

## 1. Project Overview

The HDFC Life Policy Ledger provides REST APIs to:

- Create and retrieve insurance policies
- Retrieve policies by policy number
- Filter policies by status
- Filter policies by product type
- Filter policies by customer
- Search policies by minimum premium
- Delete policies
- Create insurance claims
- Retrieve claims associated with a policy
- Manage policy-rider relationships
- Validate API requests
- Return uniform error responses
- Persist data using Spring Data JPA
- Manage database schema using Flyway
- Document and test APIs using Swagger/OpenAPI

The application does not use an `ArrayList`, `HashMap`, or other in-memory collection as the source of truth for policies. `JpaRepository` is used as the persistence layer.

---

# 2. Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3 | Application framework |
| Spring Web | REST API |
| Spring Data JPA | Database persistence |
| Hibernate | JPA implementation |
| Bean Validation | Request validation |
| Flyway | Database migrations |
| H2 | Development database |
| PostgreSQL | Production database |
| SpringDoc OpenAPI | Swagger/OpenAPI documentation |
| Maven | Build and dependency management |

---

# 3. Requirements

The project is designed according to the following requirements:

- Spring Boot 3
- Java 17
- Constructor injection only
- No field `@Autowired`
- JPA/Hibernate for persistence
- Flyway owns database tables
- `spring.jpa.hibernate.ddl-auto=none`
- `spring.jpa.open-in-view=false`
- Controllers return DTOs rather than JPA entities
- Controllers do not contain database queries
- Services contain business logic
- Repository interfaces contain persistence queries

---

# 4. Project Architecture

The application follows a layered architecture:

```text
                Client
                  |
                  v
        +--------------------+
        |    Controllers     |
        |      /web          |
        +--------------------+
                  |
                  v
        +--------------------+
        |      Services      |
        |     /service       |
        +--------------------+
                  |
                  v
        +--------------------+
        |    Repositories    |
        |       /repo        |
        +--------------------+
                  |
                  v
        +--------------------+
        |       JPA          |
        |     Hibernate      |
        +--------------------+
                  |
                  v
        +--------------------+
        |      Database      |
        |    H2/PostgreSQL   |
        +--------------------+
