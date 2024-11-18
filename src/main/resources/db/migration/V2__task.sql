create table task
(
    created_at  timestamp(6) not null,
    updated_at  timestamp(6) not null,
    id          uuid         not null,
    author_id   uuid         not null,
    executor_id uuid         not null,
    description varchar(255) not null,
    header      varchar(255) not null,
    priority    varchar(255) not null check (priority in ('HIGH', 'MEDIUM', 'LOW')),
    status      varchar(255) not null check (status in ('IN_WAITING', 'IN_PROGRESS', 'COMPLETED')),
    primary key (id)
);

alter table if exists task
    add constraint task_author_id_fk
        foreign key (author_id) references users;
alter table if exists task
    add constraint task_executor_id_fk
        foreign key (executor_id) references users;