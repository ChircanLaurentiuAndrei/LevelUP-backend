# 🚀 LevelUp – Backend API

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-green.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Aiven-blue.svg)
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
* **Infrastructure**: Distributed system with **Spring Boot on Render** and **PostgreSQL on Aiven**.
* **Security**: Spring Security, JWT, BCrypt, RBAC.
* **Utilities**: Lombok, Jakarta Validation, @ConfigurationProperties.
* **Database**: Hibernate/JPA (UUID Primary Keys, JPA Converters for Sealed Types).

---

## 🌐 Hosting & Performance

### ❄️ Cold Start Behavior
This service is hosted on **Render's Free Tier**, which includes the following performance characteristics:
* **Idle Spin-down**: The service automatically spins down after **15 minutes of inactivity**.
* **Startup Latency**: The first request after a spin-down may take **~60 seconds** to respond as the instance wakes up.
* **Database Persistence**: Managed externally via **Aiven**, ensuring data remains intact during server spin-downs.

### 🛡️ Resilience & Recovery
The application is designed to handle the ephemeral nature of free-tier hosting:
* **Stuck Task Recovery**: The `resetStuckTasks` bean in `BackendApplication.java` automatically runs on startup. It scans for tasks that were in a `VERIFYING` state (e.g., if the server spun down during an active verification session) and resets them to `PENDING`, ensuring no student's progress is lost due to infrastructure idle-outs.

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

### 1. Prerequisites
* **Java 21** installed.
* **PostgreSQL** installed and running.

### 2. Local Database Setup
1. Create a new database named `levelup_db`.
   ```bash
   psql -h localhost -U postgres -c "CREATE DATABASE levelup_db;"
   ```
2. Import the initial schema and data (optional but recommended for development):
   ```bash
   psql -h localhost -U postgres levelup_db < levelup_db.sql
   ```

### 3. Environment Configuration
The application requires several environment variables for security and database connectivity.

#### 🏗️ Render / Aiven (Production)
For the deployed version, the following environment variables are configured in the Render Dashboard:
* **DB_URL**: The JDBC connection string provided by Aiven (e.g., `jdbc:postgresql://<host>:<port>/defaultdb?sslmode=require`).
* **DB_USERNAME**: Database user provided by Aiven (usually `avnadmin`).
* **DB_PASSWORD**: Database password provided by Aiven.
* **JWT_SECRET**: A secure random string used for signing tokens.

#### 💻 Local Development
A template is provided in `.env`.

1. Copy the `.env` template or create a new one:
   ```bash
   # Add your specific values to the .env file
   DB_URL=jdbc:postgresql://localhost:5432/levelup_db
   DB_USERNAME=postgres
   DB_PASSWORD=your_password
   JWT_SECRET=your_secure_random_string_at_least_32_chars
   ```
2. The application will fail to start if `JWT_SECRET` is not provided.

### 4. Running the Application
Since Spring Boot does not natively load `.env` files, you must export the variables before running the application:

**Linux / macOS:**
```bash
export $(grep -v '^#' .env | xargs) && ./mvnw spring-boot:run
```

**Windows (PowerShell):**
```powershell
foreach($line in Get-Content .env) {
    if($line -and -not $line.StartsWith('#')) {
        $name, $value = $line -split '=', 2
        [System.Environment]::SetEnvironmentVariable($name, $value)
    }
}
./mvnw spring-boot:run
```

Alternatively, you can provide the variables directly as Maven arguments:
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DJWT_SECRET=your_secret -DDB_PASSWORD=your_password"
```
