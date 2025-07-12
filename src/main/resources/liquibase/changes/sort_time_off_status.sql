create or replace procedure check_time_off_status() as
$$
begin
    with unavailable_employees as (select emp.id
                                   from employee emp
                                            left join time_off_requests tor on tor.employee_id = emp.id
                                   where emp.status = 'UNAVAILABLE'
                                     and tor.end_date < current_date)
    update employee e
    set status = 'AVAILABLE'
    where e.id in (select id from unavailable_employees);
end;

$$ language plpgsql;