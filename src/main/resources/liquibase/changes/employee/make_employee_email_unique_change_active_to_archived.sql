ALTER TABLE employee
    ADD CONSTRAINT employee_email_key UNIQUE (email);

ALTER TABLE employee
    RENAME COLUMN active TO archived;