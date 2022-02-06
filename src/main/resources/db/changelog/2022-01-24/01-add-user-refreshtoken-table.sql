--liquibase formatted sql

--changeset krzysztof:1
CREATE TABLE users(
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME,
    edited_at DATETIME,
    deleted_at DATETIME,
    enabled BOOLEAN NOT NULL
);

--changeset krzysztof:2
CREATE TABLE authorities(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
	user_id BIGINT NOT NULL,
    authority VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

--changeset krzysztof:3
CREATE TABLE refresh_tokens(
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	user_id BIGINT,
    token VARCHAR(255) NOT NULL,
    used BOOLEAN NOT NULL,
    expiry_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
