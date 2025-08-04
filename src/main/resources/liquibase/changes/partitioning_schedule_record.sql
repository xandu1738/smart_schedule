drop table if exists schedule_record;

create table if not exists schedule_record
(
    id                  serial    not null,
    schedule_id         bigint    not null,
    employee_id         bigint    not null,
    shift_id            bigint    not null,
    time_off_request_id bigint,
    start_time          timestamp not null,
    end_time            timestamp not null,
    date_created        timestamp not null default now(),
    date_updated        timestamp not null default now(),
    active              boolean            default true not null,
    primary key (id, date_created)
) partition by range (date_created);

-- create table if not exists schedule_record_2025_07 partition of schedule_record for values from ('2025-07-01') to ('2025-08-01');

-- create table if not exists schedule_record_2025_08 partition of schedule_record for values from ('2025-08-01') to ('2025-09-01');

create or replace function auto_create_schedule_record_partition() returns void as
$$
declare
    start_date     date;
    end_date       date;
    partition_name text;
begin
    start_date := date_trunc('month', current_date + interval '1 month');
    end_date := start_date + interval '1 month';

    partition_name := 'schedule_record_' || to_char(start_date, 'YYYY_MM');
    -- first check if partition already exists
    if exists(select from information_schema.tables where table_name = partition_name) then
        raise notice 'partition % already exists', partition_name;
    else
        execute format('create table if not exists %I partition of schedule_record for values from (%L) to (%L)', partition_name, start_date, end_date);
        raise notice ' Successfully created partition %', partition_name;
    end if;
end;
$$ language plpgsql;
