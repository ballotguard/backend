
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
  "isOpen": false,
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
    "isOpen": false,
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

### Find Election (Owner)
**Endpoint:**
`GET /api/v1/user/election/find?electionId=`

**Request Body:**

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
    "isOpen": false,
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

### Find Election for Voter
**Endpoint:**
`GET /api/v1/election/find/voter?electionId=&voterId=`

**Request Body:**

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
    "isOpen": false,
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

**401 UNAUTHORIZED - User is not a valid voter:**
```json
{
  "success": false,
  "message": "This user cannot access this election"
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

### Find Open Election
**Endpoint:**
`GET /api/v1/election/find/open?electionId=`

**Request Body:**

**Flow:**
Public endpoint to fetch election details for open elections.

**Response JSON Structures:**

**200 OK - Open election found:**
```json
{
  "success": true,
  "message": "Election found",
  "electionInfo": {
    "electionId": "uuid-here",
    "electionName": "My Election",
    "electionDescription": "Description of the election",
    "isOpen": true,
    "startTime": 1640995200000,
    "endTime": 1641081600000,
    "electionLayout": {
      "pollType": "radio",
      "electionCardId": "1"
    },
    "openElectionLink": "https://example.com/vote/uuid-here",
    "options": [
      {
        "optionName": "Option 1",
        "optionId": "uuid-here"
      },
      {
        "optionName": "Option 2",
        "optionId": "uuid-here"
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

**412 PRECONDITION FAILED - Election is not public:**
```json
{
  "success": false,
  "message": "This election is not public"
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
      "isOpen": false,
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
      "isOpen": true,
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


### Update Election (Complete)
**Endpoint:**
`PATCH /api/v1/user/election/update`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "electionName": "Updated Election Name",
  "electionDescription": "Updated description",
  "isOpen": true,
  "startTime": 1640995200000,
  "endTime": 1641081600000,
  "electionLayout": {
    "pollType": "checkbox",
    "electionCardId": "2"
  },
  "options": [
    {
      "optionName": "New Option 1"
    },
    {
      "optionName": "New Option 2"
    }
  ],
  "voters": [
    {
      "voterEmail": "newvoter1@example.com"
    },
    {
      "voterEmail": "newvoter2@example.com"
    }
  ]
}
```

**Flow:**
User updates complete election information including all fields.

**Response JSON Structures:**

**200 OK - Election updated successfully:**
```json
{
  "success": true,
  "message": "Election is created successfully",
  "electionInfo": {
    "electionId": "uuid-here",
    "electionName": "Updated Election Name",
    "electionDescription": "Updated description",
    "isOpen": true,
    "startTime": 1640995200000,
    "endTime": 1641081600000,
    "electionLayout": {
      "pollType": "checkbox",
      "electionCardId": "2"
    },
    "openElectionLink": "https://example.com/vote/uuid-here",
    "options": [
      {
        "optionName": "New Option 1",
        "optionId": "uuid-here"
      },
      {
        "optionName": "New Option 2",
        "optionId": "uuid-here"
      }
    ],
    "voters": [
      {
        "voterEmail": "newvoter1@example.com"
      },
      {
        "voterEmail": "newvoter2@example.com"
      }
    ]
  }
}
```

**404 NOT FOUND - Election not found:**
```json
{
  "success": false,
  "message": "No election with this id exists"
}
```

**401 UNAUTHORIZED - User does not own election:**
```json
{
  "success": false,
  "message": "This user does not own this newElection"
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
  "message": "Max newElection name length is 30"
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
  "message": "Election poll type is invalid"
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
  "message": "One or all of the voter's email is invalid"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while changing the newElection name"
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

### Get Open Election Results (Public)
**Endpoint:**
`GET /api/v1/election/open/result?electionId={electionId}`

**Flow:**
Public endpoint to get election results for open elections. No authentication required.

**Response JSON Structures:**

**200 OK - Election result retrieved successfully:**
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

**403 FORBIDDEN - Election not ended yet:**
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

**412 PRECONDITION FAILED - Election ID empty:**
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

### Cast Multi Vote
**Endpoint:**
`PUT /api/v1/public/vote/cast/multi`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "voterId": "voter-uuid-here",
  "optionIds": "[\"option-uuid-1\", \"option-uuid-2\", \"option-uuid-3\"]"
}
```

**Flow:**
User casts multiple votes for different options using their voter ID. The `optionIds` field should be a JSON string array containing the option IDs to vote for.

**Response JSON Structures:**

**200 OK - Multi vote cast successfully:**
```json
{
  "success": true,
  "message": "Successfully casted vote"
}
```

**401 UNAUTHORIZED - Voter not valid:**
```json
{
  "success": false,
  "message": "This voter is not valid in this election"
}
```

**403 FORBIDDEN - Election not started:**
```json
{
  "success": false,
  "message": "Election has not started yet. You can vote after the election starts."
}
```

**403 FORBIDDEN - Election already ended:**
```json
{
  "success": false,
  "message": "Election has already ended."
}
```

**404 NOT FOUND - Election not found:**
```json
{
  "success": false,
  "message": "Election not found in database"
}
```

**412 PRECONDITION FAILED - Voter ID empty:**
```json
{
  "success": false,
  "message": "Voter Id is empty"
}
```

**412 PRECONDITION FAILED - Election ID empty:**
```json
{
  "success": false,
  "message": "Election Id is empty"
}
```

**412 PRECONDITION FAILED - Option IDs empty:**
```json
{
  "success": false,
  "message": "Option Ids is empty"
}
```

**500 INTERNAL SERVER ERROR - Unexpected error:**
```json
{
  "success": false,
  "message": "An error occurred while casting vote. This user can try again."
}
```

### Cast Open Vote
**Endpoint:**
`PUT /api/v1/public/vote/open/cast`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "optionId": "uuid-here"
}
```

**Flow:**
User casts a vote for an open election without requiring voter authentication.

**Response JSON Structures:**

**200 OK - Vote cast successfully:**
```json
{
  "success": true,
  "message": "Vote cast successfully"
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

**412 PRECONDITION FAILED - Election ID empty:**
```json
{
  "success": false,
  "message": "Election Id is empty"
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

### Cast Open Multi Vote
**Endpoint:**
`PUT /api/v1/public/vote/open/cast/multi`

**Request Body:**
```json
{
  "electionId": "uuid-here",
  "optionIds": "[\"option-uuid-1\", \"option-uuid-2\", \"option-uuid-3\"]"
}
```

**Flow:**
User casts multiple votes for different options in an open election. The `optionIds` field should be a JSON string array containing the option IDs to vote for.

**Response JSON Structures:**

**200 OK - Multi vote cast successfully:**
```json
{
  "success": true,
  "message": "Successfully casted vote"
}
```

**403 FORBIDDEN - Election not started:**
```json
{
  "success": false,
  "message": "Election has not started yet. You can vote after the election starts."
}
```

**403 FORBIDDEN - Election already ended:**
```json
{
  "success": false,
  "message": "Election has already ended."
}
```

**404 NOT FOUND - Election not found:**
```json
{
  "success": false,
  "message": "Election not found in database"
}
```

**412 PRECONDITION FAILED - Election ID empty:**
```json
{
  "success": false,
  "message": "Election Id is empty"
}
```

**412 PRECONDITION FAILED - Option IDs empty:**
```json
{
  "success": false,
  "message": "Option Ids is empty"
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

## User Settings Endpoints

### Get User Settings
**Endpoint:**
`GET /api/v1/user/settings`

**Response JSON Structures:**

**200 OK - Settings retrieved successfully:**
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

**400 BAD REQUEST - Invalid user settings ID:**
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
  "message": "An error occurred while fetching user settings"
}
```

### Update User Settings
**Endpoint:**
`PUT /api/v1/user/settings`

**Request Body:**
```json
{
  "id": "uuid-here",
  "userEmail": "user@example.com",
  "preferredLanguage": "English",
  "preferredTheme": "Light",
  "pushNotificationsEnabled": true,
  "emailNotificationsEnabled": true
}
```

**Response JSON Structures:**

**200 OK - Settings updated successfully:**
```json
{
  "success": true,
  "message": "User settings updated successfully",
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

**400 BAD REQUEST - Invalid user settings ID or entity:**
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

## Utilities

- **JWT Utility**: Token generation, validation, extraction, error handling for invalid/expired tokens.
- **User/Election Preconditions**: Validation helpers for user/election state, including ownership, verification, and timing.
- **Response Utility**: Standardized response body creation for all endpoints and error cases.
- **Voting String Utility**: Encryption/decryption for voting links, with error handling for invalid/expired links.
- **Pattern Matching Utility**: Email and text validation, with error handling for invalid formats.

---

## Complete Endpoint Summary

This documentation covers **all 27 endpoints** found in the codebase:

### Authentication & User Endpoints (9 endpoints)
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

### User Settings Endpoints (2 endpoints)
13. `GET /api/v1/user/settings` - Get user settings
14. `PUT /api/v1/user/settings` - Update user settings

### Election Endpoints (9 endpoints)
15. `POST /api/v1/user/election/create` - Create election
16. `DELETE /api/v1/user/election/delete` - Delete election
17. `GET /api/v1/user/election/find` - Find election (owner)
18. `GET /api/v1/election/find/open` - Find open election (public)
19. `GET /api/v1/election/find/voter` - Find election for voter
20. `GET /api/v1/user/election/find-all` - Find all user elections
21. `GET /api/v1/user/election/result` - Get election results
22. `GET /api/v1/election/open/result` - Get open election results (public)
23. `PATCH /api/v1/user/election/update` - Update complete election

### Voting Endpoints (4 endpoints)
24. `PUT /api/v1/public/vote/cast` - Cast vote
25. `PUT /api/v1/public/vote/cast/multi` - Cast multi vote
26. `PUT /api/v1/public/vote/open/cast` - Cast open vote
27. `PUT /api/v1/public/vote/open/cast/multi` - Cast open multi vote

---

*This comprehensive documentation now includes complete JSON response structures for all possible outcomes of every endpoint, based on a thorough review of all controllers, services, and utilities in the codebase.*
