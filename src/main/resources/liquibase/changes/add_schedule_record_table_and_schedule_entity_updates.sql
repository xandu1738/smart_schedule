-- Add columns to 'schedule' table if they do not exist
ALTER TABLE schedule
  ADD COLUMN IF NOT EXISTS department_id INTEGER;

ALTER TABLE schedule
  ADD COLUMN IF NOT EXISTS institution_id INTEGER;

-- Drop employee_id from 'schedule' table if it exists
ALTER TABLE schedule
DROP COLUMN IF EXISTS employee_id;


CREATE TABLE IF NOT EXISTS schedule_record
(
  id SERIAL PRIMARY KEY,
  schedule_id INTEGER,
  employee_id INTEGER,
  shift_id INTEGER,
  date_created TIMESTAMP,
  time_off_id INTEGER
);
