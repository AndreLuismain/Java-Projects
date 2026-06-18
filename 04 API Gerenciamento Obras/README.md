# Distribution Works Management API

A robust RESTful API built with Java and Spring Boot for managing distribution works, energy concessionaires, and detailed cost itemization. 

This project serves as a structured foundation for processing and correlating infrastructural data, designed to ensure data consistency, clear separation of concerns, and high performance when querying aggregated costs.

## Tech Stack

* **Java 21:** Leveraging modern language features (like Records and pattern matching).
* **Spring Boot 3:** Rapid application development, dependency injection, and web layer management.
* **Spring Data JPA & Hibernate:** ORM for database interactions.
* **PostgreSQL:** Primary relational database, chosen for its reliability and extensibility for future hybrid data architectures (e.g., JSONB integration for dynamic geographic or accounting assets).
* **Flyway:** Database migration tool for version control of the database schema.
* **Docker:** Containerization of the database to guarantee environment parity and a frictionless development setup.
* **Lombok:** Boilerplate code reduction.

## Architecture & Technical Justifications

The application strictly follows a **Layered Architecture** (Controller, Service, Repository) combined with modern Java practices.

### 1. Separation of Concerns & Clean Controllers
Controllers are kept strictly focused on HTTP routing and delegating work. There are **no `try-catch` blocks** or business logic inside the controllers. This allows the `Service` layer to act as the pure core of the application, where Java Functional Programming (`Streams`, `map`, `filter`) can be applied to process data before saving or returning it.

### 2. DTO Pattern with Java Records
Internal database entities (`@Entity`) are completely isolated from the external API contract. We use **Java Records** for DTOs (Data Transfer Objects). 
* **Why?** Records provide built-in immutability, thread safety, and a much cleaner syntax. It prevents over-posting attacks and ensures the API only exposes or accepts exactly what is required.

### 3. Global Exception Handling (`@RestControllerAdvice`)
Instead of scattering error handling throughout the code, the API uses a centralized `TratadorDeErrosGlobais`. 
* **Why?** It ensures that the client always receives a standardized, predictable JSON error response (using a custom `ErroPadrao` record) regardless of where the application fails. It cleanly translates business exceptions (`IllegalArgumentException`) and validation errors (`@Valid`) into proper HTTP status codes (e.g., 400 Bad Request).

### 4. Database Versioning with Flyway
The database schema is not generated dynamically by Hibernate's `ddl-auto=update`. Instead, Flyway executes strict `.sql` migration scripts (`V1__criar_tabelas_iniciais.sql`).
* **Why?** This provides a reliable, reproducible, and trackable database history, which is mandatory for production environments and prevents state inconsistencies across different developers' machines.

### 5. Optimized Data Retrieval (Projections)
For reporting endpoints (e.g., getting total costs grouped by distributor), the API uses Spring Data JPA `@Query` combined with interface-based Projections.
* **Why?** Instead of fetching the entire heavy object graph (Distributor + all Works + all Cost Items) into memory and calculating it via Java, the calculation is pushed down to the PostgreSQL engine. The Projection only returns the exact columns needed (`nomeDistribuidora`, `custoTotal`), drastically reducing memory footprint and network latency.

## How to Run Locally

### Prerequisites
* JDK 21
* Docker & Docker Desktop

### Setup Instructions

1. **Clone the repository:**
```bash
   git clone [https://github.com/your-username/Java-Projects.git](https://github.com/your-username/Java-Projects.git)
   cd "04 API Gerenciamento Obras"
