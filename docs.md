
# Ballotguard API Documentation (Comprehensive)

This documentation details every endpoint, its flow, request/response structure, and **all possible success and error conditions**, based on a full review of all controllers, services, and utilities.

---

## Authentication & User Endpoints

### Sign Up
**Endpoint:**
`POST /api/v1/auth/signup`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "yourPassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Flow:**
User provides info and signs up.

**Success Conditions:**
- All required fields are non-empty and valid.
- Email is unique and valid format.
- Password is 8-50 characters.
- User is created and saved successfully.

**Response JSON Structures:**

**200 OK - User created successfully:**
```json
{
  "success": true,
  "message": "User successfully created",
  "userInfo": {
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "isVerified": false,
    "isAccountEnabled": true
  },
  "jwt": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**400 BAD REQUEST - Empty fields:**
```json
{
  "success": false,
  "message": "Email, password or first name is empty"
}
```

**400 BAD REQUEST - Invalid email format:**
```json
{
  "success": false,
  "message": "This email address is not valid"
}
```

**400 BAD REQUEST - Invalid password length:**
```json
{
  "success": false,
  "message": "Password must be between 8 and 50 characters"
}
```

**409 CONFLICT - User already exists:**
```json
{
  "success": false,
  "message": "Another user with this email already exists"
}
```

**500 INTERNAL SERVER ERROR - User creation failed:**
```json
{
  "success": false,
  "message": "User creation failed, please try again"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while creating new user"
}
```

---

### Login
**Endpoint:**
`POST /api/v1/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "yourPassword123"
}
```

**Flow:**
User provides info and logs in.

**Response JSON Structures:**

**200 OK - Login successful:**
```json
{
  "success": true,
  "message": "Login successful",
  "userInfo": {
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "isVerified": true,
    "isAccountEnabled": true
  },
  "jwt": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**400 BAD REQUEST - Empty fields:**
```json
{
  "success": false,
  "message": "Email or password is empty"
}
```

**400 BAD REQUEST - Invalid email format:**
```json
{
  "success": false,
  "message": "This email address is not valid"
}
```

**401 UNAUTHORIZED - Incorrect credentials:**
```json
{
  "success": false,
  "message": "Email or password is incorrect"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while logging user in"
}
```

---

### Email Verification - Send Code
**Endpoint:**
`POST /api/v1/auth/email-verification/code`

**Flow:**
Authenticated user requests a verification code to be sent to their email.

**Response JSON Structures:**

**200 OK - Verification code sent:**
```json
{
  "success": true,
  "message": "Email verification code sent"
}
```

**406 NOT ACCEPTABLE - User already verified:**
```json
{
  "success": false,
  "message": "This user is already verified"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while sending verification code"
}
```

---

### Email Verification - Verify Code
**Endpoint:**
`POST /api/v1/auth/email-verification/verify`

**Request Body:**
```json
{
  "verificationCode": "123456"
}
```

**Flow:**
User provides code from email to verify.

**Response JSON Structures:**

**200 OK - Verification successful:**
```json
{
  "success": true,
  "message": "Email verification code verified. User is now verified"
}
```

**412 PRECONDITION FAILED - No code requested:**
```json
{
  "success": false,
  "message": "No verification code was requested"
}
```

**406 NOT ACCEPTABLE - Incorrect or expired code:**
```json
{
  "success": false,
  "message": "Verification code is incorrect or expired"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while verifying verification code"
}
```

---

### Forgot Password - Send Code
**Endpoint:**
`POST /api/v1/auth/password-reset/code`

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Flow:**
User requests password reset code.

**Response JSON Structures:**

**200 OK - Code sent:**
```json
{
  "success": true,
  "message": "Forgot password verification code sent"
}
```

**412 PRECONDITION FAILED - User does not exist:**
```json
{
  "success": false,
  "message": "User does not exist"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while sending password reset verification code"
}
```

---

### Forgot Password - Verify and Reset
**Endpoint:**
`POST /api/v1/auth/password-reset/verify-and-reset`

**Request Body:**
```json
{
  "email": "user@example.com",
  "verificationCode": "123456",
  "newPassword": "yourNewPassword123"
}
```

**Flow:**
User provides email, code, and new password to reset.

**Response JSON Structures:**

**200 OK - Password reset successful:**
```json
{
  "success": true,
  "message": "Forgot password code was verified and password reset is successful",
  "userInfo": {
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "isVerified": true,
    "isAccountEnabled": true
  },
  "jwt": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**400 BAD REQUEST - Invalid password length:**
```json
{
  "success": false,
  "message": "Password must be between 8 and 50 characters"
}
```

**406 NOT ACCEPTABLE - Incorrect or expired code:**
```json
{
  "success": false,
  "message": "Verification code is incorrect or expired"
}
```

**412 PRECONDITION FAILED - User does not exist:**
```json
{
  "success": false,
  "message": "User does not exist"
}
```

**412 PRECONDITION FAILED - No code requested:**
```json
{
  "success": false,
  "message": "No verification code was requested"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while verifying password reset verification code"
}
```

---

### Get User Info
**Endpoint:**
`GET /api/v1/user`

**Flow:**
Authenticated user fetches their info.

**Response JSON Structures:**

**200 OK - User info returned:**
```json
{
  "success": true,
  "message": "User found",
  "userInfo": {
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "isVerified": true,
    "isAccountEnabled": true
  }
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while fetching user"
}
```

---

### Delete User
**Endpoint:**
`DELETE /api/v1/user/delete`

**Flow:**
Authenticated user deletes their account.

**Response JSON Structures:**

**200 OK - User deleted successfully:**
```json
{
  "success": true,
  "message": "User is successfully deleted"
}
```

**500 INTERNAL SERVER ERROR - Partial or unsuccessful deletion:**
```json
{
  "success": false,
  "message": "User deletion has concluded partial or unsuccessful"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while deleting user"
}
```

---

### Reset Password (Logged In)
**Endpoint:**
`PUT /api/v1/user/password-reset`

**Request Body:**
```json
{
  "oldPassword": "oldPass",
  "newPassword": "newPass"
}
```

**Flow:**
User provides old and new password to reset.

**Response JSON Structures:**

**200 OK - Password reset successful:**
```json
{
  "success": true,
  "message": "Password reset successful"
}
```

**400 BAD REQUEST - Previous password did not match:**
```json
{
  "success": false,
  "message": "Previous password did not match"
}
```

**400 BAD REQUEST - Invalid password length:**
```json
{
  "success": false,
  "message": "Password must be between 8 and 50 characters"
}
```

**400 BAD REQUEST - New password same as old:**
```json
{
  "success": false,
  "message": "New password cannot be the same as the old password"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while resetting password"
}
```

---

### Get User Settings
**Endpoint:**
`GET /api/v1/user/settings`

**Flow:**
Authenticated user fetches their settings preferences.

**Response JSON Structures:**

**200 OK - User settings found:**
```json
{
  "success": true,
  "message": "User settings found",
  "userSettings": {
    "id": "uuid-here",
    "userEmail": "user@example.com",
    "preferredLanguage": "English",
    "preferredTheme": "Light",
    "pushNotificationsEnabled": true,
    "emailNotificationsEnabled": true
  }
}
```

**400 BAD REQUEST - User settings ID is null or empty:**
```json
{
  "success": false,
  "message": "Bad Request"
}
```

**404 NOT FOUND - User settings not found:**
```json
{
  "success": false,
  "message": "User settings not found"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while fetching user settings"
}
```

---

### Update User Settings
**Endpoint:**
`PUT /api/v1/user/settings`

**Request Body:**
```json
{
  "userEmail": "user@example.com",
  "preferredLanguage": "Spanish",
  "preferredTheme": "Dark",
  "pushNotificationsEnabled": false,
  "emailNotificationsEnabled": true
}
```

**Flow:**
Authenticated user updates their settings preferences.

**Response JSON Structures:**

**200 OK - User settings updated successfully:**
```json
{
  "success": true,
  "message": "User settings updated",
  "userSettings": {
    "id": "uuid-here",
    "userEmail": "user@example.com",
    "preferredLanguage": "Spanish",
    "preferredTheme": "Dark",
    "pushNotificationsEnabled": false,
    "emailNotificationsEnabled": true
  }
}
```

**400 BAD REQUEST - User ID or settings entity is null/empty:**
```json
{
  "success": false,
  "message": "Bad Request"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while updating user settings"
}
```

---
## Token Endpoints

### Refresh Token
**Endpoint:**
`POST /api/v1/auth/refresh`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response JSON Structures:**

**200 OK - New JWT generated:**
```json
{
  "success": true,
  "message": "New JWT generated",
  "jwt": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**401 UNAUTHORIZED - Invalid or expired refresh token:**
```json
{
  "success": false,
  "message": "Refresh token is invalid or expired"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while refreshing JWT"
}
```

### Token Verification
**Endpoint:**
`POST /api/v1/auth/token-verification`

**Request Body:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response JSON Structures:**

**200 OK - Valid JWT:**
```json
{
  "success": true,
  "message": "This a valid JWT"
}
```

**200 OK - Valid Refresh Token:**
```json
{
  "success": true,
  "message": "This a valid Refresh Token"
}
```

**401 UNAUTHORIZED - Invalid or expired token:**
```json
{
  "success": false,
  "message": "This token is invalid or expired"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while verifying the token"
}
```

---

## Health Check

### Health Check
**Endpoint:**
`GET /api/v1/public/health-check`

**Response JSON Structures:**

**200 OK - API is up and running:**
```json
{
  "success": true,
  "message": "API is up and running"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred"
}
```

---

## Election Endpoints

### Create Election
**Endpoint:**
`POST /api/v1/user/election/create`

**Request Body:**
```json
{
  "electionName": "My Election",
  "electionDescription": "Description of the election",
  "startTime": 1640995200000,
  "endTime": 1641081600000,
  "electionLayout": {
    "pollType": "radio",
    "electionCardId": "1"
  },
  "options": [
    {
      "optionName": "Option 1"
    },
    {
      "optionName": "Option 2"
    }
  ],
  "voters": [
    {
      "voterEmail": "voter1@example.com"
    },
    {
      "voterEmail": "voter2@example.com"
    }
  ]
}
```

**Flow:**
User creates a new election.

**Response JSON Structures:**

**201 CREATED - Election created successfully:**
```json
{
  "success": true,
  "message": "Election created successfully",
  "electionInfo": {
    "electionId": "uuid-here",
    "electionName": "My Election",
    "electionDescription": "Description of the election",
    "startTime": 1640995200000,
    "endTime": 1641081600000,
    "electionLayout": {
      "pollType": "radio",
      "electionCardId": "1"
    },
    "options": [
      {
        "optionName": "Option 1",
        "optionId": "uuid-here"
      },
      {
        "optionName": "Option 2",
        "optionId": "uuid-here"
      }
    ],
    "voters": [
      {
        "voterEmail": "voter1@example.com"
      },
      {
        "voterEmail": "voter2@example.com"
      }
    ]
  }
}
```

**406 NOT ACCEPTABLE - User not verified:**
```json
{
  "success": false,
  "message": "User is not verified"
}
```

**406 NOT ACCEPTABLE - Election name empty:**
```json
{
  "success": false,
  "message": "Election name cannot be empty"
}
```

**406 NOT ACCEPTABLE - Election name too long:**
```json
{
  "success": false,
  "message": "Election name cannot be more than 30 characters"
}
```

**406 NOT ACCEPTABLE - Card ID empty:**
```json
{
  "success": false,
  "message": "Election card id cannot be empty"
}
```

**406 NOT ACCEPTABLE - Poll type invalid:**
```json
{
  "success": false,
  "message": "Poll type is invalid"
}
```

**406 NOT ACCEPTABLE - Option name empty:**
```json
{
  "success": false,
  "message": "Option name cannot be empty"
}
```

**406 NOT ACCEPTABLE - Option name too long:**
```json
{
  "success": false,
  "message": "Option name cannot be more than 30 characters"
}
```

**406 NOT ACCEPTABLE - Voter email empty:**
```json
{
  "success": false,
  "message": "Voter email cannot be empty"
}
```

**406 NOT ACCEPTABLE - Invalid voter email:**
```json
{
  "success": false,
  "message": "Voter email is not valid"
}
```

**500 INTERNAL SERVER ERROR - Election could not be created:**
```json
{
  "success": false,
  "message": "Election could not be created"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while creating election"
}
```

### Delete Election
**Endpoint:**
`DELETE /api/v1/user/election/delete`

**Request Body:**
```json
{
  "electionId": "uuid-here"
}
```

**Flow:**
User deletes an election.

**Response JSON Structures:**

**200 OK - Election deleted successfully:**
```json
{
  "success": true,
  "message": "Election deleted successfully"
}
```

**404 NOT FOUND - Election not found:**
```json
{
  "success": false,
  "message": "Election not found in database"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**412 PRECONDITION FAILED - Election ID empty:**
```json
{
  "success": false,
  "message": "Election id is empty"
}
```

**412 PRECONDITION FAILED - User not verified:**
```json
{
  "success": false,
  "message": "User is not verified"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while deleting election."
}
```

### Find Election
**Endpoint:**
`GET /api/v1/user/election/find`

**Request Body:**
```json
{
  "electionId": "uuid-here"
}
```

**Flow:**
User fetches election details by id.

**Response JSON Structures:**

**200 OK - Election found:**
```json
{
  "success": true,
  "message": "Election found",
  "electionInfo": {
    "electionId": "uuid-here",
    "electionName": "My Election",
    "electionDescription": "Description of the election",
    "startTime": 1640995200000,
    "endTime": 1641081600000,
    "electionLayout": {
      "pollType": "radio",
      "electionCardId": "1"
    },
    "options": [
      {
        "optionName": "Option 1",
        "optionId": "uuid-here"
      },
      {
        "optionName": "Option 2",
        "optionId": "uuid-here"
      }
    ],
    "voters": [
      {
        "voterEmail": "voter1@example.com"
      },
      {
        "voterEmail": "voter2@example.com"
      }
    ]
  }
}
```

**404 NOT FOUND - Election not found:**
```json
{
  "success": false,
  "message": "Election not found in database"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**401 UNAUTHORIZED - Election not owned by user:**
```json
{
  "success": false,
  "message": "This election is not owned by current user"
}
```

**412 PRECONDITION FAILED - User not verified:**
```json
{
  "success": false,
  "message": "User is not verified"
}
```

**412 PRECONDITION FAILED - Election ID empty:**
```json
{
  "success": false,
  "message": "Election id is empty"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while fetching election details."
}
```

### Find All Elections
**Endpoint:**
`GET /api/v1/user/election/find-all`

**Flow:**
User fetches all their elections.

**Response JSON Structures:**

**200 OK - List of elections found:**
```json
{
  "success": true,
  "message": "All elections found",
  "elections": [
    {
      "electionId": "uuid-here",
      "electionName": "My Election 1",
      "electionDescription": "Description of the election",
      "startTime": 1640995200000,
      "endTime": 1641081600000,
      "electionLayout": {
        "pollType": "radio",
        "electionCardId": "1"
      },
      "options": [
        {
          "optionName": "Option 1",
          "optionId": "uuid-here"
        }
      ],
      "voters": [
        {
          "voterEmail": "voter1@example.com"
        }
      ]
    },
    {
      "electionId": "uuid-here-2",
      "electionName": "My Election 2",
      "electionDescription": "Description of the second election",
      "startTime": 1640995200000,
      "endTime": 1641081600000,
      "electionLayout": {
        "pollType": "checkbox",
        "electionCardId": "2"
      },
      "options": [
        {
          "optionName": "Option A",
          "optionId": "uuid-here"
        }
      ],
      "voters": [
        {
          "voterEmail": "voter2@example.com"
        }
      ]
    }
  ]
}
```

**412 PRECONDITION FAILED - User not verified:**
```json
{
  "success": false,
  "message": "User is not verified"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while fetching all election info."
}
```

### Update Election Name
**Endpoint:**
`PATCH /api/v1/user/election/update/name`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "newElectionName": "Updated Election Name"
}
```

**Response JSON Structures:**

**200 OK - Name updated successfully:**
```json
{
  "success": true,
  "message": "Election name updated successfully"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**412 PRECONDITION FAILED - Empty fields:**
```json
{
  "success": false,
  "message": "Election id or election name or both is empty"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while changing the election name"
}
```

### Update Election Description
**Endpoint:**
`PATCH /api/v1/user/election/update/description`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "newElectionDescription": "Updated description"
}
```

**Response JSON Structures:**

**200 OK - Description updated successfully:**
```json
{
  "success": true,
  "message": "Election description updated successfully"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**412 PRECONDITION FAILED - Empty fields:**
```json
{
  "success": false,
  "message": "Election id or election description or both is empty"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while changing the election description"
}
```

### Update Election Poll Type
**Endpoint:**
`PATCH /api/v1/user/election/update/poll-type`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "newPollType": "radio"
}
```

**Response JSON Structures:**

**200 OK - Poll type updated successfully:**
```json
{
  "success": true,
  "message": "Election poll type updated successfully"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**412 PRECONDITION FAILED - Empty fields:**
```json
{
  "success": false,
  "message": "Election id or poll type or both is empty"
}
```

**412 PRECONDITION FAILED - Invalid poll type:**
```json
{
  "success": false,
  "message": "Poll type is invalid"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while changing the election poll type"
}
```

### Update Election Card ID
**Endpoint:**
`PATCH /api/v1/user/election/update/card-id`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "newElectionCardId": "2"
}
```

**Response JSON Structures:**

**200 OK - Card ID updated successfully:**
```json
{
  "success": true,
  "message": "Election card id updated successfully"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**412 PRECONDITION FAILED - Empty fields:**
```json
{
  "success": false,
  "message": "Election id or election card id or both is empty"
}
```

**412 PRECONDITION FAILED - Invalid card ID:**
```json
{
  "success": false,
  "message": "Election card id is invalid"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while changing the election card id"
}
```

### Update Election Start Time
**Endpoint:**
`PATCH /api/v1/user/election/update/start-time`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "newStartTime": 1640995200000
}
```

**Response JSON Structures:**

**200 OK - Start time updated successfully:**
```json
{
  "success": true,
  "message": "Election start time updated successfully"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**412 PRECONDITION FAILED - Empty election ID:**
```json
{
  "success": false,
  "message": "Election id is empty"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while updating election start time"
}
```

### Update Election End Time
**Endpoint:**
`PATCH /api/v1/user/election/update/end-time`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "newEndTime": 1641081600000
}
```

**Response JSON Structures:**

**200 OK - End time updated successfully:**
```json
{
  "success": true,
  "message": "Election end time updated successfully"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**412 PRECONDITION FAILED - Empty election ID:**
```json
{
  "success": false,
  "message": "Election id is empty"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while updating voters"
}
```

### Update Election Voters
**Endpoint:**
`PATCH /api/v1/user/election/update/voters`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "newVoters": [
    {
      "voterEmail": "voter1@example.com"
    },
    {
      "voterEmail": "voter2@example.com"
    }
  ]
}
```

**Response JSON Structures:**

**200 OK - Voters updated successfully:**
```json
{
  "success": true,
  "message": "Election voters updated successfully"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**403 FORBIDDEN - Too close to start time:**
```json
{
  "success": false,
  "message": "Changes to the election aren't allowed once it is 20 minutes away from starting"
}
```

**412 PRECONDITION FAILED - Empty election ID:**
```json
{
  "success": false,
  "message": "Election id is empty"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while updating voters"
}
```

### Update Election Options
**Endpoint:**
`PATCH /api/v1/user/election/update/options`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "newOptions": [
    {
      "optionName": "Option 1"
    },
    {
      "optionName": "Option 2"
    }
  ]
}
```

**Response JSON Structures:**

**200 OK - Options updated successfully:**
```json
{
  "success": true,
  "message": "Election options updated successfully"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this election"
}
```

**403 FORBIDDEN - Too close to start time:**
```json
{
  "success": false,
  "message": "Changes to the election aren't allowed once it is 20 minutes away from starting"
}
```

**406 NOT ACCEPTABLE - Option name empty:**
```json
{
  "success": false,
  "message": "Option name cannot be empty"
}
```

**406 NOT ACCEPTABLE - Option name too long:**
```json
{
  "success": false,
  "message": "Option name cannot more than 30 characters"
}
```

**412 PRECONDITION FAILED - Empty election ID:**
```json
{
  "success": false,
  "message": "Election id is empty"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while updating voters"
}
```

### Get Election Result
**Endpoint:**
`GET /api/v1/user/election/result`

**Request Body:**
```json
{
  "electionId": "uuid-here"
}
```

**Flow:**
User fetches election results after the election has ended.

**Response JSON Structures:**

**200 OK - Election result returned:**
```json
{
  "success": true,
  "message": "Election result is generated",
  "electionResult": {
    "electionName": "My Election",
    "electionId": "uuid-here",
    "electionDescription": "Description of the election",
    "totalVotes": 15,
    "totalVoters": 20,
    "options": [
      {
        "optionName": "Option 1",
        "optionId": "uuid-here",
        "votes": 8
      },
      {
        "optionName": "Option 2",
        "optionId": "uuid-here",
        "votes": 7
      }
    ],
    "voters": [
      {
        "voterEmail": "voter1@example.com"
      },
      {
        "voterEmail": "voter2@example.com"
      }
    ]
  }
}
```

**403 FORBIDDEN - Election not ended:**
```json
{
  "success": false,
  "message": "Election result can only be obtained after the election has ended"
}
```

**404 NOT FOUND - Election not found:**
```json
{
  "success": false,
  "message": "Election not found in database"
}
```

**412 PRECONDITION FAILED - User not verified:**
```json
{
  "success": false,
  "message": "User is not verified"
}
```

**412 PRECONDITION FAILED - Empty election ID:**
```json
{
  "success": false,
  "message": "Election id is empty"
}
```

---

## Voting Endpoints

### Cast Vote
**Endpoint:**
`PUT /api/v1/public/vote/cast`

**Request Body:**
```json
{
  "votingSecret": "encrypted-voting-secret",
  "optionId": "uuid-here"
}
```

**Flow:**
User casts a vote using a unique link.

**Response JSON Structures:**

**200 OK - Vote cast successfully:**
```json
{
  "success": true,
  "message": "Vote cast successfully"
}
```

**401 UNAUTHORIZED - Voter not valid:**
```json
{
  "success": false,
  "message": "Voter is not valid in this election"
}
```

**403 FORBIDDEN - Election not started or ended:**
```json
{
  "success": false,
  "message": "Election not started or ended"
}
```

**404 NOT FOUND - Election not found:**
```json
{
  "success": false,
  "message": "Election not found in database"
}
```

**412 PRECONDITION FAILED - Voting secret empty:**
```json
{
  "success": false,
  "message": "Voting secret is empty"
}
```

**412 PRECONDITION FAILED - Option ID empty:**
```json
{
  "success": false,
  "message": "Option Id is empty"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while casting vote. This user can try again."
}
```

---

## Utilities

- **JWT Utility**: Token generation, validation, extraction, error handling for invalid/expired tokens.
- **User/Election Preconditions**: Validation helpers for user/election state, including ownership, verification, and timing.
- **Response Utility**: Standardized response body creation for all endpoints and error cases.
- **Voting String Utility**: Encryption/decryption for voting links, with error handling for invalid/expired links.
- **Pattern Matching Utility**: Email and text validation, with error handling for invalid formats.

---

## Complete Endpoint Summary

This documentation covers **all 26 endpoints** found in the codebase:

### Authentication & User Endpoints (8 endpoints)
1. `POST /api/v1/auth/signup` - User registration
2. `POST /api/v1/auth/login` - User login
3. `POST /api/v1/auth/email-verification/code` - Send email verification code
4. `POST /api/v1/auth/email-verification/verify` - Verify email code
5. `POST /api/v1/auth/password-reset/code` - Send password reset code
6. `POST /api/v1/auth/password-reset/verify-and-reset` - Reset password with code
7. `GET /api/v1/user` - Get user info
8. `DELETE /api/v1/user/delete` - Delete user account
9. `PUT /api/v1/user/password-reset` - Reset password (logged in)

### Token Endpoints (2 endpoints)
10. `POST /api/v1/auth/refresh` - Refresh JWT token
11. `POST /api/v1/auth/token-verification` - Verify token validity

### Health Check (1 endpoint)
12. `GET /api/v1/public/health-check` - API health check

### Election Endpoints (12 endpoints)
13. `POST /api/v1/user/election/create` - Create election
14. `DELETE /api/v1/user/election/delete` - Delete election
15. `GET /api/v1/user/election/find` - Find specific election
16. `GET /api/v1/user/election/find-all` - Find all user elections
17. `GET /api/v1/user/election/result` - Get election results
18. `PATCH /api/v1/user/election/update/name` - Update election name
19. `PATCH /api/v1/user/election/update/description` - Update election description
20. `PATCH /api/v1/user/election/update/poll-type` - Update poll type
21. `PATCH /api/v1/user/election/update/card-id` - Update card ID
22. `PATCH /api/v1/user/election/update/start-time` - Update start time
23. `PATCH /api/v1/user/election/update/end-time` - Update end time
24. `PATCH /api/v1/user/election/update/voters` - Update voters list
25. `PATCH /api/v1/user/election/update/options` - Update options list

### Voting Endpoints (1 endpoint)
26. `PUT /api/v1/public/vote/cast` - Cast vote

---

*This comprehensive documentation now includes complete JSON response structures for all possible outcomes of every endpoint, based on a thorough review of all controllers, services, and utilities in the codebase.*
