# 🚀 LevelUp – Backend API

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-green.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18+-336791.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

The robust server-side architecture for **LevelUp**, a platform that gamifies the student experience. This RESTful API orchestrates user progression, secure authentication, and real-time task management using a modern Java stack.

---

## ✨ Key Features

### 🎮 Gamification Engine
* **XP & Leveling System**: Calculates experience points with a fixed threshold of **100 XP per level**.
* **Dynamic Achievements**: Automatically unlocks badges based on criteria types like `TASK_COUNT`, `STREAK_DAYS`, `XP_TOTAL`, and `LEVEL_THRESHOLD`.
* **Global Leaderboard**: Optimized queries to rank users, excluding Admins from the competition.

### 🧠 Intelligent Task Management
* **Smart Assignment Algorithm**: Assigns a **daily limit of 8 tasks**. It prioritizes program-specific tasks (minimum 4) and fills the remainder with global quests.
* **Async Verification**: Uses non-blocking threads (`@Async`) to simulate a **3-second grading process**, keeping the API responsive while processing happens in the background.
* **Self-Healing Architecture**: Includes a startup routine (`CommandLineRunner`) that automatically detects and resets "stuck" tasks (tasks trapped in `VERIFYING` state) back to `PENDING`.

### 🔐 Security & Architecture
* **Stateless Auth**: Full JWT (JSON Web Token) implementation with custom filters. Tokens are valid for **24 hours**.
* **Role-Based Access Control (RBAC)**: Secure endpoints for standard `USER` and privileged `ADMIN` roles.
* **Data Integrity**: Uses **Pessimistic Locking** (`PESSIMISTIC_WRITE`) to prevent race conditions during concurrent XP updates.
* **CORS Configured**: Pre-configured to allow requests from `http://localhost:5173` and `http://localhost:3000`.

---

## 🛠️ Tech Stack

* **Core**: Java 21, Spring Boot 4.0.0 (Web, Security, Data JPA).
* **Database**: PostgreSQL.
* **Security**: Spring Security, JJWT (0.13.0), BCrypt.
* **Utilities**: Lombok, Jakarta Validation.
* **Testing**: JUnit 5, Spring Boot Test.

---

## 📡 API Endpoints

### 🟢 Authentication
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Register a new student and trigger initial task assignment. |
| **POST** | `/api/auth/login` | Authenticate user, auto-calculate streaks, and return JWT. |
| **GET** | `/api/auth/study-programs` | List all available faculties/majors. |

### 🟡 Core Features (User)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/dashboard` | Get current user stats, level progress, and daily task list. |
| **POST** | `/api/tasks/{id}/complete` | Submit a task. Triggers background verification thread. |
| **GET** | `/api/user/me` | Fetch full user profile and unlocked achievement IDs. |
| **GET** | `/api/user/leaderboard` | Retrieve the global ranking of top students. |
| **GET** | `/api/user/achievements` | List all available achievements in the game. |

### 🔴 Administration
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/admin/users` | List all users in the system. |
| **PUT** | `/api/admin/users/{id}` | Update user details (Level, XP, Streak, Role). |
| **DELETE** | `/api/admin/users/{id}` | Permanently delete a user. |

---

## 🚀 Getting Started

### 1. Prerequisites
* **Java JDK 21** installed.
* **PostgreSQL** installed and running on port `5432`.

### 2. Database Setup
Create a local database named `levelup_db`.
```bash
createdb levelup_db