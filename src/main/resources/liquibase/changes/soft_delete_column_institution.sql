ALTER table INSTITUTION
  add column if not exists department_id Long;
ALTER table INSTITUTION
  add column if not exists institution_id Long;

-- created new table for schedule records
CREATE TABLE IF NOT EXISTS schedule_record
(
  id
  SERIAL
  PRIMARY
  KEY,
  schedule_id
  INTEGER
  NOT
  NULL,
  employee_id
  INTEGER
  NOT
  NULL,
  shift_id
  INTEGER
  NOT
  NULL,
  date_created
  TIMESTAMP
  NOT
  NULL,
  time_off_id
  INTEGER
  NOT
  NULL
);

