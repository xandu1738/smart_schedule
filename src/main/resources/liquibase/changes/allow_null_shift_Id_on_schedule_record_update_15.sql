ALTER TABLE schedule_record
  ALTER COLUMN shift_id DROP NOT NULL;

ALTER TABLE schedule_record
  ALTER COLUMN time_off_request_id DROP NOT NULL;
