ALTER table departments add COLUMN if not exists active boolean;
ALTER table employee add COLUMN if not exists active boolean;
ALTER table shift drop COLUMN if exists shift_duration;


