# Quiz Backend API Document

## 1. Overview

This document describes the REST API endpoints for the Quiz Backend system.

The backend is built with Spring Boot and provides APIs for:

- User registration and email verification
- Login with JWT access token
- Refresh token
- Password update
- Exam CRUD
- Question CRUD
- Importing questions from Excel file

## 2. Base URL

```http
http://localhost:8383
```

## 3. Authentication

Most APIs require JWT authentication.

Send the access token in the request header:

```http
Authorization: Bearer {accessToken}
```

Public APIs:

```http
POST /auth/register
POST /auth/login
POST /auth/refresh-token
GET  /auth/verify-email
```

Protected APIs:

```http
/users/**
/exams/**
/exams/{examId}/questions/**
```

---

# 4. Common Response Format

The project uses a common response wrapper.

Example success response:

```json
{
  "code": "SUCCESS",
  "message": "Success",
  "data": {}
}
```

For endpoints that do not return data:

```json
{
  "code": "SUCCESS",
  "message": "Success",
  "data": null
}
```

---

# 5. Authentication APIs

## 5.1 Register

Create a new user account and send an email verification link.

### Endpoint

```http
POST /auth/register
```

### Authentication

Not required.

### Request Body

```json
{
  "name": "Nguyen Van A",
  "email": "user@example.com",
  "password": "123456"
}
```

### Validation Rules

| Field | Type | Required | Rule |
|---|---|---|---|
| name | string | Yes | Not blank |
| email | string | Yes | Must be valid email format |
| password | string | Yes | Minimum 6 characters |

### Success Response

HTTP Status:

```http
201 Created
```

Response body:

```json
{
  "code": "CREATED",
  "message": "Created successfully",
  "data": null
}
```

### Notes

After registration, the system sends a verification email to the user's email address.  
The verification token expires after 15 minutes.

---

## 5.2 Verify Email

Verify a user's email address using the token sent by email.

### Endpoint

```http
GET /auth/verify-email?token={token}
```

### Authentication

Not required.

### Query Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| token | string | Yes | Email verification token |

### Success Behavior

If the token is valid, the backend redirects to:

```http
{BASE_URL_FE}/verify-email/success
```

### Failed Behavior

If the token is invalid or expired, the backend redirects to:

```http
{BASE_URL_FE}/verify-email/failed
```

---

## 5.3 Login

Login with email and password.

### Endpoint

```http
POST /auth/login
```

### Authentication

Not required.

### Request Body

```json
{
  "email": "user@example.com",
  "password": "123456"
}
```

### Validation Rules

| Field | Type | Required | Rule |
|---|---|---|---|
| email | string | Yes | Not blank |
| password | string | Yes | Not blank |

### Success Response

HTTP Status:

```http
200 OK
```

Response body:

```json
{
  "code": "SUCCESS",
  "message": "Success",
  "data": {
    "accessToken": "jwt_access_token",
    "refreshToken": "refresh_token",
    "refreshTokenExpiryDate": "2026-06-26T10:00:00"
  }
}
```

---

## 5.4 Refresh Token

Generate a new access token using a valid refresh token.

### Endpoint

```http
POST /auth/refresh-token
```

### Authentication

Not required.

### Request Body

```json
{
  "refreshToken": "your_refresh_token"
}
```

### Success Response

HTTP Status:

```http
200 OK
```

Response body example:

```json
{
  "code": "SUCCESS",
  "message": "Success",
  "data": {
    "accessToken": "new_jwt_access_token"
  }
}
```

---

# 6. User APIs

## 6.1 Change Password

Change the password of the current authenticated user.

### Endpoint

```http
PUT /users/me/password
```

### Authentication

Required.

### Headers

```http
Authorization: Bearer {accessToken}
```

### Request Body

```json
{
  "oldPassword": "old_password",
  "newPassword": "new_password"
}
```

### Success Response

HTTP Status:

```http
200 OK
```

Response body:

```json
{
  "code": "SUCCESS",
  "message": "Success",
  "data": null
}
```

---

# 7. Exam APIs

All exam APIs require authentication.

## 7.1 Get Current User's Exams

Get all exams that belong to the current authenticated user.

### Endpoint

```http
GET /exams
```

### Authentication

Required.

### Headers

```http
Authorization: Bearer {accessToken}
```

### Success Response

HTTP Status:

```http
200 OK
```

Response body:

