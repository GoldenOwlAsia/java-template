-- V1__init_schema.sql
CREATE TABLE GO_PERMISSION (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(512)
);

CREATE TABLE GO_ROLE (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE GO_ROLE_PERMISSIONS (
    role_id VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role
        FOREIGN KEY (role_id) REFERENCES GO_ROLE(id) ON DELETE CASCADE,
    CONSTRAINT fk_permission
        FOREIGN KEY (permission_id) REFERENCES GO_PERMISSION(id) ON DELETE CASCADE
);

CREATE TABLE GO_USER (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    username VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    oauth_id VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE GO_USER_ROLES (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES GO_USER(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_user
        FOREIGN KEY (role_id) REFERENCES GO_ROLE(id) ON DELETE CASCADE
);

CREATE TABLE GO_SIGN_UP (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    username VARCHAR(255) NOT NULL,
    password TEXT NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    current_verification_token VARCHAR(255) NOT NULL,
    expired_verification_token_date TIMESTAMP NOT NULL
);
