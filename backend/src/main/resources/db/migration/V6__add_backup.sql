CREATE TABLE IF NOT EXISTS backups (
    id BIGSERIAL NOT NULL,
    enterprise_id UUID NOT NULL,
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    size_bytes BIGINT,
    type VARCHAR(32),
    state VARCHAR(32),
    start_date TIMESTAMP(6) WITHOUT TIME ZONE,
    end_date TIMESTAMP(6) WITHOUT TIME ZONE,
    message VARCHAR(1024),
    CONSTRAINT backups_pkey PRIMARY KEY (id),
    CONSTRAINT fk_backups_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprises (id) ON DELETE CASCADE
);
