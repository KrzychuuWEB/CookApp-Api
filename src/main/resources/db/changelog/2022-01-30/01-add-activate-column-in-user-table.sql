--liquibase formatted sql

--changeset krzysztof:4
ALTER TABLE users ADD COLUMN activation VARCHAR(40)