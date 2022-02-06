--liquibase formatted sql

--changeset krzysztof:5
CREATE TABLE reset_passwords(
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	user_id BIGINT,
    token INT NOT NULL UNIQUE,
    expiry_date DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);