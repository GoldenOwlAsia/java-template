ALTER TABLE GO_USER DROP CONSTRAINT IF EXISTS go_user_username_key;
ALTER TABLE GO_USER DROP CONSTRAINT IF EXISTS go_user_email_key;

-- Soft-deleted users must not block re-registration of the same username/email.
CREATE UNIQUE INDEX IF NOT EXISTS uq_go_user_username_active
    ON GO_USER (username)
    WHERE deleted IS NOT TRUE;

CREATE UNIQUE INDEX IF NOT EXISTS uq_go_user_email_active
    ON GO_USER (email)
    WHERE deleted IS NOT TRUE;
