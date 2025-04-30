# Task-Manager

A robust To-Do application backend built with Spring Boot, featuring MongoDB for data storage and Redis for token/session management. Includes authentication, role-based access control, and email notifications.

Features :

User Authentication
Email/password registration with verification
Login/logout functionality
Password reset functionality
Role-Based Access Control
USER and ADMIN roles
Protected endpoints based on roles
To-Do Management
Create, read, update, and delete todos
Mark todos as complete
Set due dates
mail Notifications
Account verification emails
Login alerts
Password reset emails
OAuth2 Integration
Google login support
Data Persistence
MongoDB for primary data storage
Redis for token/session management

Technologies Used :

Backend: Spring Boot 3.2.4
Database: MongoDB
Cache/Session: Redis
Authentication: Spring Security
Email: JavaMailSender
API Documentation: None (Swagger/OpenAPI removed)

Build Tool: Maven

Java Version: 21

Getting Started
Prerequisites
Java 21 JDK

Maven 3.6+

MongoDB 5.0+

Redis 7.0+

SMTP email service credentials (for email functionality)

property file :

# Server
server.port=8080

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/todoapp

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# Email
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-password
app.email.from=your-email@example.com

# OAuth2 (optional)
spring.security.oauth2.client.registration.google.client-id=your-client-id
spring.security.oauth2.client.registration.google.client-secret=your-client-secret
