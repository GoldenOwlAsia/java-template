<p align="center">
  <img src="https://res.cloudinary.com/deop9ytsv/image/upload/v1542422606/spring-boot-icon0_cf21dec4-5056-b3a8-49c015fd3bde6cb5.png" alt="Project Banner" />
</p>

<h2 align="center">Spring Template - Mono Project</h2>

<p align="center">
  This is a monolithic web application backend built with Spring Boot 3, structured using the Packaged by Feature approach. It supports secure authentication, authorization, user management, role-based permission control, and email notifications using templated emails.
</p>

<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Java-21-007396" /></a>
  <a href="#"><img src="https://img.shields.io/badge/SpringBoot-3.x-brightgreen" /></a>
  <a href="#"><img src="https://img.shields.io/badge/PostgreSQL-17-blue" /></a>
  <a href="#"><img src="https://img.shields.io/badge/Build-passing-success" /></a>
</p>

---

## 🧩 Description

This monolithic backend service supports authentication, caching, mailing, and data persistence for a scalable web application. It is built on Spring Boot 3 with a focus on security, modularity, and clean architecture principles.

The project is currently organized using a package-by-feature structure to improve modularity and feature ownership.
If simplicity is preferred, the structure can be easily refactored to a traditional package-by-layer approach.

---

## ⚙️ Technologies Used

### ✅ Core Frameworks
- **Spring Boot 3.x** – Main application framework.
- **Spring Security** – For securing the APIs with JWT & OAuth2.
- **Spring Data JPA** – Database access with Hibernate.
- **Spring Validation** – Request validation and DTO binding.
- **Spring Cache (Caffeine)** – High-performance in-memory caching.

### 📦 Database & Migration
- **PostgreSQL** – Primary database.
- **Flyway** – Database version control.

### 🔐 Authentication & Authorization
- **JWT** – Token-based authentication.
- **Spring OAuth2 Resource Server & Client** – Integration with OAuth2 providers.
- **Google OAuth2** – Login with Google integration.
- **Refresh Token Support**
- **Logout Handling via Token Blacklist using Caffeine Cache**
- **Permission-based Authorization** – Each user can be assigned roles, and roles are associated with fine-grained permissions (e.g., CLASS_CREATE, USER_DELETE).
- **Access control is enforced via custom annotations and method-level security**

### 📊 Documentation & API
- **Springdoc OpenAPI** – Interactive Swagger UI.

### 💌 Email & Templating
- **Spring Mail** – For sending emails.
- **Thymeleaf** – Email template rendering.

### 🧰 Development Utilities
- **Lombok** – Less boilerplate code.
- **MapStruct** – Efficient Java bean mapping.
- **Spring Boot Devtools** – Hot reload during development.

### 🧪 Testing
- **Spring Boot Starter Test**
- **H2 Database** – In-memory DB for testing.
- **Spring Security Test** – Test secured endpoints.

---

## 📁 Project Structure
... More