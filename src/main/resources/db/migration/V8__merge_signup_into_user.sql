-- Merge signup lifecycle into GO_USER and retire GO_SIGN_UP.

ALTER TABLE GO_USER
    ADD COLUMN IF NOT EXISTS status VARCHAR(255),
    ADD COLUMN IF NOT EXISTS current_verification_token VARCHAR(255),
    ADD COLUMN IF NOT EXISTS expired_verification_token_date TIMESTAMP;

UPDATE GO_USER
SET status = 'ACTIVE'
WHERE status IS NULL;

-- Carry over pending registrations that never became users.
INSERT INTO GO_USER (
    id, ol, created_at, created_by, last_modified_at, last_modified_by,
    username, password, email, name, oauth_id, deleted,
    status, current_verification_token, expired_verification_token_date
)
SELECT
    s.id,
    s.ol,
    s.created_at,
    s.created_by,
    s.last_modified_at,
    s.last_modified_by,
    s.username,
    s.password,
    s.email,
    s.name,
    NULL,
    FALSE,
    'PENDING',
    s.current_verification_token,
    s.expired_verification_token_date
FROM GO_SIGN_UP s
WHERE s.status = 'PENDING'
  AND NOT EXISTS (
      SELECT 1 FROM GO_USER u WHERE u.username = s.username OR u.email = s.email
  );

ALTER TABLE GO_USER
    ALTER COLUMN status SET NOT NULL;

DROP TABLE IF EXISTS GO_SIGN_UP;
