# Java Assessment

## 1. Project Overview

This project is a Spring Boot REST API application developed as part of a Java technical assessment. It provides APIs for managing customers, products, and sales records.

The application uses a relational database for data persistence and includes product data file upload, sales report generation in PDF format, request validation, exception handling, and application logging.

## 2. Technology Stack

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- MySQL
- Maven
- JUnit and Mockito
- JasperReports
- Swagger / OpenAPI
- SonarQube Community Build

## 3. Prerequisites

Ensure the following are installed:

- JDK 17 or later
- MySQL Server
- Maven, or use the Maven Wrapper included in the project

## 4. Configuration

Configure the database connection in your local application configuration file.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/javaassessment
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
```

Create the required database in MySQL before starting the application.

Use your local credentials and avoid committing passwords or other secrets to GitHub.

## 5. How to Run the Application

1. Clone or download the repository.
2. Configure the MySQL connection.
3. Open a terminal in the project root directory.
4. Run the application using the Maven Wrapper.

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Alternatively, build the project:

```bash
mvnw.cmd clean package
```

## 6. API Documentation

The application exposes REST endpoints for customer, product, and sales management.

The APIs support the applicable HTTP operations, including POST, GET, PUT, and PATCH.

Swagger UI can be used to explore and test the available endpoints.

Swagger UI URL: `http://localhost:8080/swagger-ui/index.html`


## 7. Product File Upload

The application supports uploading product data from a text file. Product fields are separated by the pipe (`|`) delimiter.

Example:

```text
Laptop|Gaming laptop|2999.90|10
Mouse|Wireless mouse|59.90|50
```

Each row must follow the field order and validation rules implemented by the application.

## 8. Sales Report

The application supports generating a PDF sales report for a specified sales date using JasperReports.

The report is generated from sales records retrieved from the database.

## 9. Testing

Automated tests are implemented using JUnit and Mockito.

Run the tests using:

```bash
mvnw.cmd test
```

Run the tests and build the project using:

```bash
mvnw.cmd clean verify
```

## 10. Code Quality Analysis

SonarQube Community Build was used to analyse the project's code quality and identify potential issues.

Analysis results:

Tool: SonarQube Community Build

Project: JavaAssessment

Analysis status: [Passed]

Issues remaining: [51]

## Code Quality Analysis

Analysed using SonarQube Community Build.

### Overview
![SonarQube Overview](docs/screenshots/sonarqube-overview.png)

### Issues
![SonarQube Issues](docs/screenshots/sonarqube-issues.png)