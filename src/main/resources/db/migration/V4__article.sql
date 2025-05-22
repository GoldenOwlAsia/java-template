CREATE TABLE GO_ARTICLE (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_article_author
        FOREIGN KEY (author_id) REFERENCES GO_USER(id) ON DELETE CASCADE
);
