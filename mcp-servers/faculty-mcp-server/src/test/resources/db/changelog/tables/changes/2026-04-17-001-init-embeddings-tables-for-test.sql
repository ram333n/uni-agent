--liquibase formatted sql

--changeset roman:002

CREATE TABLE IF NOT EXISTS qwen_0_6b_document_embeddings
(
    id        UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content   TEXT,
    metadata  JSONB,
    embedding VECTOR(1024)
);
--
-- CREATE INDEX IF NOT EXISTS qwen_0_6b_document_embeddings_hnsw_idx
--     ON qwen_0_6b_document_embeddings
--         USING hnsw (embedding vector_cosine_ops)
--     WITH (m = 16, ef_construction = 64);

------

CREATE TABLE IF NOT EXISTS qwen_4b_document_embeddings
(
    id        UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content   TEXT,
    metadata  JSONB,
    embedding VECTOR(2560)
);

------

CREATE TABLE IF NOT EXISTS bge_m3_document_embeddings
(
    id        UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content   TEXT,
    metadata  JSONB,
    embedding VECTOR(1024)
);
