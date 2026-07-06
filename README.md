# 🚀 LevelUp – Backend API

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-green.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Local-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

The robust server-side architecture for **LevelUp**, a platform that gamifies the student experience. This RESTful API orchestrates user progression and real-time task management.

Looking for the client-side user interface? Check out the [LevelUp Frontend](https://github.com/l4aaa/LevelUP-frontend).

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
* **Infrastructure**: Local hosting via **Docker Compose** (Spring Boot API + PostgreSQL).
* **Security**: Spring Security, JWT, BCrypt, RBAC.
* **Utilities**: Lombok, Jakarta Validation, @ConfigurationProperties.
* **Database**: Hibernate/JPA (UUID Primary Keys, JPA Converters for Sealed Types).

---

## 🛡️ Resilience & Recovery

The application is built to handle local interruptions and container restarts gracefully:
* **Stuck Task Recovery**: The `resetStuckTasks` bean in `BackendApplication.java` automatically runs on startup. It scans for tasks that were in a `VERIFYING` state (e.g., if the container crashed or restarted during an active verification session) and resets them to `PENDING`, ensuring no student's progress is lost due to infrastructure restarts.

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
| **PUT** | `/api/admin/users/{id}` | Update user stats (Level, XP, Streak, Role). |
| **DELETE** | `/api/admin/users/{id}` | Permanently delete a user profile. |

---

## 🐳 Local Hosting with Docker (Recommended)

The easiest way to run the entire stack (Spring Boot API + PostgreSQL) is using **Docker Compose**.

### 1. Prerequisites
Ensure you have [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) installed.

### 2. Running the Stack
To start the database and application containers:
```bash
docker compose up --build
```
This command:
1. Builds the Spring Boot container using the multi-stage `Dockerfile`.
2. Starts the PostgreSQL container.
3. Automatically imports the schema and seed data from `levelup_db.sql` on the first startup.
4. Starts the backend application once the database healthcheck passes.

The backend API will be accessible at: `http://localhost:8080`

### 3. Stopping the Stack
To stop the services:
```bash
docker compose down
```
To also remove the persistent database volume (for a fresh start):
```bash
docker compose down -v
```

---

## 🚀 Native Local Setup (Without Docker)

If you prefer to run the application natively on your host machine:

### 1. Prerequisites
* **Java 21** installed.
* **PostgreSQL** installed and running.

### 2. Local Database Setup
1. Create a database named `levelup_db`:
   ```bash
   psql -h localhost -U postgres -c "CREATE DATABASE levelup_db;"
   ```
2. Import the initial schema and data:
   ```bash
   psql -h localhost -U postgres levelup_db < levelup_db.sql
   ```

### 3. Environment Configuration
1. Copy the `.env.example` file to `.env`:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and fill in your database credentials and `JWT_SECRET`.

### 4. Running the Application
Export the environment variables and run the Spring Boot application:

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
