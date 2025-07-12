-- back up data
CREATE TABLE schedule_record_backup AS
SELECT * FROM schedule_record;

-- drop range partitioning
DO $$
DECLARE
child text;
BEGIN
FOR child IN
SELECT inhrelid::regclass::text
FROM pg_inherits
WHERE inhparent = 'schedule_record'::regclass
  LOOP
    EXECUTE format('DROP TABLE IF EXISTS %I CASCADE;', child);
END LOOP;
END$$;


DROP TABLE IF EXISTS schedule_record CASCADE;


CREATE TABLE schedule_record (
                               id                  SERIAL PRIMARY KEY,
                               schedule_id         BIGINT      NOT NULL,
                               employee_id         BIGINT      NOT NULL,
                               shift_id            BIGINT,
                               time_off_request_id BIGINT,
                               start_time          TIMESTAMP   NOT NULL,
                               end_time            TIMESTAMP   NOT NULL,
                               date_created        TIMESTAMP   DEFAULT now() NOT NULL,
                               date_updated        TIMESTAMP   DEFAULT now() NOT NULL,
                               active              BOOLEAN     DEFAULT true NOT NULL
);


INSERT INTO schedule_record (
  schedule_id, employee_id, shift_id, time_off_request_id,
  start_time, end_time, date_created, date_updated, active
)
SELECT
  schedule_id, employee_id, shift_id, time_off_request_id,
  start_time, end_time, date_created, date_updated, active
FROM schedule_record_backup;


DROP TABLE schedule_record_backup;
