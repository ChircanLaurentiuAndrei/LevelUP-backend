# 🚀 LevelUp Backend

The backend API for **LevelUp**, a high-performance gamified learning platform. This Spring Boot service orchestrates secure authentication, user progression tracking, and an asynchronous task verification engine designed for modern, responsive user experiences.

---

## 📋 Table of Contents

* [Overview](#-overview)
* [Tech Stack](#-tech-stack)
* [System Architecture](#-system-architecture)
* [Key Features](#-key-features)
* [API Documentation](#-api-documentation)
* [Getting Started](#-getting-started)

---

## 📖 Overview

LevelUp transforms traditional studying into an engaging, progression-based experience. Users choose a dedicated study program, complete curriculum-aligned tasks, and earn Experience Points (XP) to increase their profile level and unlock prestigious achievements.

The backend provides the backbone for this ecosystem, utilizing **PostgreSQL** for persistence and a sophisticated **@Async verification mechanism** to handle heavy processing without blocking user workflows.

---

## 🛠 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 4.0 (Web, Security, Data JPA)
* **Database:** PostgreSQL
* **Security:** Stateless JWT (JSON Web Tokens) & BCrypt hashing
* **Build Tool:** Maven 3.9.11
* **Processing:** Asynchronous Event Handling (`@Async`)

---

## 🏗 System Architecture

The application follows a clean **Controller → Service → Repository** architectural pattern.

### 🔄 Asynchronous Task Flow

To ensure zero UI lag, task completion follows a non-blocking background process:

1. **Submission:** User submits a task via `POST /api/tasks/{id}/complete`.
2. **Atomic Lock:** The system performs an atomic update, switching the task status from `PENDING` to `VERIFYING`.
3. **Immediate Response:** The API returns a success message immediately to the client.
4. **Background Processing:**
    * The `VerificationService` simulates heavy grading/verification (3-second delay).
    * On completion, the status updates to `COMPLETED`.
    * The `GamificationService` calculates XP, checks for level-ups, and evaluates achievement criteria.

---

## ✨ Key Features

### 🔐 Security & Identity

* **Stateless Authentication:** Secure JWT-based sessions.
* **CORS Ready:** Pre-configured for modern frontend environments (`localhost:3000`, `localhost:5173`).
* **Robust Validation:** Input validation for registration and login via Jakarta Validation.

### 🎮 Gamification Engine

* **Dynamic Leveling:** Users level up for every 100 XP earned.
* **Daily Task Rotation:** The system automatically cleans unfinished tasks and assigns 8 new random tasks upon the first login of a new day.
* **Streak System:** Tracks consecutive daily logins to encourage consistency.
* **Achievements:** Automated badge unlocking based on `TASK_COUNT`, `LEVEL_THRESHOLD`, `XP_TOTAL`, or `STREAK_DAYS`.

---

## 📡 API Documentation

### Authentication

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | `/api/auth/register` | Create a new account & assign initial tasks |
| POST | `/api/auth/login` | Authenticate & update daily streaks |
| GET  | `/api/auth/study-programs` | List available learning paths |

### Dashboard & Progression

| Method | Endpoint | Description |
|------|---------|-------------|
| GET  | `/api/dashboard` | Retrieve XP, level, and today's task list |
| POST | `/api/tasks/{id}/complete` | Trigger async task verification |
| GET  | `/api/user/me` | Detailed profile with unlocked achievements |
| GET  | `/api/user/leaderboard` | Global rankings by XP |

---

## 🚀 Getting Started

### Prerequisites

* **JDK 21**
* **PostgreSQL 15+** (Running on port 5432)

### Configuration

Update the `src/main/resources/application.properties` with your environment variables:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/levelup_db
spring.datasource.username=your_user
spring.datasource.password=your_password

jwt.secret=your_secure_256_bit_secret_key
jwt.expiration=86400000
```
### Execution

1. **Initialize Database:** Create levelup_db and apply your schema.

2. **Run with Maven Wrapper:**
```
./mvnw spring-boot:run
```