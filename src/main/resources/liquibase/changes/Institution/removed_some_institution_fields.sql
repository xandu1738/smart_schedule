ALTER TABLE institution
  DROP COLUMN IF EXISTS owner_name;

ALTER TABLE institution
DROP COLUMN IF EXISTS location;
