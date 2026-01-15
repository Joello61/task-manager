CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) not null ,
    email VARCHAR(255) not null unique,
    password VARCHAR(255) not null ,
    role VARCHAR(255) not null ,
    date_creation TIMESTAMP NOT NULL
);