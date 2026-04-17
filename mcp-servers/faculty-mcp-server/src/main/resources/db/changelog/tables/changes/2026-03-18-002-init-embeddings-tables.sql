--liquibase formatted sql

--changeset roman:002

CREATE TABLE IF NOT EXISTS qwen_0_6b_document_embeddings
(
    id        UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content   TEXT,
    metadata  JSONB,
    embedding VECTOR(1024)
);