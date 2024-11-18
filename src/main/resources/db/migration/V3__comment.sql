create table comment
(
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    id         uuid not null,
    task_id    uuid not null,
    author_id  uuid not null,
    text       TEXT not null,
    primary key (id)
);

alter table if exists comment
    add constraint comment_author_id_fk
        foreign key (author_id) references users;
alter table if exists comment
    add constraint comment_task_id_fk
        foreign key (task_id) references task;