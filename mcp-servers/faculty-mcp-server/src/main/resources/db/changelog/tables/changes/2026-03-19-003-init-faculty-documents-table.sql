--liquibase formatted sql

--changeset roman:003

CREATE TABLE IF NOT EXISTS faculty_documents
(
    id           BIGSERIAL     PRIMARY KEY,
    file_name    VARCHAR(512)  NOT NULL,
    file_path    VARCHAR(1024) NOT NULL UNIQUE,
    content_hash VARCHAR(64)   NOT NULL,
    uploaded_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS
    faculty_documents$content_hash_idx ON faculty_documents (content_hash);
