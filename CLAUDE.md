# CLAUDE.md

Instructions for **Claude Code**.

## Source of truth

Follow **`.agents/`** only (full rules + skills):

1. [AGENTS.md](AGENTS.md)  
2. [`.agents/README.md`](.agents/README.md)  
3. Open the matching file under **`.agents/rules/`** or **`.agents/skills/`**

Ignore `.cursor/` stubs for content — those exist for Cursor discovery and point back here.

| When working on… | Open |
|------------------|------|
| Always | `.agents/rules/project.mdc`, `.agents/rules/architecture.mdc` |
| Java / MapStruct | `.agents/rules/java-spring.mdc` |
| Feature scaffold | `.agents/skills/add-feature/SKILL.md` |
| Flyway / schema | `.agents/skills/add-flyway-migration/SKILL.md` |
| Mail template | `.agents/skills/add-mail-handler/SKILL.md` |
| Refactor | `.agents/skills/refactor-code/SKILL.md` |
| Auth / Redis / JWT | `.agents/rules/auth-redis.mdc` |
| Controllers | `.agents/rules/api-controllers.mdc` |
| Exceptions | `.agents/rules/exceptions.mdc` |
| Entities / migrations | `.agents/rules/data-flyway.mdc` |
| Logging | `.agents/rules/logging.mdc` |
| Email package | `.agents/rules/email.mdc` |
| Tests | `.agents/rules/tests.mdc` |
| Docker / env | `.agents/rules/docker-env.mdc` |

## Workflow

- Use `./mvnw` (wrapper).
- After Java changes: `./mvnw spotless:apply`, then smallest `./mvnw -Dtest=… test`. CI uses `./mvnw verify`.
- New CRUD feature → `.agents/skills/add-feature/SKILL.md` (clone `article`).
- Refactor / align conventions → `.agents/skills/refactor-code/SKILL.md`.
- Prefer editing only the touched feature package.

## Do not regress

- Blacklist: `StringRedisTemplate` + `jti` + SET NX refresh consume  
- No Redisson unless asked  
- Redis/mail health stay disabled; fail-open if Redis is down  
- No committing `.env` / secrets  
