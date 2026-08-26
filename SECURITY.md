# Security notes

This repo is a **starter**, not a hardened production deployment. Clone it, then change secrets before any shared or production environment.

## Report a vulnerability

Email the maintainers listed in `pom.xml`. Do not file a public GitHub issue for exploitable bugs.

## Change before any real deploy

| Item | Why |
|------|-----|
| Flyway seed `admin` / `admin123` | Documented in `V2__init-auth-data.sql`. Rotate or delete. |
| `JWT_SECRET_KEY` | HS256, ≥ 32 characters. **Required** on profile `prod` (no default). |
| `DATABASE_PASSWORD` / Redis | Compose defaults are demo-only. Redis is published on `6379` **without AUTH** for local `./mvnw` — do not expose that port on a real host. |
| `GOOGLE_CLIENT_*` / `MAIL_*` | Empty placeholders. OAuth and mail will not work until set. |
| `CORS_ALLOWED_ORIGINS` | Local defaults are `localhost:3000` and `localhost:5173`. Prod is **empty** unless you set the env var. |

## Intentional template behaviour

- Redis token blacklist and cache **fail open** if Redis is down (availability over hard fail).
- Actuator Redis/mail health is **off** so dummy SMTP / Redis downtime does not mark the app unhealthy.
- Profile `dev` exposes `health,info,metrics` only (not heapdump/env).
- `GET /api/v1/users/{username}` is self or `ADMIN`.
- `POST /api/v1/auth/refresh-user-verification` does not reveal whether the username exists.
- Signup still returns **409** if username/email is taken (REST conflict). Treat that as enumeration if the API is public.

## JWT

Access and refresh tokens carry `jti`. Logout blacklists by `jti`. Refresh consume is Redis `SET NX`. Do not move the blacklist onto Spring `CacheManager`.
