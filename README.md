# 📚 Bookshop API Project

Welcome to the **Bookshop API**! This project is an API designed for managing an online bookstore, providing features for browsing books, managing shopping carts, and processing orders. This project demonstrates robust backend development practices using Spring Boot and other advanced technologies.

> **Goal**: Develop a seamless online bookstore experience with secure authentication, dynamic data handling, and user-friendly endpoints.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Technologies Used](#technologies-used)
3. [Features](#features)
4. [Architecture](#architecture)
5. [Installation and Setup](#installation-and-setup)
6. [API Documentation](#api-documentation)
7. [Challenges & Solutions](#challenges--solutions)
8. [Future Improvements](#future-improvements)
9. [Usage Demo](#usage-demo)
10. [Contact](#contact)

---

## Project Overview

The **Bookshop API** is an online bookstore backend that supports functionalities such as user registration, book browsing, shopping cart management, and order placement. It aims to be robust, secure, and simple to integrate with front-end interfaces, providing a comprehensive backend for managing books, orders, and user accounts.

## Technologies Used

- **Java** - Core programming language for backend development.
- **Spring Boot** - Framework used for building the API with quick setup and pre-configured defaults.
- **Spring Security** - Provides authentication and authorization features to secure the application.
- **Spring Data JPA** - Simplifies database access and supports relational data management.
- **Swagger** - Used for API documentation, making it easier to understand and test the endpoints.
- **Docker** - Containerization for simplified setup and deployment.
- **Test containers** - Integration testing with real databases in Docker.
- **Liquibase** - Manages database migrations and versioning.
- **JUnit & Mockito** - Unit testing and mocking for test coverage.
- **Maven** - Dependency management and build tool to streamline project setup and maintenance.
- **TC Database**: Lightweight, in-memory database for testing purposes
- **Postman**: For endpoint testing and verification

## Features
This API includes the following main features:

1. **User Registration and Authentication** - Users can register, log in, and log out securely, with token-based authentication provided by Spring Security.
2. **Book Management** - Admin users can add, edit, or delete books, while regular users can browse the book catalog.
3. **Category Management** - Allows categorization of books, enabling better browsing and filtering for users.
4. **Shopping Cart** - Users can add, view, update, and remove items in their shopping cart.
5. **Order Processing** - Handles order creation, confirmation, and payment (with placeholder endpoints for payment integration).
6. **API Documentation** - Provides an interactive interface through Swagger for testing and understanding API endpoints.

## Architecture
The architecture of the project follows a typical layered structure for a Spring Boot application:

1. **Controller Layer**: Handles HTTP requests and sends responses.
2. **Service Layer**: Contains business logic and manages transactions.
3. **Repository Layer**: Directly interacts with the database using JPA.
4. **Entity Layer**: Models core entities like User, Book, Order, and Category.
5. **Security Layer**: Manages authentication and authorization using JWT.
6. **Documentation Layer**: Swagger setup for API interaction.

## Installation and Setup

To run this project locally, follow these steps:

### Prerequisites
- **Java 17** or higher
- **Maven** for dependency management
- **Docker** and **Docker Compose**

### Steps to Launch the Application

1. **Clone the Repository**
   ```bash
   git clone https://github.com/AGuliaiev/Spring_intro
   ```
2. **Configure Environment Variables:** Create a .env file with the following variables:
    ```env
    MYSQL_DB=book_shop
    MYSQL_USER=your_mysql_user
    MYSQL_PASSWORD=your_mysql_password
    MYSQL_ROOT_PASSWORD=root_password
    MYSQL_ROOT_PASSWORD=superpassword
    MYSQLDB_USER=mysql_user
    MYSQLDB_PASSWORD=mysql_password
    MYSQL_LOCAL_PORT=3307
    MYSQL_DOCKER_PORT=3306
   
    SPRING_LOCAL_PORT=8088
    SPRING_DOCKER_PORT=8080
    DEBUG_PORT=5005
   ```
3. **Run Docker Containers:**
   ```bash
   docker-compose up
   ```
4. **Access the API:**
   - **Swagger Documentation:** Open [Swagger UI](http://localhost:8088/swagger-ui/) to explore and test API endpoints.
   - **Application:** Accessible at http://localhost:8080.

5. **Run Tests** Execute unit and integration tests:
    ```bash
   mvn clean test
   ```

## API Documentation
The API documentation is available via Swagger and includes the following key endpoints:
1. **User Registration** - POST /auth/register: Allows new users to create accounts.
2. **User Login** - POST /auth/login: Authenticates users and returns a JWT token.
3. **Book Management** - GET /books, POST /books (admin), PUT /books/{id} (admin), DELETE /books/{id} (admin).
4. **Category Management** - GET /categories, POST /categories (admin).
5. **Shopping Cart** - POST /cart: Adds a book to the user's cart, GET /cart: Views current cart items.
6. **Order Processing** - POST /orders: Places a new order based on the items in the cart.
Visit [Swagger UI](http://localhost:8088/swagger-ui/) to view all endpoints and their detailed descriptions.

## Challenges & Solutions
1. **Database Migration:**
    - **Issue:** Ensuring schema consistency across environments.
    - **Solution:** Used Liquibase for versioned migrations, ensuring consistency and easy rollback in case of issues.
2. **Security Authentication:**
    - **Issue:** Securing endpoints based on user roles and managing JWT tokens.
    - **Solution:** Configured Spring Security with role-based access controls and implemented JWT-based authentication for stateless session management.
3. **Testing with Docker:**
    - **Issue:** Managing test environments with real databases.
    - **Solution:** Utilized Test-containers for Docker-based integration testing.

## Future Improvements
1. **Enhanced Search and Filtering** - Adding more filters for book categories, authors, and prices to improve user experience.
2. **Full Payment Integration** - Integrating with a payment gateway for real transaction processing.
3. **Order History and Notifications** - Adding order history for users and email/SMS notifications upon order placement.
4. **Review and Rating System** - Allowing users to rate and review books for better recommendations.

## Usage Demo
For a quick demonstration of how this project works, you can use the following methods:
 - **Swagger UI** - Visit [Swagger UI](http://localhost:8088/swagger-ui/) to interact with the API.
 - **Testing the API with Postman** 
   To test the Bookshop API endpoints using Postman, follow these steps:

1. **Set Up Your Environment in Postman:**
   - Open Postman and create a new environment.
   - Add a variable base_url with a value pointing to your local server (e.g., http://localhost:8080).

2. **Create Requests:**
   - Manually create HTTP requests for each API endpoint you want to test, setting the URL to {{base_url}}/your-endpoint.
   - For example:
      - **User Registration:** POST {{base_url}}/auth/register
      - **Login:** POST {{base_url}}/auth/login 
      - **View Books:** GET {{base_url}}/books

3. **Add Authorization:**
   - For endpoints requiring authorization, obtain a JWT token from the login endpoint.
   - Add this token to the Authorization tab of your request as a Bearer Token.

4. **Send Requests:**
   - Once configured, click Send to test each endpoint.
   - Check the response to verify correct functionality and troubleshoot as needed.

With Postman, you can quickly test each API endpoint and verify the application's functionality.

---

This step-by-step guide explains how to configure and use Postman with your API for straightforward testing without needing a pre-configured collection file.
## Contact
For questions or contributions:
- **Email:** gulini32@gmail.com
- **GitHub:** AGuliaiev

---

This project demonstrates a complete API setup for an online bookstore, with modern Java technologies and robust design. Feel free to explore, test, and contribute!

Also I'm adding the video where I show how my application works: https://www.loom.com/share/d5c62e5978264f9a8432b95ed4411809
