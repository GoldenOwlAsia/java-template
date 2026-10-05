---
name: refactor-code
description: >-
  Refactors existing Java/Spring code to match this template's package-by-feature
  rules without changing behavior or inventing new stack. Use when the user asks
  to refactor, clean up, dọn code, chỉnh convention, extract layers, move a class
  between packages, reduce duplication, or align code with project rules.
---

# Refactor to project rules

Goal: **same behavior**, smaller / clearer code, **this repo's conventions**.
Not a rewrite, not a new feature, not a framework swap.

Read neighboring code in the **same feature package** first. Clone patterns from
`article/` (CRUD) or the closest existing class.

## 0. Scope before editing

1. Name the files / package in scope. Default: **only what the user pointed at**.
2. If the request is actually “add a new domain package”, stop and use
   **add-feature** instead.
3. If schema must change, do not “fix” old Flyway files — use
   **add-flyway-migration**.
4. Confirm **no behavior change** unless the user explicitly asked for a fix.

Do **not** expand into drive-by refactors (rename the world, reformat unrelated
packages, “while we’re here” auth rewrites).

## 1. Hard boundaries (never “improve” these unless asked)

From `.agents/rules/project.mdc` and `auth-redis.mdc`:

- Token blacklist stays `StringRedisTemplate` + JWT **`jti`**; refresh consume is **SET NX**.
- Do not move blacklist onto Spring `CacheManager`.
- Do not add Redisson or a second Redis client.
- Redis/mail health stay disabled; Redis down stays **fail-open**.
- Do not commit `.env` / secrets.
- Do not replace JWT with sessions.

## 2. Target shape (check against rules)

Open the matching rule; do not invent a second style.

| Topic | Rule |
|-------|------|
| Where code lives | `.agents/rules/architecture.mdc` |
| Java / Lombok / MapStruct / validation | `.agents/rules/java-spring.mdc` |
| Controllers / HTTP / `@PreAuthorize` | `.agents/rules/api-controllers.mdc` |
| Exceptions / HTTP mapping | `.agents/rules/exceptions.mdc` |
| Entity / repo / Flyway | `.agents/rules/data-flyway.mdc` |
| Logging | `.agents/rules/logging.mdc` |
| Auth / Redis / cache | `.agents/rules/auth-redis.mdc` |
| Tests | `.agents/rules/tests.mdc` |
| Email | `.agents/rules/email.mdc` |

### Package-by-feature

- Keep a vertical slice inside one feature: `controller` · `service`/`impl` ·
  `repository` · `entity` · `dto` · `mapper`.
- `app` = shared config, `BaseEntity`, exceptions, utils — **no domain rules**.
- `auth` = tokens/security/roles — **not** profile CRUD.
- `user` = user persistence/profile/status — **not** JWT issuance.
- Moving a type across features: update imports, MapStruct `uses`, tests, and
  permissions together. Do not leave a “helper” dump in `app`.

### Layers

- Controller: HTTP + `@Valid` + `@PreAuthorize`; call service only. No JPA/Redis.
  Package-private like neighbors. No controller logging.
- Service: public interface + package-private `@Service` `@Transactional`
  `@RequiredArgsConstructor` `@Slf4j` impl.
- Repository: Spring Data only; `@EntityGraph` when fetching associations.
- Mapper: MapStruct only (`mapToDto` / `mapToEntity` / `@MappingTarget`).
  No hand-mapping in controllers. Ignore `id` / `ol` / audit on DTO → entity.
- API boundary: DTOs (`*DTO`, `*SaveDTO`, `*DetailDTO`) — never return entities.
- Errors: throw existing `app.exception.*`. Do not add a second
  `@RestControllerAdvice` or ad-hoc `Map` bodies.
- Logging: parameterized `log.info` milestones in services; never log passwords,
  raw JWT, `Authorization`, SMTP/OAuth secrets.

### API / data consistency (if you touch those files)

- Paths `/api/v1/<plural>`; create → `201` + Location; list → `GET .../paging`
  + `@PageableDefault`.
- Permissions: `FEATURE_READ|CREATE|UPDATE|DELETE` matching `GO_PERMISSION`.
- Entities extend `BaseEntity`; `@SoftDelete` like `ArticleEntity`. Do not
  rewrite applied migrations as part of a Java cleanup.

## 3. Allowed refactor moves

Prefer mechanical, local moves that match neighbors:

- Extract MapStruct mapping out of a controller/service.
- Split a god class **within the same feature** (not into `app` unless truly
  cross-cutting).
- Replace field injection / `new` mappers with constructor injection + MapStruct.
- Replace `System.out` / leftover debug with `@Slf4j` at the correct level.
- Align names: `*Entity`, `*ServiceImpl`, `*Repository`, `*Controller`.
- Make `*ServiceImpl` / controllers package-private if the rest of the package is.
- Deduplicate by reusing existing exceptions, utils, constants
  (`ProfileConstant`, `CacheConstant`) — do not add a new util “framework”.
- Tests: same package path, `*Test`; keep H2 / no real Redis; copy Mockito /
  `@WebMvcTest` patterns from the closest test.

## 4. Disallowed “refactors”

- New frameworks, MapStruct alternatives, extra response envelopes.
- Broad rename across the repo “for consistency” without being asked.
- Changing auth/JWT/blacklist/cache semantics to look cleaner.
- Enabling Redis/mail health or tightening fail-open to fail-closed.
- Editing old `V*__*.sql` to match Java after a rename — add a **new** migration
  if the schema/permission names must change.
- Putting `@Async` on JPA entity-listener methods (see **add-mail-handler**).
- Reformatting by hand — use `./mvnw spotless:apply`.

## 5. Workflow

1. List current violations in scope (layering, DTO leak, mapping, logging, package).
2. Apply the smallest set of edits that fixes those violations.
3. Update tests in the same feature if signatures / visibility / packages moved.
4. `./mvnw spotless:apply`
5. Smallest tests: `./mvnw -Dtest=TouchedClassTest,… test`
6. If you touched auth / JWT / blacklist / cache, also run the suite in
   `.agents/rules/tests.mdc`.

## 6. Done checklist

- [ ] Behavior unchanged (unless a bugfix was requested)
- [ ] Diff stays inside the agreed package(s)
- [ ] Matches architecture + java-spring + the layer-specific rule
- [ ] No new advice/envelope/Redis client/framework
- [ ] Spotless applied
- [ ] Targeted tests pass
