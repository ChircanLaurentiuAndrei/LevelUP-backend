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
* **Dynamic Achievements**: Automatically unlocks badges based on criteria like `TASK_COUNT`, `LEVEL_THRESHOLD`, `XP_TOTAL`, and `STREAK_DAYS`.
* **Global Leaderboard**: Optimized server-side ranking that explicitly excludes administrators.

### 🧠 Intelligent Task Management
* **Smart Assignment Algorithm**: Assigns a **daily limit of 8 tasks**, prioritizing program-specific quests.
* **Async Verification**: Uses non-blocking threads (`@Async`) to simulate a grading process, awarding XP only after verification completes.
* **Resilient Lifecycle**: Includes a startup routine and runtime fallbacks to recover tasks from failed verification states.

### 🔐 Modern Architecture
* **Supabase Auth Integration**: Leverages Supabase Auth (GoTrue) for identity. Internal data is mapped to the `profiles` table linked via **UUID**.
* **Stateless Resource Server**: Acts as a secure resource server that validates Supabase-issued JWTs.
* **Java 21 Modernization**: Utilizes **Records** for immutable DTOs and **Pattern Matching** for cleaner business logic.
* **Strict Layering**: Enforces a service-centric architecture with constructor injection and standardized global error handling.

---

## 🛠️ Tech Stack

* **Core**: Java 21, Spring Boot 4.0.0.
* **Infrastructure**: Local PostgreSQL.
* **Security**: Spring Security (Supabase JWT Validation), RBAC.
* **Utilities**: Lombok, Jakarta Validation, @ConfigurationProperties.
* **Database**: PostgreSQL (UUID Primary Keys, Identity Columns).

---

## 📡 API Endpoints

### 🟢 Metadata (Public/Auth)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
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
| **GET** | `/api/admin/users` | List all registered profiles. |
| **PUT** | `/api/admin/users/{uuid}` | Update user stats (Level, XP, Streak, Role). |
| **DELETE** | `/api/admin/users/{uuid}` | Permanently delete a user profile. |

*Note: Authentication (Signup/Login) is handled directly via Supabase Auth on the client side.*

---

## 🚀 Setup & Configuration

### 1. Local Database Setup
1. Install PostgreSQL on your machine.
2. Create a new database named `levelup`.
3. Run the provided SQL schema `levelup_db.sql` to initialize the tables and data.
   ```bash
   psql -U postgres -d levelup -f levelup_db.sql
   ```

### 2. Environment Variables
Create a `.env` file in the root directory (or set environment variables) with the following keys:

```env
# Local Database Credentials
SUPABASE_DB_PASSWORD=your_local_db_password
SUPABASE_DB_URL=jdbc:postgresql://127.0.0.1:5432/levelup

# Supabase Auth Secrets (Found in your Supabase project dashboard)
SUPABASE_JWT_SECRET=your_supabase_jwt_secret
```

### 3. Running the Application
Ensure the environment variables are loaded and run:
```bash
./mvnw spring-boot:run
```
