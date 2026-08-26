# Agent docs (tool-agnostic)

**Canonical content lives only under `.agents/`.** Vendor folders hold thin entrypoints, not copies of the full text.

```text
.agents/
  README.md
  rules/*.mdc          ← full rules (edit here)
  skills/*/SKILL.md    ← full skills (edit here)

.cursor/rules/*.mdc    ← stubs: same frontmatter + “read .agents/rules/…”
.cursor/skills/*/SKILL.md  ← stubs: description + “read .agents/skills/…”

AGENTS.md / CLAUDE.md  ← entrypoints → .agents/
```

No symlinks. Change rules/skills only in **`.agents/`**.

## Rules

| File | When |
|------|------|
| `project.mdc` | Always — map, commands, boundaries |
| `architecture.mdc` | Always — package-by-feature |
| `java-spring.mdc` | `*.java` |
| `logging.mdc` | `*.java` |
| `api-controllers.mdc` | controllers |
| `exceptions.mdc` | exceptions / services / controllers |
| `auth-redis.mdc` | auth, cache, yml |
| `data-flyway.mdc` | entity, repo, migration |
| `email.mdc` | email package |
| `docker-env.mdc` | compose / env / yml |
| `tests.mdc` | `src/test/**` |

CI (`.github/workflows/ci.yml`): `spotless:check` then `./mvnw verify`.

## Skills

| Skill | Use for |
|-------|---------|
| `add-feature` | New domain package (clone `article`) |
| `add-flyway-migration` | New `V{n}__*.sql` |
| `add-mail-handler` | New Thymeleaf mail handler |
