<p align="center">
  <img src="https://res.cloudinary.com/deop9ytsv/image/upload/v1542422606/spring-boot-icon0_cf21dec4-5056-b3a8-49c015fd3bde6cb5.png" alt="Project Banner" />
</p>

<h2 align="center">Spring Template - Mono Project</h2>

<p align="center">
  This is a monolithic web application backend built with Spring Boot 4, structured using the Packaged by Feature approach. It supports secure authentication, authorization, user management, role-based permission control, and email notifications using templated emails.
</p>

<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Java-21-007396" /></a>
  <a href="#"><img src="https://img.shields.io/badge/SpringBoot-4.1-brightgreen" /></a>
  <a href="#"><img src="https://img.shields.io/badge/PostgreSQL-17-blue" /></a>
  <a href="#"><img src="https://img.shields.io/badge/Redis-7-red" /></a>
  <a href="#"><img src="https://img.shields.io/badge/Spotless-Google-orange" /></a>
</p>

Defaults are fine for local demos. Before any shared environment, follow [SECURITY.md](SECURITY.md).

---

## Overview

- Package-by-feature: `auth`, `user`, `article`, `email`, plus shared `app`
- JWT access/refresh (with `jti`) + Google OAuth2
- Redis for application cache and token blacklist (atomic refresh consume)
- PostgreSQL + Flyway migrations
- CORS via `CORS_ALLOWED_ORIGINS` (localhost defaults on `dev`; empty on `prod` unless set)
- Spotless (Google Java Format) on Maven `validate`

---

## Stack

| Area | Choice |
|------|--------|
| Runtime | Java 21, Spring Boot 4.1 |
| API | Spring Web MVC, Validation, Actuator |
| Security | Spring Security, OAuth2 Resource Server + Client, JWT (jjwt) |
| Data | Spring Data JPA, PostgreSQL 17, Flyway |
| Cache / blacklist | Spring Cache + Redis 7 |
| Docs | Springdoc OpenAPI (`dev` only; off on `prod`) |
| Mail | Spring Mail + Thymeleaf |
| Codegen / style | Lombok, MapStruct, Spotless (Google) |
| Tests | JUnit 5, H2, Spring Security Test |

---

## Project structure

```text
java-template/
├── AGENTS.md / CLAUDE.md   # AI entrypoints → .agents/
├── .agents/                # canonical agent rules + skills
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── pom.xml
├── mvnw / mvnw.cmd
└── src
    ├── main
    │   ├── java/com/goldenowl/springboottemplate
    │   │   ├── app/       # config, exceptions, shared utils
    │   │   ├── auth/      # login, JWT, OAuth2, roles
    │   │   ├── user/      # profile / user management
    │   │   ├── article/   # sample CRUD + cache
    │   │   └── email/     # mail + Thymeleaf handlers
    │   └── resources
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       ├── db/migration/
    │       └── templates/email/
    └── test
        ├── java/...
        └── resources/application-test.yml
```

---

## Prerequisites

- **Java 21+**
- **Docker** (recommended for PostgreSQL + Redis), or local PostgreSQL 17 + Redis 7
- Maven Wrapper is included (`./mvnw`) — no global Maven required

---

## Quick start (local)

### 1. Clone and env

```bash
cp .env.example .env
```

Spring Boot does **not** load `.env` for `./mvnw`. Compose does. For local Maven runs, either:

- rely on `application.yml` defaults (`localhost:5460` / Redis `localhost:6379`), or
- export the vars yourself.

For **Docker Compose `app-api`**, keep compose service hostnames in `.env` (see `.env.example`).

| Variable | Notes |
|----------|--------|
| `JWT_SECRET_KEY` | ≥ 32 chars (HS256). **Required** on `prod` (no default in yml) |
| `DATABASE_*` / `REDIS_*` | Compose vs localhost — see `.env.example` |
| `GOOGLE_CLIENT_*` / `MAIL_*` | Optional for basic JWT login |
| `CORS_ALLOWED_ORIGINS` | Comma-separated browser origins. Local default: `localhost:3000`, `localhost:5173`. Empty on `prod` unless set |

