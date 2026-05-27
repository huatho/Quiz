# Quiz Backend API

Backend API for a Quiz Management System built with Spring Boot.  
The system supports user authentication, email verification, JWT-based authorization, exam management, question management, and Excel import for questions.

## Tech Stack

- Java 17
- Spring Boot 3.5.13
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- Spring Mail
- Apache POI
- Lombok
- Docker & Docker Compose

## Main Features

### Authentication

- Register new account
- Send email verification link
- Verify account by email token
- Login with email and password
- Generate access token and refresh token
- Refresh access token

### User

- Change current user's password

### Exam

- Create exam
- Get all exams of current authenticated user
- Get exam detail by ID
- Update exam
- Delete exam

### Question

- Create question manually
- Import questions from Excel file
- Update question
- Delete question

## Project Structure

```bash
src/main/java/com/example/demo
├── common
│   ├── exception
│   ├── response
│   └── utils
├── controller
│   ├── AuthController.java
│   ├── ExamController.java
│   ├── QuestionController.java
│   └── UserController.java
├── dto
├── entity
├── mapper
├── repo
├── security
├── service
└── ProjectApplication.java
```

## Database Entities

### User

Stores account information.

Main fields:

- `id`
- `name`
- `email`
- `password`
- `role`
- `active`
- `exams`
- `refreshTokens`

### Exam

Stores exam information.

Main fields:

- `id`
- `title`
- `duration`
- `createdAt`
- `updatedAt`
- `user`
- `questions`

### Question

Stores question information.

Main fields:

- `id`
- `questionText`
- `answer1`
- `answer2`
- `answer3`
- `answer4`
- `correctAnswer`
- `exam`

### RefreshToken

Stores refresh token data.

Main fields:

- `id`
- `token`
- `expiryDate`
- `isRevoked`
- `user`

### EmailVerificationToken

Stores email verification token data.

Main fields:

- `id`
- `token`
- `expiredAt`
- `user`

## API Endpoints

Base URL:

```bash
http://localhost:8383
```

---

## Authentication API

### Register

```http
POST /auth/register
```

Request body:

```json
{
  "name": "Nguyen Van A",
  "email": "user@example.com",
  "password": "123456"
}
```

After registration, the system sends an email verification link to the user's email.

---

### Verify Email

```http
GET /auth/verify-email?token={token}
```

If verification succeeds, the system redirects to:

```bash
{BASE_URL_FE}/verify-email/success
```

If verification fails, the system redirects to:

```bash
{BASE_URL_FE}/verify-email/failed
```

---

### Login

```http
POST /auth/login
```

Request body:

```json
{
  "email": "user@example.com",
  "password": "123456"
}
```

Response data:

```json
{
  "accessToken": "jwt_access_token",
  "refreshToken": "refresh_token",
  "refreshTokenExpiryDate": "2026-06-26T10:00:00"
}
```

---

### Refresh Token

```http
POST /auth/refresh-token
```

Request body:

```json
{
  "refreshToken": "your_refresh_token"
}
```

---

## User API

All `/users/**` endpoints require authentication.

### Change Password

```http
PUT /users/me/password
```

Headers:

```http
Authorization: Bearer {accessToken}
```

Request body:

```json
{
  "oldPassword": "old_password",
  "newPassword": "new_password"
}
```

---

## Exam API

All `/exams/**` endpoints require authentication.

Headers:

```http
Authorization: Bearer {accessToken}
```

### Get Current User's Exams

```http
GET /exams
```

---

### Get Exam Detail

```http
GET /exams/{examId}
```

---

### Create Exam

```http
POST /exams
```

Request body:

```json
{
  "title": "Java Basic Quiz",
  "duration": 30
}
```

---

### Update Exam

```http
PUT /exams/{examId}
```

Request body:

```json
{
  "title": "Updated Java Quiz",
  "duration": 45
}
```

---

### Delete Exam

```http
DELETE /exams/{examId}
```

---

## Question API

All question APIs require authentication.

Headers:

```http
Authorization: Bearer {accessToken}
```

### Create Question

```http
POST /exams/{examId}/questions
```

Request body:

```json
{
  "questionText": "Java is a programming language?",
  "answer1": "True",
  "answer2": "False",
  "answer3": "Maybe",
  "answer4": "None",
  "correctAnswer": 1
}
```

`correctAnswer` must be from `1` to `4`.

---

### Import Questions From Excel

```http
POST /exams/{examId}/questions/import
```

Content-Type:

```http
multipart/form-data
```

Form data:

```bash
file: questions.xlsx
```

Excel format:

| Question Text | Answer 1 | Answer 2 | Answer 3 | Answer 4 | Correct Answer |
|--------------|----------|----------|----------|----------|----------------|
| What is Java? | Language | Database | OS | Browser | 1 |

Rules:

- The first row is treated as the header row.
- Data starts from row 2.
- `Correct Answer` must be a number from `1` to `4`.
- Empty required cells will cause a bad request error.

---

### Update Question

```http
PUT /exams/{examId}/questions/{questionId}
```

Request body:

```json
{
  "questionText": "Updated question?",
  "answer1": "Option A",
  "answer2": "Option B",
  "answer3": "Option C",
  "answer4": "Option D",
  "correctAnswer": 2
}
```

---

### Delete Question

```http
DELETE /exams/{examId}/questions/{questionId}
```

---

## Security

This project uses stateless JWT authentication.

Public endpoints:

```bash
/auth/**
```

Protected endpoints:

```bash
/exams/**
/users/**
```

The access token must be sent in the `Authorization` header:

```http
Authorization: Bearer {accessToken}
```

## Environment Variables

Create environment variables before running the application:

```bash
JWT_SECRET=your_jwt_secret_key
JWT_ACCESS_TOKEN_EXPIRATION=900000
REFRESH_TOKEN_EXPIRATION_DAYS=30

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

BASE_URL_FE=http://localhost:3000
BASE_URL_BE=http://localhost:8383
```

Database configuration:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/project_03302026
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_database_password
```

> Note: Do not commit real passwords or secrets to GitHub. Use environment variables instead.

## Run Locally

### 1. Clone repository

```bash
git clone https://github.com/huatho/Quiz.git
cd Quiz
```

### 2. Create PostgreSQL database

Create a database named:

```bash
project_03302026
```

### 3. Configure environment variables

Make sure the required variables are available in your system or IDE.

### 4. Run application

On Linux/macOS:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The server will run at:

```bash
http://localhost:8383
```

## Run With Docker

Build and start the application with PostgreSQL:

```bash
docker compose up --build
```

Stop containers:

```bash
docker compose down
```

Stop containers and remove database volume:

```bash
docker compose down -v
```

## Build Project

```bash
./mvnw clean package
```

The built JAR file will be generated in:

```bash
target/
```

## CORS

The backend currently allows requests from:

```bash
http://localhost:3000
```

This is suitable for a frontend development server such as React or Next.js.

## Notes

- Email verification token expires after 15 minutes.
- Refresh token expiration is configurable by `REFRESH_TOKEN_EXPIRATION_DAYS`.
- Access token expiration is configurable by `JWT_ACCESS_TOKEN_EXPIRATION`.
- Exams and questions are scoped by the authenticated user, so users can only manage their own exams.
