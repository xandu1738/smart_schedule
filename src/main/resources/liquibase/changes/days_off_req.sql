create table if not exists time_off_requests
(
    id           serial primary key,
    employee_id  integer     not null,
    start_date   date        not null,
    end_date     date        not null,
    status       varchar(20) not null,
    approved_by  integer,
    requested_by integer,
    requested_on timestamp default now(),
    approved_on  timestamp
);

alter table time_off_requests add column if not exists reason text;