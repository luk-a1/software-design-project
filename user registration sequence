sequenceDiagram
    autonumber
    actor User
    participant AuthController
    participant AuthService
    participant PasswordEncoder
    participant UserRepository
    database DB

    User->>AuthController: POST /api/auth/signup (RegistrationRequest)
    AuthController->>AuthService: registerUser(signUpRequest)
    AuthService->>UserRepository: existsByUsername(username)
    UserRepository-->>AuthService: false
    AuthService->>PasswordEncoder: encode(password)
    PasswordEncoder-->>AuthService: hashed_password
    AuthService->>UserRepository: save(newUser)
    UserRepository->>DB: INSERT INTO users
    DB-->>UserRepository: Success
    UserRepository-->>AuthService: UserObject
    AuthService-->>AuthController: RegistrationSuccess
    AuthController-->>User: 201 Created (Success Message)
