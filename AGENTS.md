# AGENTS.md

Shared entrypoint for AI coding agents (Cursor, Claude Code, Codex, etc.).

**Canonical docs:** [`.agents/`](.agents/) — edit only there.  
Cursor uses thin stubs under `.cursor/` that point at `.agents/` (no symlinks, no duplicated bodies).

Human runbook: [README.md](README.md).

## Stack

Java 21 · Spring Boot 4.1 · package-by-feature (`app`, `auth`, `user`, `article`, `email`).

## Quick map

```text
app/       shared config, BaseEntity, exceptions
auth/      JWT, OAuth2, security, token blacklist
user/      profile / status
article/   CRUD sample (+ cache) — clone for new features
email/     mail + Thymeleaf
db/migration/   Flyway only
```

## Commands

```bash
./mvnw spotless:apply
./mvnw test                     # local iterate
./mvnw verify                   # CI / pre-merge
./mvnw spring-boot:run          # profile dev by default
docker compose up -d app-postgres-db app-redis
```

## Topic → open (always `.agents/…`)

| Topic | Path |
|-------|------|
| Project / boundaries | `.agents/rules/project.mdc` |
| Architecture | `.agents/rules/architecture.mdc` |
| Java / MapStruct | `.agents/rules/java-spring.mdc` |
| Logging | `.agents/rules/logging.mdc` |
| API controllers | `.agents/rules/api-controllers.mdc` |
| Exceptions | `.agents/rules/exceptions.mdc` |
| Auth / Redis | `.agents/rules/auth-redis.mdc` |
| Data / Flyway | `.agents/rules/data-flyway.mdc` |
| Email | `.agents/rules/email.mdc` |
| Docker / env | `.agents/rules/docker-env.mdc` |
| Tests | `.agents/rules/tests.mdc` |
| New feature | `.agents/skills/add-feature/SKILL.md` |
| New migration | `.agents/skills/add-flyway-migration/SKILL.md` |
| New mail | `.agents/skills/add-mail-handler/SKILL.md` |

## Hard boundaries

No secrets in git · no Redisson unless asked · no blacklist on Spring Cache · JWT `jti` + SET NX refresh · Redis fail-open · health redis/mail off · no drive-by refactors.
