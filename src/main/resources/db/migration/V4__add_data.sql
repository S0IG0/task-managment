CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


INSERT INTO users (created_at, updated_at, id, email, username, first_name, last_name, password, roles)
VALUES
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), 'admin@mail.ru', 'admin', 'first_name', 'last_name', '$2a$10$.1b7uqqfLZs7Xc0HGBCK9uO6RNPEDcKK/WYj0svgsqcWXFfRZMyqa', ARRAY['ROLE_USER', 'ROLE_ADMIN']),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), 'user1@mail.ru', 'user1', 'John', 'Doe', '$2a$10$.1b7uqqfLZs7Xc0HGBCK9uO6RNPEDcKK/WYj0svgsqcWXFfRZMyqa', ARRAY['ROLE_USER']),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), 'user2@mail.ru', 'user2', 'Jane', 'Doe', '$2a$10$.1b7uqqfLZs7Xc0HGBCK9uO6RNPEDcKK/WYj0svgsqcWXFfRZMyqa', ARRAY['ROLE_USER']),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), 'user3@mail.ru', 'user3', 'Jim', 'Beam', '$2a$10$.1b7uqqfLZs7Xc0HGBCK9uO6RNPEDcKK/WYj0svgsqcWXFfRZMyqa', ARRAY['ROLE_USER']);

INSERT INTO task (created_at, updated_at, id, author_id, executor_id, description, header, priority, status)
VALUES
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), (SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM users WHERE username = 'user2'), 'Task description 1', 'Task header 1', 'HIGH', 'IN_WAITING'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), (SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM users WHERE username = 'user3'), 'Task description 2', 'Task header 2', 'MEDIUM', 'IN_PROGRESS'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), (SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM users WHERE username = 'user1'), 'Task description 3', 'Task header 3', 'LOW', 'COMPLETED');

INSERT INTO comment (created_at, updated_at, id, task_id, author_id, text)
VALUES
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), (SELECT id FROM task WHERE header = 'Task header 1'), (SELECT id FROM users WHERE username = 'admin'), 'Comment for Task 1'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), (SELECT id FROM task WHERE header = 'Task header 2'), (SELECT id FROM users WHERE username = 'admin'), 'Comment for Task 2'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, uuid_generate_v4(), (SELECT id FROM task WHERE header = 'Task header 3'), (SELECT id FROM users WHERE username = 'admin'), 'Comment for Task 3');
