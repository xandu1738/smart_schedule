ALTER TABLE employee
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE employee
    ADD COLUMN IF NOT EXISTS updated_by BIGINT;
