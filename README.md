# Attendance System API Documentation

## Overview

This API provides endpoints for user management, authentication, and password
management in an attendance tracking system. It supports different user roles
(Admin, Facilitator, and User) with varying levels of permissions.


## Authentication

The API uses JWT (JSON Web Token) for authentication. After successful login,
include the token in the Authorization header for protected endpoints:

Authorization: Bearer {your_jwt_token}


## Endpoints

### Authentication and User Management

#### Create Admin User

**Method:** `POST api/auth/create-admin`

Creates a new admin user in the system.

**Request Body:**

```json
{
  "firstName": "John",
  "middleName": "Michael",
  "lastName": "Doe",
  "email": "admin@example.com"
}
```
**Response:**

- Status: 200 OK
- Body: "User created successfully"

**Notes:**
- A temporary password will be generated and sent to the provided email.
- The user will be required to reset their password on first login.

#### User Login

**Method:** `POST api/auth/login`

Authenticates a user and returns a JWT token.

Request Body:

```json
{
  "email": "user@example.com",
  "password": "yourpassword"
}
```
Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "passwordResetRequired": false,
  "role": "ADMIN"
}
```
#### Request Password Reset

**Method:** `POST api/auth/reset-password-request?email=user@example.com`

Initiates the password reset process by sending a reset token to the user's email.

**Query Parameters:**
- email: The email address of the user requesting password reset

**Response:**
- Status: 200 OK
- Body: "Password reset code sent to your email address"

#### Reset Password

**Method:** `POST api/auth/reset-password?email=user@example.com&token=reset_token_here`

Resets a user's password using the token sent to their email.

**Query Parameters:**
- email: The email address of the user
- token: The reset token received via email

**Request Body:**


```json
{
  "password": "NewP@ssword123",
  "confirmPassword": "NewP@ssword123"
}
```
**Response:**
- Status: 200 OK
- Body: "Password reset successful"

#### First-time Password Reset

**Method:** `POST api/auth/first-password-reset?email=user@example.com`

Allows users to reset their temporary password after first login.

**Query Parameters:**
- email: The email address of the user

**Request Body:**

```json
{
  "password": "NewP@ssword123",
  "confirmPassword": "NewP@ssword123"
}
```
**Response:**
- Status: 200 OK
- Body: "Password reset successful"

### Admin Operations

These endpoints require admin privileges.

#### Register Regular User

**Method:** `POST api/admin/create-nsp`

Creates a new regular user.

**Request Body:**

```json
{
  "firstName": "Jane",
  "middleName": "Marie",
  "lastName": "Smith",
  "email": "jane.smith@example.com"
}
```
**Response:**
- Status: 200 OK
- Body: "User registered successfully"

#### Create Facilitator

**Method:** `POST api/admin/create-facilitator`

Creates a new facilitator user.

**Request Body:**


```json
{
  "firstName": "Robert",
  "middleName": "James",
  "lastName": "Johnson",
  "email": "robert.johnson@example.com"
}
```
**Response:**
- Status: 200 OK
- Body: "User created successfully"

#### Update User

**Method:** `PUT api/admin/users/{userId}`

Updates a user's information.

**Path Parameters:**
- userId: The ID of the user to update

**Request Body:**

```json
{
  "firstName": "Jane",
  "middleName": "Marie",
  "lastName": "Smith-Johnson",
  "email": "jane.smith@example.com"
}
```
Response:

```json
{
  "id": 1,
  "firstName": "Jane",
  "middleName": "Marie",
  "lastName": "Smith-Johnson",
  "email": "jane.smith@example.com",
  "passwordResetRequired": false,
  "createdAt": "2023-05-15",
  "role": "USER"
}
```
#### Delete User

**Method:** `DELETE api/admin/users/{userId}`

Deletes a user from the system.

**Path Parameters:**
- userId: The ID of the user to delete

**Response:**
- Status: 200 OK
- Body: "User deleted successfully"

## Error Responses

- **404 Not Found:** "User not found"
- **403 Forbidden:** "You are not authorized to perform this action"

## Data Validation

### User Registration/Creation
- **firstName:** Required, 3-50 characters
- **lastName:** Required, 3-50 characters
- **email:** Required, must be a valid email format

### Password Reset
- **password:** Required, minimum 8 characters, must contain at least one special character
- **confirmPassword:** Required, must match password

## Error Handling

The API returns appropriate HTTP status codes and error messages:

| Status Code | Description                                      |
| ----------- | ------------------------------------------------ |
| 400         | Bad Request - Validation errors or invalid input |
| 401         | Unauthorized - Authentication failure            |
| 403         | Forbidden - Insufficient permissions             |
| 404         | Not Found - Resource not found                   |
| 500         | Internal Server Error - Server-side errors       |

For validation errors, the response will include specific error messages for each invalid field.

## Notes

- New users are created with a temporary password that must be reset on first login.
- Password reset tokens have a limited validity period.
- All endpoints that modify data require authentication.
- Admin operations require specific admin privileges.


