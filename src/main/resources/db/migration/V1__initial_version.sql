CREATE extension IF NOT EXISTS "uuid-ossp";

CREATE TABLE roles (
    code text PRIMARY KEY,
    name text NOT NULL
);

INSERT INTO roles(code, name) VALUES
       ('reader', 'Reader'),
       ('manager', 'Manager'),
       ('admin', 'Admin');

CREATE TABLE users (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name text NOT NULL,
    last_name text NOT NULL,
    age integer NOT NULL
);

CREATE TABLE users_to_roles (
    users_id uuid NOT NULL REFERENCES users(id),
    roles_code text NOT NULL REFERENCES roles(code)
);
