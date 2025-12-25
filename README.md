# 🔐 ApexAuth - Authentication Microservice

A production-ready authentication and authorization microservice built with Spring Boot 4.0, featuring JWT-based authentication, refresh tokens, and role-based access control.

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Environment Configuration](#-environment-configuration)
- [Docker Deployment](#-docker-deployment)
- [API Endpoints](#-api-endpoints)
- [Security](#-security)
- [Development](#-development)
- [Production Deployment](#-production-deployment)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

- 🔐 **JWT Authentication** - Secure token-based authentication
- 🔄 **Refresh Tokens** - Long-lived tokens for seamless user experience
- 👥 **Role-Based Access Control (RBAC)** - Admin and User roles
- 📧 **Flexible Login** - Support for email, username, or phone number
- 🔒 **Password Hashing** - BCrypt encryption for passwords
- 🌐 **CORS Support** - Configurable cross-origin resource sharing
- 📊 **Health Checks** - Spring Boot Actuator endpoints
- 📚 **API Documentation** - Interactive Swagger/OpenAPI UI
- 🐳 **Docker Ready** - Containerized for easy deployment
- 🔍 **Request Logging** - Track all API calls for debugging
- ⚡ **Production Optimized** - Security hardened with JVM tuning

---

## 🛠 Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Java 25 |
| **Framework** | Spring Boot 4.0 |
| **Security** | Spring Security + JWT (JJWT 0.11.5) |
| **Database** | PostgreSQL 17 |
| **ORM** | Spring Data JPA (Hibernate) |
| **Documentation** | SpringDoc OpenAPI 2.7 |
| **Containerization** | Docker + Docker Compose |
| **Build Tool** | Maven 3.9 |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────┐
│                  Client Applications                    │
│         (React, Angular, Mobile Apps, etc.)            │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ HTTP/HTTPS + JWT
                     │
┌────────────────────▼────────────────────────────────────┐
│              ApexAuth Microservice                      │
│                                                         │
│  ┌─────────────────────────────────────────────────┐  │
│  │  Public Endpoints                               │  │
│  │  • POST /auth/register                          │  │
│  │  • POST /auth/login                             │  │
│  │  • POST /auth/refresh                           │  │
│  └─────────────────────────────────────────────────┘  │
│                                                         │
│  ┌─────────────────────────────────────────────────┐  │
│  │  Protected Endpoints (JWT Required)             │  │
│  │  • GET  /auth/me                                │  │
│  │  • GET  /user/profile                           │  │
│  └─────────────────────────────────────────────────┘  │
│                                                         │
│  ┌─────────────────────────────────────────────────┐  │
│  │  Admin Endpoints (ROLE_ADMIN Required)          │  │
│  │  • GET    /admin/stats                          │  │
│  │  • GET    /admin/users                          │  │
│  │  • DELETE /admin/users/{id}                     │  │
│  └─────────────────────────────────────────────────┘  │
│                                                         │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ JDBC
                     │
┌────────────────────▼────────────────────────────────────┐
│               PostgreSQL Database                       │
│  • users table                                          │
│  • refresh_tokens table                                 │
└─────────────────────────────────────────────────────────┘
```

---

## 🚀 Quick Start

### Prerequisites

- Java 25
- PostgreSQL 17
- Maven 3.9+ (or use included `./mvnw`)
- Docker & Docker Compose (optional)

### 1. Clone Repository

```bash
git clone https://github.com/yourname/apexauth.git
cd apexauth
```

### 2. Set Up Database

```bash
# Create PostgreSQL database
psql -U postgres
CREATE DATABASE apexauth_db;
\q
```

### 3. Configure Environment

```bash
# Copy example environment file
cp .env.example .env

# Edit .env with your configuration
nano .env
```

### 4. Run Locally (Without Docker)

```bash
# Build and run
./mvnw spring-boot:run

# Application starts at http://localhost:8080
```

### 5. Run with Docker

```bash
# Copy Docker environment file
cp .env.docker.example .env.docker

# Edit .env.docker with your configuration
nano .env.docker

# Build and run
docker-compose up -d

# View logs
docker-compose logs -f
```

---

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

**Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 🔧 Environment Configuration

### Required Environment Variables

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/apexauth_db
DB_USER=your_username
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your-super-secret-key-at-least-32-characters-long
JWT_EXPIRATION_MS=900000              # 15 minutes
JWT_REFRESH_EXPIRATION_MS=604800000   # 7 days

# Server
SERVER_PORT=8080
SPRING_PROFILE=local                   # local, dev, prod

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

### Generate Secure JWT Secret

```bash
# Using OpenSSL
openssl rand -base64 64

# Using /dev/urandom
cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 64 | head -n 1
```

---

## 🐳 Docker Deployment

### Development Build

```bash
# Build image
docker build -t apexauth:dev .

# Run container
docker-compose up -d
```

### Production Build

```bash
# Build optimized production image
docker build -f Dockerfile.prod -t apexauth:1.0.0 .

# Tag as latest
docker tag apexauth:1.0.0 apexauth:latest

# Run
docker run -d \
  --name apexauth-prod \
  --env-file .env.docker \
  -p 8080:8080 \
  --restart always \
  apexauth:1.0.0
```

### Push to Docker Hub

```bash
# Login
docker login

# Tag
docker tag apexauth:1.0.0 yourname/apexauth:1.0.0

# Push
docker push yourname/apexauth:1.0.0
```

---

## 🔌 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | Login and get JWT | No |
| POST | `/auth/refresh` | Refresh access token | No |
| GET | `/auth/me` | Get current user | Yes (JWT) |

### User Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/user/profile` | Get user profile | Yes (JWT) |
| PUT | `/user/profile` | Update user profile | Yes (JWT) |

### Admin Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/admin/stats` | Get user statistics | Yes (ADMIN) |
| GET | `/admin/users` | List all users | Yes (ADMIN) |
| DELETE | `/admin/users/{id}` | Delete user | Yes (ADMIN) |

### Health Check

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/actuator/health` | Service health status | No |

---

## 🔒 Security

### Features

- ✅ **Password Hashing** - BCrypt with salt
- ✅ **JWT Tokens** - HS256 signature algorithm
- ✅ **Refresh Token Rotation** - One active token per user
- ✅ **Token Expiration** - Short-lived access tokens (15 min)
- ✅ **Non-root Container** - Production image runs as unprivileged user
- ✅ **CORS Protection** - Configurable allowed origins
- ✅ **Stateless Sessions** - No server-side session storage

### JWT Token Structure

```json
{
  "sub": "user_id",
  "email": "user@example.com",
  "role": "ROLE_USER",
  "iat": 1734567890,
  "exp": 1734568790
}
```

---

## 💻 Development

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=AuthServiceTest
```

### Build JAR

```bash
# Build JAR file
./mvnw clean package

# JAR location: target/apexauth.jar

# Run JAR
java -jar target/apexauth.jar
```

### Local Development Workflow

```bash
# 1. Make code changes
vim src/main/java/...

# 2. Build
./mvnw clean package -DskipTests

# 3. Run
./mvnw spring-boot:run

# 4. Test
curl http://localhost:8080/actuator/health
```

---

## 🌐 Production Deployment

### Using Docker Compose

```bash
# 1. Set production environment variables
cp .env.docker.example .env.docker
nano .env.docker  # Configure for production

# 2. Build production image
DOCKERFILE=Dockerfile.prod docker-compose build

# 3. Deploy
docker-compose up -d

# 4. Verify
curl https://your-domain.com/actuator/health
```

### Using Docker Directly

```bash
# Build
docker build -f Dockerfile.prod -t apexauth:1.0.0 .

# Run
docker run -d \
  --name apexauth-prod \
  -e DB_URL="jdbc:postgresql://prod-db:5432/apexauth_prod" \
  -e DB_PASSWORD="$(aws secretsmanager get-secret-value --secret-id prod/db --query SecretString --output text)" \
  -e JWT_SECRET="$(aws secretsmanager get-secret-value --secret-id prod/jwt --query SecretString --output text)" \
  -p 8080:8080 \
  --restart always \
  apexauth:1.0.0
```

### Production Checklist

- [ ] Use strong JWT secret (64+ random characters)
- [ ] Set up HTTPS/TLS certificates
- [ ] Configure production database (AWS RDS, Cloud SQL)
- [ ] Set up secrets manager (AWS Secrets Manager, Vault)
- [ ] Configure monitoring and alerts
- [ ] Set up database backups
- [ ] Configure rate limiting
- [ ] Set up CI/CD pipeline
- [ ] Use environment-specific configurations
- [ ] Enable security headers

---

## 📊 Project Structure

```
apexauth/
├── src/
│   ├── main/
│   │   ├── java/com/apexauth/apexauth/
│   │   │   ├── auth/              # Authentication logic
│   │   │   ├── config/            # Spring configuration
│   │   │   ├── controller/        # REST controllers
│   │   │   ├── exception/         # Error handling
│   │   │   ├── security/          # JWT, filters
│   │   │   ├── token/             # Refresh tokens
│   │   │   ├── user/              # User entity & repo
│   │   │   └── util/              # Helper classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-local.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/                      # Unit tests
├── Dockerfile                     # Development build
├── Dockerfile.prod                # Production build
├── docker-compose.yml             # Container orchestration
├── pom.xml                        # Maven dependencies
├── .env.example                   # Environment template
├── .env.docker.example            # Docker env template
├── .gitignore                     # Git ignore rules
├── INTERVIEW_QA.md               # Interview questions
└── README.md                      # This file
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📧 Contact

**Project Link:** [https://github.com/yourname/apexauth](https://github.com/yourname/apexauth)

**Issues:** [https://github.com/yourname/apexauth/issues](https://github.com/yourname/apexauth/issues)

---

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [JWT.io](https://jwt.io/)
- [Docker](https://www.docker.com/)
- [PostgreSQL](https://www.postgresql.org/)

---

## 📈 Roadmap

- [ ] Email verification
- [ ] Password reset flow
- [ ] Two-factor authentication (2FA)
- [ ] OAuth2 integration (Google, GitHub)
- [ ] Rate limiting
- [ ] Account lockout after failed attempts
- [ ] Audit logging
- [ ] WebAuthn support
- [ ] Kubernetes deployment manifests
- [ ] Comprehensive test coverage

---

**Built with ❤️ using Spring Boot**
