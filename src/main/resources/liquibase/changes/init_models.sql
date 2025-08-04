create table if not exists employee
(
    id            serial primary key,
    name          varchar(255) not null,
    department    varchar(255) not null,
    email         varchar(255) not null,
    status        varchar(255) not null,
    created_at    timestamp default now(),
    days_off_used int          not null,
    created_by    int8
);

create table if not exists institution
(
    id         serial primary key,
    name       varchar(255) not null,
    code       varchar(255) not null,
    created_at timestamp default now(),
    created_by int8
);

create table if not exists shift_assignment
(
    id          serial primary key,
    shift_id    int,
    employee_id int,
    status      varchar(255),
    assigned_by int8,
    updated_at  timestamp default now()
);
create table if not exists shift_availability
(
    id             serial primary key,
    employee_id    int,
    day_of_week    varchar(255),
    available_from timestamp,
    available_to   timestamp
);
create table if not exists shift_swap_request
(
    id            serial primary key,
    from_employee int,
    to_employee   int,
    shift_id      int,
    status        varchar(255),
    approved_by   int8
);

create table if not exists schedule
(
    id          serial primary key,
    start_date  timestamp not null,
    end_date    timestamp not null,
    employee_id int
);

create table if not exists departments
(
    id             serial primary key,
    name           varchar(255) not null,
    institution_id int8         not null,
    created_at     timestamp default now(),
    created_by     int8
);

create table if not exists shift
(
    id             serial primary key,
    department_id  int,
    type           varchar(255),
    name           varchar(255),
    shift_duration int,
    start_time     timestamp,
    end_time       timestamp,
    created_at     timestamp default now(),
    created_by     int8,
    max_people     int
);