Flyway seed user: **`admin` / `admin123`** — change or remove before any real deploy.

### 2. Start PostgreSQL & Redis

```bash
docker compose up -d app-postgres-db app-redis
```

### 3. Run the API

```bash
# uses spring.profiles.default=dev
./mvnw spring-boot:run

# production-like (requires JWT_SECRET_KEY in the environment)
export JWT_SECRET_KEY='your-production-grade-secret-at-least-32-chars'
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

| Endpoint | URL |
|----------|-----|
| API | http://localhost:8160 |
| Health | http://localhost:8160/actuator/health |
| Swagger | http://localhost:8160/swagger-ui.html (`dev` only; off on `prod`) |
| Google OAuth | http://localhost:8160/oauth2/authorization/google |

Google Cloud redirect URI: `http://localhost:8160/login/oauth2/code/google`.

Mail and Redis health indicators are **disabled** so dummy SMTP / Redis downtime does not mark the app unhealthy. Profile `dev` exposes actuator `health`, `info`, and `metrics` only (not heapdump/env).

### 4. Smoke test

Login JSON uses `token` (access) and `refreshToken`.

```bash
LOGIN=$(curl -s -X POST http://localhost:8160/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}')

TOKEN=$(printf '%s' "$LOGIN" | python3 -c "import json,sys; print(json.load(sys.stdin)['token'])")
REFRESH=$(printf '%s' "$LOGIN" | python3 -c "import json,sys; print(json.load(sys.stdin)['refreshToken'])")

curl -s 'http://localhost:8160/api/v1/articles/paging?page=0&size=10' \
  -H "Authorization: Bearer $TOKEN"

curl -s -X POST http://localhost:8160/api/v1/auth/refresh-token \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"$REFRESH\"}"
```

---

## Docker Compose (full stack)

```bash
cp .env.example .env
# Ensure DATABASE_URL / REDIS_HOST use compose service names (app-postgres-db, app-redis)

docker compose up --build

# optional local debugging profile
SPRING_PROFILES_ACTIVE=dev docker compose up --build
```

Default Compose profile for `app-api` is **`prod`**. API is published on **8160**.

---

## Build, format, and tests

Spotless (Google Java Format) is bound to Maven **`validate`**, so local `./mvnw test` / `package` / `verify` fail if Java is not formatted. Scope: `src/main/java` and `src/test/java`.

```bash
./mvnw spotless:apply          # rewrite sources
./mvnw spotless:check          # check only
./mvnw test                    # local iterate (includes Spotless)
./mvnw -Dtest=AuthServiceImplTest,TokenBlacklistServiceImplTest test
./mvnw verify                  # tests + Boot jar
./mvnw -DskipTests package     # jar only
```

The `test` profile uses H2 (Flyway off), `spring.cache.type=simple`, Redis autoconfig excluded, and an in-memory token blacklist. No PostgreSQL or Redis required.

CI: `./mvnw -B spotless:check`, then `./mvnw -B verify -Dspotless.check.skip=true`.

---

## Profiles

| Profile | Behavior |
|---------|----------|
| `dev` | Default for local (`spring.profiles.default=dev`). `show-sql`, `root` DEBUG, Swagger on, actuator `health,info,metrics` |
| `prod` | Swagger off, `JWT_SECRET_KEY` required, quieter logs, CORS empty unless `CORS_ALLOWED_ORIGINS` is set (Compose default) |
| `test` | Maven tests only: H2 + in-memory cache/blacklist |

---

## AI agents

Canonical rules and skills: [`.agents/`](.agents/). Entrypoints: [AGENTS.md](AGENTS.md), [CLAUDE.md](CLAUDE.md). Cursor uses thin stubs under [`.cursor/`](.cursor/) that point at `.agents/` (no symlinks).
