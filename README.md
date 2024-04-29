# User management API

## Introduction
This is a test assignment which is Java-based RESTful application to manage users through endpoints.
## Technologies Used
The project is developed using the following technologies and tools:
- **Java(17)**
- **Spring Boot**
- **Spring Data JPA**
- **MySQL DB**
- **Swagger** (for API documentation)
- **Jakarta Validation**
- **JUnit**
- **Maven** (for project management and checkstyle)

#### User Controller Endpoints Overview:
1. **Register User**:
    
    - **Endpoint:** `POST /api/users`
    - **Description:** Registers a new user by creating, validating, and saving user details to the database. (cannot register if under 18)
    - **Request Body:** UserRegisterRequestDto
    - **Response:** UserResponseDto
    - **HTTP Status Code:** 201 Created
2. **Update User Email by ID**:
    
    - **Endpoint:** `PATCH /api/users/{id}`
    - **Description:** Updates the email of a user identified by their ID.
    - **Request Body:** UpdateUserRequestDto
    - **Response:** UserResponseDto
    - **HTTP Status Code:** 200 OK
3. **Update User by ID**:
    
    - **Endpoint:** `PUT /api/users/{id}`
    - **Description:** Updates all user data fields by the specified user ID.
    - **Request Body:** UserRegisterRequestDto
    - **Response:** UserResponseDto
    - **HTTP Status Code:** 200 OK
4. **Delete User by ID**:
    
    - **Endpoint:** `DELETE /api/users/{id}`
    - **Description:** Deletes a user from the database by the specified ID.
    - **Response:** No content
    - **HTTP Status Code:** 204 No Content
5. **Search User by Birth Date Range**:
    
    - **Endpoint:** `GET /api/users/search`
    - **Description:** Searches for users within the specified range of birth dates.
    - **Query Parameters:**
        - `from`: Start date of the birth date range
        - `to`: End date of the birth date range
    - **Response:** List of UserResponseDto
    - **HTTP Status Code:** 200 OK

#### Dependencies:

- **Spring Web:** Used for building RESTful web services.
- **Spring Boot Starter Validation:** For request validation using annotations.
- **Project Lombok:** For reducing boilerplate code with annotations like `@RequiredArgsConstructor`.
## Setup and Usage

## Requirements
- Java-17 or above
- MySQL server 8.0.36 or above
- Apache Maven 3.6.3 or above
- Git latest version
## Instalation
To set up and use the project:
1. Clone the repository:
   `git clone https://github.com/luk14n/user-api.git`
1. Build the project using Maven:
   `mvn -X clean package`.
3. Run the application: `java -jar target/*.jar`.
   or just hit play button if using Inteliji IDEA
1. Access the Swagger documentation at `http://localhost:8080/swagger-ui.html` for detailed API information.