```json
{
  "code": "SUCCESS",
  "message": "Success",
  "data": [
    {
      "id": 1,
      "title": "Java Basic Quiz",
      "duration": 30,
      "createdAt": "2026-05-27T10:00:00",
      "updatedAt": null
    }
  ]
}
```

---

## 7.2 Get Exam Detail

Get a specific exam by ID.

### Endpoint

```http
GET /exams/{examId}
```

### Authentication

Required.

### Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| examId | number | Yes | Exam ID |

### Headers

```http
Authorization: Bearer {accessToken}
```

### Success Response

HTTP Status:

```http
200 OK
```

Response body:

```json
{
  "code": "SUCCESS",
  "message": "Success",
  "data": {
    "id": 1,
    "title": "Java Basic Quiz",
    "duration": 30,
    "createdAt": "2026-05-27T10:00:00",
    "updatedAt": null
  }
}
```

---

## 7.3 Create Exam

Create a new exam for the current authenticated user.

### Endpoint

```http
POST /exams
```

### Authentication

Required.

### Headers

```http
Authorization: Bearer {accessToken}
```

### Request Body

```json
{
  "title": "Java Basic Quiz",
  "duration": 30
}
```

### Validation Rules

| Field | Type | Required | Rule |
|---|---|---|---|
| title | string | Yes | Not blank |
| duration | number | Yes | Must be positive |

### Success Response

HTTP Status:

```http
201 Created
```

Response body:

```json
{
  "code": "CREATED",
  "message": "Created successfully",
  "data": {
    "id": 1,
    "title": "Java Basic Quiz",
    "duration": 30,
    "createdAt": "2026-05-27T10:00:00",
    "updatedAt": null
  }
}
```

---

## 7.4 Update Exam

Update an existing exam.

### Endpoint

```http
PUT /exams/{examId}
```

### Authentication

Required.

### Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| examId | number | Yes | Exam ID |

### Headers

```http
Authorization: Bearer {accessToken}
```

### Request Body

```json
{
  "title": "Updated Java Quiz",
  "duration": 45
}
```

### Validation Rules

| Field | Type | Required | Rule |
|---|---|---|---|
| title | string | Yes | Not blank |
| duration | number | Yes | Must be positive |

### Success Response

HTTP Status:

```http
200 OK
```

Response body:

```json
{
  "code": "UPDATED",
  "message": "Updated successfully",
  "data": {
    "id": 1,
    "title": "Updated Java Quiz",
    "duration": 45,
    "createdAt": "2026-05-27T10:00:00",
    "updatedAt": "2026-05-27T11:00:00"
  }
}
```

---

## 7.5 Delete Exam

Delete an existing exam.

### Endpoint

```http
DELETE /exams/{examId}
```

### Authentication

Required.

### Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| examId | number | Yes | Exam ID |

### Headers

```http
Authorization: Bearer {accessToken}
```

### Success Response

HTTP Status:

```http
200 OK
```

Response body:

```json
{
  "code": "DELETED",
  "message": "Deleted successfully",
  "data": null
}
```

---

# 8. Question APIs

All question APIs require authentication.

## 8.1 Create Question

Create a new question for an exam.

### Endpoint

```http
POST /exams/{examId}/questions
```

### Authentication

Required.

### Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| examId | number | Yes | Exam ID |

### Headers

```http
Authorization: Bearer {accessToken}
```

### Request Body

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

### Validation Rules

| Field | Type | Required | Rule |
|---|---|---|---|
| questionText | string | Yes | Not blank |
| answer1 | string | Yes | Not blank |
| answer2 | string | Yes | Not blank |
| answer3 | string | Yes | Not blank |
| answer4 | string | Yes | Not blank |
| correctAnswer | number | Yes | Min 1, max 4 |

### Success Response

HTTP Status:

```http
201 Created
```

Response body:

```json
{
  "code": "SUCCESS",
  "message": "Success",
  "data": {
    "id": 1,
    "questionText": "Java is a programming language?",
    "answer1": "True",
    "answer2": "False",
    "answer3": "Maybe",
    "answer4": "None",
    "correctAnswer": 1
  }
}
```

---

## 8.2 Import Questions From Excel

Import multiple questions from an Excel file.

### Endpoint

```http
POST /exams/{examId}/questions/import
```

### Authentication

Required.

### Content-Type

```http
multipart/form-data
```

### Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| examId | number | Yes | Exam ID |

