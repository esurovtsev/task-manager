# Task Manager Application

A modern, feature-rich Task Management application built with Spring Boot, demonstrating best practices in web application development. This project serves as a showcase of AI-driven development using Cascade AI Agent in the Windsurf editor.

## Overview

This Task Manager is a web application that helps users organize and track their tasks efficiently. What makes this project unique is that the entire codebase was generated by the Cascade AI Agent with minimal human supervision, demonstrating the capabilities of modern AI-assisted development.

The application is being developed incrementally, with new features being added in a series of development iterations, each documented in video format showing the interaction between human developer and AI agent.

## AI-Driven Development

This project showcases:
- Full codebase generation by Cascade AI Agent
- Best practices in software development automatically applied by AI
- Human-AI collaboration in software development
- Practical application of AI agents in real-world projects

## Features

- Task Management
  - Create, read, update, and delete tasks
  - Task properties: title, description, status, priority, due date
  - Filter tasks by status and priority
  - Sort tasks by various fields
  - Pagination support

- MongoDB Integration
  - Persistent storage using MongoDB
  - Automatic sample data seeding
  - MongoDB Express UI for database management

- Authentication & Authorization
  - MongoDB-based user authentication
  - Role-based access control (USER, ADMIN)
  - BCrypt password encoding
  - Basic authentication support
  - Automatic test user creation

### Core API (Current)
- RESTful API endpoints for task management
- CRUD operations for tasks
- Advanced task filtering and search capabilities
- Pagination support
- Proper error handling and validation

### Task Properties
- Unique identifier
- Name and description
- Due date
- Priority levels
- Status tracking
- Tag support for better organization

## Technical Stack

### Backend
- Java 17
- Spring Boot
- Spring Web
- Maven for dependency management
- RESTful API design

## Project Structure

```
src/main/java/com/grabduck/taskmanager/
├── controller/         # REST API endpoints
├── domain/            # Domain models
├── dto/               # Data Transfer Objects
├── exception/         # Custom exceptions and error handling
└── service/          # Business logic layer
```

## Development Branches

The project is organized into feature branches, each representing a significant development milestone:

- [`feature/task-api-foundation`](https://github.com/esurovtsev/task-manager/tree/feature/task-api-foundation): Initial API implementation with core task management functionality
  - [Watch the development video](https://youtu.be/BPaF68MU4aM?si=7NnMAw4zf2Ed6Zbe) to see how this foundation was built using Cascade AI Agent
- [`feature/mongodb-persistence`](https://github.com/esurovtsev/task-manager/tree/feature/mongodb-persistence): MongoDB persistence layer implementation
  - [Watch the development video](https://youtu.be/HO0VcvkWpPA?si=u9WjEj7a4io7jg3K) to see how MongoDB persistence was implemented using Cascade AI Agent
  - This branch implements MongoDB persistence layer for the Task Manager application. Key features:
    - MongoDB integration with Spring Data MongoDB
    - Task document model and repository implementation
    - Advanced task filtering and search capabilities using MongoDB queries
    - Automatic database seeding with sample tasks on application startup
    - Docker Compose configuration for MongoDB and Mongo Express

  To run the application with MongoDB:
  1. Start MongoDB using Docker Compose:
     ```bash
     docker compose up -d
     ```
  2. Access MongoDB Express UI at http://localhost:8081
  3. Start the application - it will automatically connect to MongoDB and seed sample tasks
- [`feature/user-auth`](https://github.com/esurovtsev/task-manager/tree/feature/user-auth): Implements user authentication and authorization using MongoDB:
  - User domain model with roles (USER, ADMIN)
  - MongoDB-based user repository
  - Spring Security integration with MongoDB authentication
  - Test user seeding for development
  - BCrypt password encoding

  Default test user credentials:
  - Username: `user-new`
  - Password: `123`
  - Role: `USER`
- More branches will be added as new features are developed

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Building the Project
```bash
mvn clean install
```

### Running the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

All endpoints require Basic Authentication. Use the following credentials for testing:
- Username: `user-new`
- Password: `123`

### Available Endpoints

#### Tasks
- `POST /api/v1/tasks` - Create a new task
- `GET /api/v1/tasks` - Get paginated list of tasks with filtering options
- `GET /api/v1/tasks/{id}` - Get task by ID
- `PUT /api/v1/tasks/{id}` - Update an existing task
- `DELETE /api/v1/tasks/{id}` - Delete a task

## Contributing

This project is part of a development workshop series. Each major feature addition is documented and can be found in its corresponding branch.

## License

This project is open source and available under the [MIT License](LICENSE).

---
*Note: This is a living document that will be updated as new features are added to the application.*
