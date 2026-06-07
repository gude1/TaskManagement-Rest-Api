# Task Management REST API

A Spring Boot REST API for managing tasks per user. Users sign up, authenticate with JWT access and refresh tokens, and manage their own tasks stored in MySQL.

**Base URL:** `http://localhost:8080`

---

## Features

- User signup and login with **BCrypt** password hashing
- **JWT access tokens** (short-lived) and **refresh tokens** (stored in MySQL, revocable on logout)
- Task CRUD scoped to a user (`/api/users/{userId}/tasks`)
- **Pagination** for task lists
- Request validation with clear error responses
- Layered architecture: controllers (DTOs) → services (entities) → repositories (JPA)

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Runtime | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Web | Spring Web MVC |
| Persistence | Spring Data JPA, Hibernate, MySQL |
| Security | Spring Security, JWT (jjwt 0.12.6), BCrypt |
| Validation | Jakarta Bean Validation |
| Build | Maven |

---

## Prerequisites

- **Java 25** (JDK)
- **Maven** (or use the included `./mvnw` wrapper)
- **MySQL 8+** running locally (default port `3306`)

---

## Getting Started

### 1. Clone and configure secrets

**Do not put real passwords in `application.properties`** — that file is committed to GitHub.

Choose **one** of these options for local development:

#### Option A — Local properties file (easiest)

```bash
cp src/main/resources/application-local.properties.example \
   src/main/resources/application-local.properties
```

Edit `application-local.properties` with your MySQL password and JWT secret:

```properties
spring.datasource.password=your_mysql_password
jwt.secret=your_jwt_secret_at_least_32_characters_long
```

This file is **gitignored** and stays on your machine only.

#### Option B — Environment variables

Set variables in your terminal before running the app (macOS / Linux):

```bash
export DB_PASSWORD='your_mysql_password'
export JWT_SECRET='your_jwt_secret_at_least_32_characters_long'
./mvnw spring-boot:run
```

Optional overrides:

```bash
export DB_URL='jdbc:mysql://localhost:3306/taskdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME='root'
```

**Windows (PowerShell):**

```powershell
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="your_jwt_secret_at_least_32_characters_long"
.\mvnw.cmd spring-boot:run
```

**IDE (Cursor / VS Code / IntelliJ):** add `DB_PASSWORD` and `JWT_SECRET` in your run configuration's environment variables section so you don't need to export them in the terminal each time.

Environment variables take precedence over `application-local.properties`.

### 2. MySQL

Create a database (optional — the app can create it automatically):

```sql
CREATE DATABASE taskdb;
```

Default connection settings are in `application.properties` (`localhost:3306`, database `taskdb`, user `root`).

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Or run `TaskManagementRestApiApplication` from your IDE.

After adding or changing dependencies, **reload the Maven project** and do a **full restart** (not just DevTools hot reload).

The server starts on **port 8080**. Hibernate creates/updates tables automatically (`spring.jpa.hibernate.ddl-auto=update`).

---

## Authentication

The API is **stateless**. Protected routes require a JWT in the header:

```
Authorization: Bearer <accessToken>
```

### Token types

| Token | Lifetime (default) | Purpose |
|-------|-------------------|---------|
| Access token | 15 minutes | Sent on every protected request |
| Refresh token | 7 days | Used to obtain a new token pair; stored in DB and revoked on logout |

### Typical client flow

```
1. POST /api/auth/signup     → create account + receive tokens
2. GET  /api/auth/me         → get your user id
3. POST /api/users/{id}/tasks → create tasks (with Bearer token)
4. POST /api/auth/refresh    → new tokens when access token expires
5. POST /api/auth/logout     → revoke refresh token
```

### Public vs protected routes

| Public (no token) | Protected (Bearer token) |
|-------------------|---------------------------|
| `POST /api/auth/signup` | `GET /api/auth/me` |
| `POST /api/auth/login` | All `/api/users/{userId}/tasks/**` routes |
| `POST /api/auth/refresh` | |
| `POST /api/auth/logout` | |

---

## API Reference

### Auth

#### Sign up

```http
POST /api/auth/signup
Content-Type: application/json
```

```json
{
  "email": "alice@example.com",
  "password": "secret123"
}
```

| Field | Rules |
|-------|-------|
| `email` | Required, valid email |
| `password` | Required, 6–100 characters |

**Response `201 Created`:**

```json
{
  "accessToken": "eyJhbG...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

#### Login

```http
POST /api/auth/login
Content-Type: application/json
```

Same request body as signup. **Response `200 OK`:** same shape as signup.

#### Get current user

```http
GET /api/auth/me
Authorization: Bearer <accessToken>
```

**Response `200 OK`:**

```json
{
  "id": 1,
  "email": "alice@example.com"
}
```

#### Refresh tokens

```http
POST /api/auth/refresh
Content-Type: application/json
```

```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response `200 OK`:** new access + refresh token pair (old refresh token is rotated/revoked).

#### Logout

```http
POST /api/auth/logout
Content-Type: application/json
```

