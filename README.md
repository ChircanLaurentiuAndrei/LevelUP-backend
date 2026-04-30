# 🚀 LevelUp – Backend API

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-green.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Local-blue.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

The robust server-side architecture for **LevelUp**, a platform that gamifies the student experience. This RESTful API orchestrates user progression and real-time task management.

---

## ✨ Key Features

### 🎮 Gamification Engine
* **XP & Leveling System**: Calculates experience points based on a configurable threshold (Default: **100 XP per level**).
* **Dynamic Achievements**: Automatically unlocks badges based on criteria defined in **Java 21 Sealed Interfaces**.
* **Global Leaderboard**: Optimized server-side ranking with **JOIN FETCH** to eliminate N+1 queries.
* **Lock Management**: Uses **Pessimistic Locking** on XP updates with a minimized transaction scope for high concurrency.

### 🧠 Intelligent Task Management
* **Smart Assignment Algorithm**: Assigns a **daily limit of 8 tasks**, prioritizing program-specific quests.
* **Async Verification**: Uses non-blocking threads (`@Async`) to simulate a grading process, awarding XP only after verification completes.
* **Resilient Lifecycle**: Includes a startup routine and runtime fallbacks to recover tasks from failed verification states.

### 🔐 Modern Architecture & Security
* **Integrated Authentication**: Built-in Signup and Login flow using **BCrypt** password hashing and **JWT** (3-hour TTL).
* **Domain Integrity**: Leverages **Java 21 Sealed Interfaces** (`TaskStatus`, `AchievementType`) for type-safe business logic.
* **Stateless Resource Server**: Standardized JWT validation with custom claims (`username`, `role`).
* **Strict Layering**: Enforces a service-centric architecture with constructor injection and standardized global error handling.
* **Validation**: Rigorous input sanitization using **Jakarta Bean Validation**.

---

## 🛠️ Tech Stack

* **Core**: Java 21, Spring Boot 4.0.0.
* **Infrastructure**: Local PostgreSQL.
* **Security**: Spring Security, JWT, BCrypt, RBAC.
* **Utilities**: Lombok, Jakarta Validation, @ConfigurationProperties.
* **Database**: Hibernate/JPA (UUID Primary Keys, JPA Converters for Sealed Types).

---

## 📡 API Endpoints

### 🟢 Authentication (Public)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Register a new user and assign initial daily tasks. |
| **POST** | `/api/auth/login` | Authenticate and receive a JWT (3h TTL). |
| **GET** | `/api/auth/study-programs` | List all available study programs for onboarding. |

### 🟡 Core Features (Authenticated User)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/dashboard` | Get stats, level progress, and active daily tasks. |
| **POST** | `/api/tasks/{id}/complete` | Submit a task for background verification. |
| **GET** | `/api/user/me` | Fetch full profile and unlocked achievements. |
| **GET** | `/api/user/leaderboard` | Retrieve global student rankings. |

### 🔴 Administration (Admin Only)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/admin/users` | List all registered profiles (Optimized fetching). |
| **PUT** | `/api/admin/users/{uuid}` | Update user stats (Level, XP, Streak, Role). |
| **DELETE** | `/api/admin/users/{uuid}` | Permanently delete a user profile. |

---

## 🚀 Setup & Configuration

### 1. Local Database Setup
1. Install PostgreSQL on your machine.
2. Create a new database named `levelup_db`.
   ```bash
   PGPASSWORD=your_postgres_password psql -h localhost -U postgres -c "CREATE DATABASE levelup_db;"
   ```
3. The application uses `hibernate.ddl-auto=update`, so tables will be created automatically on the first run.

### 2. Configuration
The application is configured in `src/main/resources/application.properties`. Default settings:
* **Port**: 8080
* **DB URL**: `jdbc:postgresql://localhost:5432/levelup_db`
* **JWT TTL**: 3 Hours

Ensure your local PostgreSQL user is `postgres` with password `postgres`, or update the properties file accordingly.

### 3. Running the Application
```bash
./mvnw spring-boot:run
```
