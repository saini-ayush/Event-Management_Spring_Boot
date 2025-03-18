# Event Management System

## Overview
A Spring Boot application for managing events, bookings, and ticket sales. This system provides functionality to create, update, and delete events, as well as track available seats and ticket pricing. This is a learning project for practicing Spring Boot and API development.

## Features
- Event creation and management
- Filtering for upcoming events
- Available seats tracking
- Ticket price management
- Event location management

## Technical Stack
- Java
- Spring Boot
- Maven
- JUnit 5 for testing
- Mockito for mocking during tests

## Project Structure
- **Model**: Event entities with properties like name, description, dates, location, and seating capacity
- **Repository**: Data access layer for event persistence
- **Service**: Business logic implementation
- **Controller**: (Not visible in provided code) REST endpoints for the application
- **DTO**: Request/response objects for data transfer

## Getting Started
1. Clone the repository
2. Build the project with Maven: `./mvnw clean install`
3. Run the application: `./mvnw spring-boot:run`

## Testing
The application includes comprehensive unit tests for the service layer using JUnit 5 and Mockito.

Run tests with: `./mvnw test`

## API Endpoints
### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get access token

### Admin Endpoints
- `POST /api/admin/events` - Create event
- `PUT /api/admin/events/{id}` - Update event
- `DELETE /api/admin/events/{id}` - Delete event
- `GET /api/admin/events` - View all events
- `GET /api/admin/bookings` - View all bookings

### User Endpoints
- `GET /api/events` - View available events
- `POST /api/events/{id}/book` - Book tickets
- `DELETE /api/events/{id}/cancel` - Cancel booking
- `GET /api/events/history` - View booking history

## Docker Installation

1. Build the project to get the JAR file

```
./mvnw clean install
```

2. Build the Docker image
```
docker-compose up
```