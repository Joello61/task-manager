CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) not null,
    description VARCHAR(255) not null,
    user_id BIGINT not null references users(id),
    date_creation TIMESTAMP NOT NULL,
    done BOOLEAN
);