### Headers

```http
Authorization: Bearer {accessToken}
```

### Form Data

| Field | Type | Required | Description |
|---|---|---|---|
| file | file | Yes | Excel `.xlsx` file |

### Excel Format

The first row is treated as the header row.  
Question data starts from the second row.

| Column A | Column B | Column C | Column D | Column E | Column F |
|---|---|---|---|---|---|
| Question Text | Answer 1 | Answer 2 | Answer 3 | Answer 4 | Correct Answer |

Example:

| Question Text | Answer 1 | Answer 2 | Answer 3 | Answer 4 | Correct Answer |
|---|---|---|---|---|---|
| What is Java? | Programming language | Database | Operating system | Browser | 1 |
| Which keyword creates inheritance in Java? | this | super | extends | implements | 3 |

### Import Rules

- File must not be empty.
- Each row must have all required cells.
- `Correct Answer` must be a number from `1` to `4`.
- If any row is invalid, the API returns a bad request error.

### Success Response

HTTP Status:

```http
201 Created
```

Response body:

```json
{
  "code": "SUCCESS",
  "message": "Success",
  "data": [
    {
      "id": 1,
      "questionText": "What is Java?",
      "answer1": "Programming language",
      "answer2": "Database",
      "answer3": "Operating system",
      "answer4": "Browser",
      "correctAnswer": 1
    },
    {
      "id": 2,
      "questionText": "Which keyword creates inheritance in Java?",
      "answer1": "this",
      "answer2": "super",
      "answer3": "extends",
      "answer4": "implements",
      "correctAnswer": 3
    }
  ]
}
```

---

## 8.3 Update Question

Update an existing question in an exam.

### Endpoint

```http
PUT /exams/{examId}/questions/{questionId}
```

### Authentication

Required.

### Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| examId | number | Yes | Exam ID |
| questionId | number | Yes | Question ID |

### Headers

```http
Authorization: Bearer {accessToken}
```

### Request Body

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

### Validation Rules

| Field | Type | Required | Rule |
|---|---|---|---|
| questionText | string | Yes | Not blank |
| answer1 | string | Yes | Not blank |
| answer2 | string | Yes | Not blank |
| answer3 | string | Yes | Not blank |
| answer4 | string | Yes | Not blank |
| correctAnswer | number | Yes | Min 1, max 4 |

### Success Response

HTTP Status:

```http
200 OK
```

Response body:

```json
{
  "code": "UPDATED",
  "message": "Updated successfully",
  "data": {
    "id": 1,
    "questionText": "Updated question?",
    "answer1": "Option A",
    "answer2": "Option B",
    "answer3": "Option C",
    "answer4": "Option D",
    "correctAnswer": 2
  }
}
```

---

## 8.4 Delete Question

Delete a question from an exam.

### Endpoint

```http
DELETE /exams/{examId}/questions/{questionId}
```

### Authentication

Required.

### Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| examId | number | Yes | Exam ID |
| questionId | number | Yes | Question ID |

### Headers

```http
Authorization: Bearer {accessToken}
```

### Success Response

HTTP Status:

```http
200 OK
```

Response body:

```json
{
  "code": "DELETED",
  "message": "Deleted successfully",
  "data": null
}
```

---

# 9. Status Codes

| HTTP Status | Meaning |
|---|---|
| 200 OK | Request successful |
| 201 Created | Resource created successfully |
| 400 Bad Request | Invalid request data |
| 401 Unauthorized | Missing or invalid authentication token |
| 403 Forbidden | User does not have permission |
| 404 Not Found | Resource not found |
| 500 Internal Server Error | Unexpected server error |

---

# 10. Environment Configuration

Required environment variables:

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

---

# 11. Postman Testing Flow

Recommended testing order:

1. Register a new account.
2. Open the verification link from email.
3. Login with verified email and password.
4. Copy the `accessToken`.
5. Add this header to protected requests:

```http
Authorization: Bearer {accessToken}
```

6. Create an exam.
7. Create questions manually or import questions from Excel.
8. Update or delete exams/questions.

---

# 12. Notes

- Email verification token expires after 15 minutes.
- Access token expiration is configured by `JWT_ACCESS_TOKEN_EXPIRATION`.
- Refresh token expiration is configured by `REFRESH_TOKEN_EXPIRATION_DAYS`.
- Users can only access and manage their own exams and questions.
- The backend runs on port `8383` by default.
