# PlayGrounds API

A Spring Boot REST API for managing playgrounds, attractions, and kids. The API supports creating playgrounds with attractions, adding/removing kids, tracking utilization, and more. Validation is handled using Bean Validation annotations.

## Features

- Create, retrieve, and delete playgrounds
- Add and remove kids from playgrounds
- Track playground utilization and total visitors
- Input validation with detailed error responses
- Interactive API documentation with Swagger UI

## Installation
   mvn clean install
   run [PlayGroundsApplication.java] or mvn spring-boot:run

The API will be available at `http://localhost:8080`.

## Swagger UI

Interactive API documentation is available via Swagger UI.

- Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) in your browser.
- Explore and test all endpoints directly from the UI.

## API Endpoints

| Method | Endpoint                                 | Description                                 |
|--------|------------------------------------------|---------------------------------------------|
| POST   | `/api/v1/playsites`                      | Create a new playground                     |
| GET    | `/api/v1/playsites/{id}`                 | Get playground details by ID                |
| DELETE | `/api/v1/playsites/{id}`                 | Delete a playground by ID                   |
| POST   | `/api/v1/playsites/{id}/kids`            | Add a kid to a playground                   |
| DELETE | `/api/v1/playsites/{id}/kids/{ticket}`   | Remove a kid from a playground by ticket    |
| GET    | `/api/v1/playsites/{id}/utilization`     | Get utilization percentage for a playground |
| GET    | `/api/v1/playsites/visitors`             | Get total visitors today across all sites   |


## Tech

- Java 21
- Spring Boot
- Maven
- Swagger (springdoc-openapi)
- JUnit & Mockito (for testing)

---