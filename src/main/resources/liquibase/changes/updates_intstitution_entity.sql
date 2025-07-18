ALTER TABLE institution
  ADD COLUMN IF NOT EXISTS description VARCHAR(255) NOT NULL DEFAULT '';

ALTER TABLE institution
  ADD COLUMN IF NOT EXISTS owner_name VARCHAR(255) NOT NULL DEFAULT '';

ALTER TABLE institution
  ADD COLUMN IF NOT EXISTS location VARCHAR(255) NOT NULL DEFAULT '';

ALTER TABLE institution
  ADD COLUMN IF NOT EXISTS reg_no VARCHAR(255) NOT NULL UNIQUE;

ALTER TABLE institution
  ADD COLUMN IF NOT EXISTS year_established INTEGER;

ALTER TABLE institution
  ADD COLUMN IF NOT EXISTS institution_type VARCHAR(255) NOT NULL DEFAULT '';
