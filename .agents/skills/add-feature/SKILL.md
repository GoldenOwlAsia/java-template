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

Clone the **`article`** feature as the **package layout and CRUD wiring**. Do not invent a new tree.

Copy structure and neighbors (`UserService` author, MapStruct, tests). Do **not** copy article cache (`@Cacheable` / `@CacheEvict`, `CacheConstant`, `RedisCacheConfig`) unless the user asked for cache.

Reference roots:

- Main: `src/main/java/com/goldenowl/springboottemplate/article/`
- Test: `src/test/java/com/goldenowl/springboottemplate/article/`

Replace `article` / `Article` with the new name (e.g. `product` / `Product`).

## 1. Confirm scope before coding

Ask only if missing:

- Resource name + API base path (default `/api/v1/<plural>`)
- Fields / relations (e.g. Product → User)
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

1. **Flyway** — skill **add-flyway-migration**: next `V{n}__*.sql` with `GO_*` table **and** `GO_PERMISSION` / `GO_ROLE_PERMISSIONS` inserts. Article perms live in `V2` — do not edit `V2`/`V4`; never `ddl-auto` outside tests.
2. **Entity** — extend `BaseEntity`; `@Table(name = "GO_…")`; `@SoftDelete`. Default relations **LAZY**. Soft-deleted parent (article `author`): `@NotFound(IGNORE)` + **EAGER** (Hibernate forces it — do not also set `LAZY`).
3. **Repository** — Spring Data JPA; `@EntityGraph` on paging/detail like `findAll` / `findDetailedById`.
4. **DTOs + Mapper** — MapStruct `@Mapper(uses = {UserMapper.class})` when flattening author; `@NotBlank(message=...)` on save DTO; ignore `id`/`ol`/audit on DTO → entity.
5. **Service** — public interface + package-private `@Service` `@Transactional` `@RequiredArgsConstructor` `@Slf4j` impl. Set author via `AuthenticationUtils.getCurrentUsername()` + `UserService` like article. `log.info` milestones; `app.exception.*`; never log secrets.
6. **Controller** — `/api/v1/<plural>`; create → `201` + Location; `@PreAuthorize("hasAuthority('<FEATURE>_READ|CREATE|UPDATE|DELETE')")`; `GET .../paging` + `@PageableDefault`.
7. **Cache** — default **off**. Strip cache annotations from the article clone. Only if asked: `CacheConstant` + TTL in `RedisCacheConfig` + `@Cacheable`/`@CacheEvict`.
8. **Security** — already authenticated; do not edit `SecurityConfig` unless the user wants public routes.
9. **Tests** — clone `ArticleControllerTest` / `ArticleServiceImplTest`: `@WebMvcTest` + `@ActiveProfiles(TEST)` + `@Import({SecurityConfigTest.class, GlobalExceptionHandler.class})` + `@WithMockUser(authorities = {…})`. No real Redis/Postgres.

## 4. Do / don't

**Do**

- Keep all feature code inside `<feature>/` (package-by-feature).
- Reuse `ResourceNotFoundException`, `ResourceAlreadyExistsException`, etc.
- Run `./mvnw spotless:apply` and targeted tests when done.

**Don't**

- Put domain rules into `app` or `auth`.
- Expose entities from controllers.
- Copy article cache / Redisson / new frameworks for a normal CRUD feature.
- Edit old Flyway versions or `SecurityConfig` / `RedisCacheConfig` without being asked.
- Hand-edit huge unrelated files.

## 5. Example: `product`

User: “add feature product”

Assume unless they said otherwise:

- `/api/v1/products`
- Same shape as article (`title`, `content`, `author` → `UserEntity`) if they did not list fields
- `PRODUCT_READ|CREATE|UPDATE|DELETE` — ADMIN all, USER `PRODUCT_READ` only
- **No** cache, inventory, pricing, or extra packages

Rename map (keep article wiring; drop cache):

| From `article` | To `product` |
|----------------|--------------|
| `ArticleController` | `ProductController` — `/api/v1/products` |
| `ArticleService` / `ArticleServiceImpl` | `ProductService` / `ProductServiceImpl` — **no** `@Cacheable` / `@CacheEvict` |
| `ArticleRepository` | `ProductRepository` — keep `@EntityGraph` + `findDetailedById` |
| `ArticleEntity` | `ProductEntity` — `@Table(name = "GO_PRODUCT")`, `@SoftDelete`, author `@NotFound` |
| `ArticleDTO` / `DetailDTO` / `SaveDTO` | `ProductDTO` / `ProductDetailDTO` / `ProductSaveDTO` |
| `ArticleMapper` | `ProductMapper` — `uses = UserMapper`, flatten `author.username` |
| Controller/service tests | `ProductControllerTest` / `ProductServiceImplTest` — authorities `PRODUCT_*` |
| `ARTICLE_*` | `PRODUCT_*` |

Also clone (same classes, not renamed): `UserService` + `AuthenticationUtils` for current user as author.

Flyway: **new** `V{n}__product.sql` (`GO_PRODUCT` like `V4` **plus** permission/role inserts; do not edit `V2`/`V4`).

Leave untouched: `CacheConstant`, `RedisCacheConfig`, `SecurityConfig`.

Stop and ask before cache, `permitAll`, or extra domains.

## 6. Done checklist

- [ ] Package tree complete under `<feature>/`
- [ ] New Flyway version: table + permission seeds (old `V*` untouched)
- [ ] `@PreAuthorize` names match seeded `GO_PERMISSION`
- [ ] No cache/`SecurityConfig` changes unless requested
- [ ] Controller + service + tests exist (WebMvcTest profile `test`)
- [ ] `./mvnw spotless:apply`
- [ ] `./mvnw -Dtest=<Feature>ControllerTest,<Feature>ServiceImplTest test`
