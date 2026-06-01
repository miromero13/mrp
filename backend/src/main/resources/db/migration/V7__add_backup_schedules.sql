CREATE TABLE IF NOT EXISTS backup_schedules (
    id BIGSERIAL NOT NULL,
    enterprise_id UUID NOT NULL,
    cron_expression VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    CONSTRAINT backup_schedules_pkey PRIMARY KEY (id),
    CONSTRAINT fk_backup_schedules_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprises (id) ON DELETE CASCADE,
    CONSTRAINT uq_backup_schedules_enterprise UNIQUE (enterprise_id)
);