```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response `204 No Content`**

---

### Tasks

All task routes require authentication. Replace `{userId}` with your id from `GET /api/auth/me`.

#### List tasks (paginated)

```http
GET /api/users/{userId}/tasks?page=0&size=10&sort=id,desc
Authorization: Bearer <accessToken>
```

| Query param | Default | Description |
|-------------|---------|-------------|
| `page` | `0` | Page index (0-based) |
| `size` | `10` | Items per page |
| `sort` | `id` | Sort field and direction (e.g. `title,asc`) |

**Response `200 OK`:**

```json
{
  "content": [
    {
      "id": 1,
      "title": "Buy groceries",
      "description": "Milk and eggs",
      "completed": false,
      "userId": 1
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

#### Get task by id

```http
GET /api/users/{userId}/tasks/{taskId}
Authorization: Bearer <accessToken>
```

#### Create task

```http
POST /api/users/{userId}/tasks
Authorization: Bearer <accessToken>
Content-Type: application/json
```

```json
{
  "title": "Buy groceries",
  "description": "Milk and eggs",
  "isCompleted": false
}
```

| Field | Rules |
|-------|-------|
| `title` | Required, 3–100 characters |
| `description` | Required, 3–1000 characters |
| `isCompleted` | Optional, defaults to `false` |

**Response `201 Created`:** task object (same shape as items in the list above).

#### Update task

```http
PUT /api/users/{userId}/tasks/{taskId}
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Same body as create. **Response `200 OK`:** updated task.

#### Delete task

```http
DELETE /api/users/{userId}/tasks/{taskId}
Authorization: Bearer <accessToken>
```

**Response `200 OK`:**

```json
"Task deleted successfully: 1"
```

---

## Error Responses

Errors return a consistent JSON shape:

```json
{
  "status": "BAD_REQUEST",
  "message": "Validation failed",
  "timestamp": "2026-06-07T16:00:00",
  "errors": {
    "title": "Title is required"
  }
}
```

| HTTP Status | When |
|-------------|------|
| `400` | Validation failure, invalid JSON, missing body |
| `401` | Missing/invalid token, bad login, expired refresh token |
| `404` | User or task not found |
| `409` | Email already exists on signup |
| `500` | Unexpected server error |

---

## Database Schema

Hibernate manages these tables:

**users**
| Column | Type | Notes |
|--------|------|-------|
| `id` | BIGINT | Primary key |
| `email` | VARCHAR | Unique, not null |
| `password` | VARCHAR | BCrypt hash, not null |

**tasks**
| Column | Type | Notes |
|--------|------|-------|
| `id` | BIGINT | Primary key |
| `title` | VARCHAR | |
| `description` | VARCHAR | |
| `is_completed` | BOOLEAN | |
| `user_id` | BIGINT | Foreign key → `users.id` |

**refresh_tokens**
| Column | Type | Notes |
|--------|------|-------|
| `id` | BIGINT | Primary key |
| `token` | VARCHAR | Unique UUID |
| `user_id` | BIGINT | Foreign key → `users.id` |
| `expires_at` | TIMESTAMP | |
| `revoked` | BOOLEAN | Set on logout/rotation |

---

## Project Structure

```
src/main/java/org/example/task_management_rest_api/
├── config/           # SecurityConfig, JwtProperties
├── controller/       # AuthController, TaskController (HTTP + DTOs)
├── dto/
│   ├── request/      # SignupRequest, LoginRequest, CreateTaskRequest, ...
│   ├── response/     # AuthResponse, TaskResponse, UserResponse, PagedResponse
│   └── error/        # ApiError
├── exception/        # Custom exceptions + GlobalExceptionHandler
├── model/            # JPA entities (User, Task, RefreshToken)
├── repository/       # Spring Data JPA repositories
├── security/         # JWT filter, JwtService, RefreshTokenService, UserDetailsService
└── service/          # Business logic (AuthService, UserService, TaskService)
```

### Layer responsibilities

| Layer | Responsibility |
|-------|----------------|
| **Controller** | HTTP, status codes, maps DTOs ↔ entities |
| **Service** | Business rules, works with entities |
| **Repository** | Database access |
| **Security** | JWT creation/validation, filter chain, refresh token lifecycle |

---

## Example: Full workflow with cURL

```bash
# 1. Sign up
curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"secret123"}'

# 2. Save accessToken from response, then get your user id
curl -s http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# 3. Create a task (use id from /me as userId)
curl -s -X POST http://localhost:8080/api/users/1/tasks \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy groceries","description":"Milk and eggs","isCompleted":false}'

# 4. List tasks
curl -s "http://localhost:8080/api/users/1/tasks?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## Security Notes

- Passwords are hashed with **BCrypt** before storage; plain passwords are never returned in API responses.
- **Never commit** `application-local.properties` or `.env` files with real secrets.
- Use `application-local.properties.example` as a template for new developers.
- On production hosts, set `DB_PASSWORD`, `JWT_SECRET`, and related env vars in the platform dashboard.
- If secrets were ever pushed to GitHub, rotate the MySQL password and JWT secret immediately.

---

## License

This project is for educational purposes.
