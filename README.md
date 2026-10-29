# Simple Task Manager - Backend API

A production-grade, RESTful Task Management service built with **Spring Boot 3.4.2** and **Java 21 (LTS)**. The service implements a clean layered architecture, declarative validation, comprehensive global exception handling, pagination with RFC-compliant DTO serialization, embedded database persistence, and a complete JUnit 5 test suite.

---

## Technical Stack & Libraries

| Category | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java (OpenJDK Temurin) | `21` (LTS) | Core application runtime & modern language features |
| **Framework** | Spring Boot | `3.4.2` | Core application framework & dependency management |
| **Build Tool** | Gradle | `8.x+` | Dependency management & reproducible builds via Gradle Wrapper |
| **Persistence** | Spring Data JPA / Hibernate | Bundled | Object-Relational Mapping & repository abstractions |
| **Database** | H2 Database Engine | `2.3.232` | In-memory relational database for rapid execution & testing |
| **Validation** | Jakarta Bean Validation | Bundled | Declarative request payload validation (`@NotBlank`, `@Size`) |
| **API Docs** | SpringDoc OpenAPI (Swagger 3) | `2.8.5` | Interactive API documentation & OpenAPI v3 specs |
| **Monitoring** | Spring Boot Actuator | Bundled | Health checks, metrics, and application readiness probes |
| **Boilerplate** | Project Lombok | Managed | Code generation (`@Data`, `@Builder`, `@RequiredArgsConstructor`) |
| **Logging** | SLF4J / Logback | Bundled | Structured application logging with contextual traceability |
| **Testing** | JUnit 5, Mockito, MockMvc, AssertJ | Managed | Unit testing, WebMvc slice testing, and repository verification |

---

## Architectural & Design Highlights

1. **Layered Architecture**: Clear separation of concerns:
   - **Controller Layer**: Handles HTTP requests, enforces request validation, and routes to service interfaces.
   - **Service Layer**: Manages business logic with declarative transactional boundaries (`@Transactional`). Read operations use `@Transactional(readOnly = true)` to optimize Hibernate dirty checking and connection lifecycles.
   - **Repository Layer**: Extends `JpaRepository` for data access and paginated queries.
   - **DTO Pattern**: Decouples API client contracts (`TaskRequest`, `TaskResponse`) from internal persistence entities (`Task`).

2. **Global Exception Handling & Error Standardization**:
   - Implemented via `@RestControllerAdvice` (`GlobalExceptionHandler`).
   - Translates validation failures into `400 Bad Request` maps showing exact field violations.
   - Handles `NoResourceFoundException` for clean `404 Not Found` messages.
   - Catch-all handler for unhandled exceptions returning `500 Internal Server Error`, masking internal stack traces from clients while logging them securely on the server via SLF4J.

3. **Production Configuration Decisions**:
   - **OSIV Disabled (`spring.jpa.open-in-view=false`)**: Prevents database connection pool exhaustion and avoids the anti-pattern of query execution during JSON rendering.
   - **Stable Page Serialization (`VIA_DTO`)**: Configured globally with `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` to ensure stable, forward-compatible pagination JSON payloads.
   - **Cross-Origin Resource Sharing (CORS)**: Configured in `WebConfig` using `allowedOriginPatterns` to safely support credentials (`allowCredentials(true)`) without violating the W3C wildcard origin restriction.

---

## Project Structure

```text
simple-task-manager-backend/
├── src/
│   ├── main/
│   │   ├── java/com/mukesh/taskmanager/
│   │   │   ├── TaskManagerBackendApplication.java # Application entry point & @EnableSpringDataWebSupport
│   │   │   ├── config/
│   │   │   │   └── WebConfig.java                 # Global CORS configuration
│   │   │   ├── controller/
│   │   │   │   └── TaskController.java            # REST controller endpoints
│   │   │   ├── dto/
│   │   │   │   ├── TaskRequest.java               # Incoming validated payload
│   │   │   │   └── TaskResponse.java              # Outgoing data transfer object
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java    # Centralized @RestControllerAdvice
│   │   │   ├── model/
│   │   │   │   └── Task.java                      # JPA Entity
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java            # Spring Data JPA Repository
│   │   │   └── service/
│   │   │       ├── TaskService.java               # Service contract
│   │   │       └── impl/
│   │   │           └── TaskServiceImpl.java       # Transactional business logic
│   │   └── resources/
│   │       └── application.properties             # Environment & persistence settings
│   └── test/
│       └── java/com/mukesh/taskmanager/
│           ├── controller/
│           │   └── TaskControllerTest.java        # WebMvc slice testing with MockMvc & Mockito
│           ├── repository/
│           │   └── TaskRepositoryTest.java        # DataJpa slice tests with H2
│           └── service/
│               └── TaskServiceImplTest.java       # Isolated unit tests with Mockito
├── build.gradle                                   # Gradle configuration & dependencies
├── settings.gradle
└── README.md
```

---

## Configuration (`application.properties`)

```properties
# Server
server.port=8080

# Application
spring.application.name=simple-task-manager-backend

# Database (H2 In-Memory)
spring.datasource.url=jdbc:h2:mem:taskdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

# Actuator Endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

# Swagger / OpenAPI Path
springdoc.swagger-ui.path=/swagger-ui.html
```

---

## Build and Run

### Prerequisites
- **JDK 21** installed and configured (`java -version`)
- Terminal shell (macOS/Linux/Windows)

### Compile and Run Application
```bash
# Clone the repository
git clone https://github.com/mukeshkrishnan666/simple-task-manager-backend.git
cd simple-task-manager-backend

# Start the Spring Boot application
./gradlew bootRun
```
The application will launch on `http://localhost:8080`.

### Run Test Suite
```bash
./gradlew clean test
```
To generate and view the detailed HTML test execution report:
```bash
open build/reports/tests/test/index.html
```

---

## API Endpoints & Contracts

### 1. Create a Task
- **URL**: `POST /api/tasks`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
  ```json
  {
    "title": "Implement authentication",
    "description": "Configure Spring Security with JWT tokens"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "id": 1,
    "title": "Implement authentication",
    "description": "Configure Spring Security with JWT tokens",
    "createdAt": "2026-10-29T13:10:00"
  }
  ```
- **Error Response (Validation Failure)**: `400 Bad Request`
  ```json
  {
    "title": "Title is required and cannot be empty"
  }
  ```

### 2. Get All Tasks (Paginated)
- **URL**: `GET /api/tasks?page=0&size=10&sort=createdAt,desc`
- **Response**: `200 OK`
  ```json
  {
    "content": [
      {
        "id": 1,
        "title": "Implement authentication",
        "description": "Configure Spring Security with JWT tokens",
        "createdAt": "2026-10-29T13:10:00"
      }
    ],
    "page": {
      "size": 10,
      "number": 0,
      "totalElements": 1,
      "totalPages": 1
    }
  }
  ```

---

## Interactive Portals & Utilities

| Portal | URL | Credentials / Notes |
| :--- | :--- | :--- |
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interactive OpenAPI 3 explorer |
| **OpenAPI Docs** | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) | Raw OpenAPI v3 JSON spec |
| **H2 Web Console** | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) | **JDBC URL**: `jdbc:h2:mem:taskdb`<br>**User**: `sa`<br>**Password**: *(empty)* |
| **Health Check** | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | Returns application health status |
