---
name: add-flyway-migration
description: >-
  Adds a new Flyway SQL migration under db/migration with correct versioning,
  GO_ table style, soft-delete columns, FKs, partial unique indexes, or
  permission seed inserts. Use when the user asks for a Flyway migration,
  schema change, ALTER TABLE, new table SQL, permission seed, or V__ script.
---

# Add a Flyway migration

## 1. Versioning

- Path: `src/main/resources/db/migration/`
- Name: `V{n}__{snake_description}.sql` (double underscore)
- `{n}` = highest existing `V*` + 1 (currently through **V8** — always re-check the folder)
- **Never edit** applied migrations; always add a new version
- One concern per file when practical (create table vs data fix vs permissions)

## 2. Pick the recipe

### A) New domain table (like `V4__article.sql`)

```sql
CREATE TABLE GO_<NAME> (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE,
    -- domain columns...
    CONSTRAINT fk_...
        FOREIGN KEY (...) REFERENCES GO_USER(id) ON DELETE CASCADE
);
```

- Table names `GO_*` uppercase; columns snake_case
- Include `ol` + audit columns always for `BaseEntity`
- Include `deleted` when the entity will use `@SoftDelete`

### B) Permission seed (extend auth data)

Do not rewrite `V2`. New file:

1. `INSERT INTO GO_PERMISSION (id, name, description) VALUES (...)`
2. `INSERT INTO GO_ROLE_PERMISSIONS` — ADMIN (`role-1`) → all new perms
3. USER (`role-2`) → usually `*_READ` only (unless asked otherwise)

Use stable new string ids (`perm-5`, …) that do not collide with existing rows. Check `V2` and later permission inserts first.

### C) Partial unique (active rows only)

Follow `V7__soft_delete_partial_unique.sql`:

```sql
CREATE UNIQUE INDEX IF NOT EXISTS uq_go_<table>_<col>_active
    ON GO_<TABLE> (<col>)
    WHERE deleted IS NOT TRUE;
```

### D) Alter / data migration

- Prefer additive changes (`ADD COLUMN`, new indexes)
- Destructive changes: be explicit; ask if data loss is possible
- Use `IF EXISTS` / `IF NOT EXISTS` when matching project style (see V6/V7)

## 3. Align Java (if schema is for a feature)

If this migration backs an entity:

- Entity extends `BaseEntity`
- `@SoftDelete` when `deleted` column exists
- `ddl-auto` stays `validate` outside tests
- MapStruct/services are out of scope unless the user also asked for the feature (then use skill **add-feature**)

## 4. Verify

```bash
# list current versions
ls src/main/resources/db/migration/

# after writing, local app with Postgres applies on startup
./mvnw spring-boot:run
```

Do not invent a parallel migration tool (Liquibase, etc.).

## 5. Done checklist

- [ ] Filename `V{n}__...sql` with next free `n`
- [ ] No edits to older `V*` files
- [ ] `GO_` + snake_case + BaseEntity columns when creating tables
- [ ] Permissions use new inserts + role links if needed
- [ ] Entity/`@SoftDelete` called out or updated if this is for a feature
