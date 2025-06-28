alter table shift_swap_request
    add column if not exists requested_by int8;

alter table shift_swap_request
    add column if not exists approved_by int8;

alter table shift_swap_request
    add column if not exists requested_on timestamp default now();

alter table shift_swap_request
    add column if not exists approved_on timestamp;

alter table shift_assignment
    add column if not exists updated_by int;