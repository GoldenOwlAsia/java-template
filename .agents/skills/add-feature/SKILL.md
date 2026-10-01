---
name: add-feature
description: >-
  Adds a new package-by-feature module to this Spring Boot template (entity,
  repository, service, controller, DTOs, MapStruct mapper, Flyway, tests,
  permissions). Use when the user asks to add a feature/module/domain such as
  order, payment, product, cart, notification, or says package-by-feature,
  new CRUD API, or scaffold a feature package.
---

# Add a feature package

Clone the **`article`** feature as the structural reference. Do not invent a new layout.

Reference roots:

- Main: `src/main/java/com/goldenowl/springboottemplate/article/`
- Test: `src/test/java/com/goldenowl/springboottemplate/article/`

Replace `article` / `Article` with the new name (e.g. `order` / `Order`).

## 1. Confirm scope before coding

Ask only if missing:

- Resource name + API base path (default `/api/v1/<plural>`)
- Fields / relations (e.g. Order → User)
- Whether permissions are needed (default yes, mirror `ARTICLE_*`)
- Whether detail caching is needed (default no unless asked)

## 2. Create package tree

```text
src/main/java/com/goldenowl/springboottemplate/<feature>/
  controller/<Feature>Controller.java      # package-private class
  service/<Feature>Service.java            # public interface
  service/impl/<Feature>ServiceImpl.java   # package-private
  repository/<Feature>Repository.java
  entity/<Feature>Entity.java              # extends BaseEntity
  dto/<Feature>DTO.java
  dto/<Feature>DetailDTO.java              # if detail differs from list
  dto/<Feature>SaveDTO.java                # create/update body + validation
  mapper/<Feature>Mapper.java              # MapStruct
```

Tests (same packages under `src/test/java/...`):

- `controller/<Feature>ControllerTest.java` (pattern: `ArticleControllerTest`)
- `service/impl/<Feature>ServiceImplTest.java` (pattern: `ArticleServiceImplTest`)

## 3. Implement in this order

1. **Flyway** — use skill **add-flyway-migration** (next `V{n}__*.sql`, `GO_*` table, soft-delete column, permission inserts). Never edit old versions or rely on `ddl-auto` outside tests.
2. **Entity** — extend `BaseEntity`; add `@SoftDelete` like `ArticleEntity`. Default relations **LAZY**. Soft-deleted parent (like article author): `@NotFound(IGNORE)` — Hibernate **forces EAGER**, so do not also set `FetchType.LAZY` (see `ArticleEntity`).
3. **Repository** — Spring Data JPA; add `@EntityGraph` for paging/detail if associations are fetched.
4. **DTOs + Mapper** — MapStruct; `@NotBlank(message=...)` on save DTO; ignore `id`/`ol`/audit on entity maps.
5. **Service** — interface + `@Service` `@Transactional` `@RequiredArgsConstructor` `@Slf4j` impl; `log.info` milestones; throw `app.exception.*`; never log secrets.
6. **Controller** — `/api/v1/<plural>`; create → `201` + Location; `@PreAuthorize("hasAuthority('FEATURE_…')")`; `GET .../paging` with `@PageableDefault`.
7. **Cache** — only if requested: `CacheConstant` + `@Cacheable`/`@CacheEvict`.
8. **Security** — authenticated by default; only change `SecurityConfig` for public exceptions.
9. **Tests** — mirror article tests; no real Redis/Postgres.

## 4. Do / don't

**Do**

- Keep all feature code inside `<feature>/` (package-by-feature).
- Reuse `ResourceNotFoundException`, `ResourceAlreadyExistsException`, etc.
- Run `./mvnw spotless:apply` and targeted tests when done.

**Don't**

- Put order/business rules into `app` or `auth`.
- Expose entities from controllers.
- Add Redisson / new frameworks for a normal CRUD feature.
- Hand-edit huge unrelated files.

## 5. Example: `order`

User: “thêm feature order”

Deliver:

- Package `.../order/` with Order* types and `/api/v1/orders`
- Flyway create `orders` (+ permission rows if needed)
- Service/controller/tests modeled on `article`
- Stop and ask before adding payment gateways, outbox, or Redis cache unless requested

## 6. Done checklist

- [ ] Package tree complete under `<feature>/`
- [ ] Flyway migration added
- [ ] Permissions seeded if using `@PreAuthorize`
- [ ] Controller + service + tests exist
- [ ] `./mvnw spotless:apply`
- [ ] `./mvnw -Dtest=<Feature>ControllerTest,<Feature>ServiceImplTest test` (or full `./mvnw test`)
