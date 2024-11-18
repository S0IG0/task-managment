create table users
(
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    id         uuid         not null,
    email      varchar(255) not null unique,
    username   varchar(255) not null unique,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    password   varchar(255) not null,
    roles      varchar(255) array not null,
    primary key (id)
)