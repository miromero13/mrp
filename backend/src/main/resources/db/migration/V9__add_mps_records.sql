CREATE TABLE IF NOT EXISTS mps_records (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    enterprise_id UUID NOT NULL,
    product_id UUID NOT NULL,
    period_mode VARCHAR(50) NOT NULL,
    period_start_date DATE NOT NULL,
    forecast_demand NUMERIC(14,2) NOT NULL,
    allocated_demand NUMERIC(14,2) NOT NULL,
    planned_production NUMERIC(14,2) NOT NULL,
    projected_available_balance NUMERIC(14,2) NOT NULL,
    CONSTRAINT mps_records_pkey PRIMARY KEY (id),
    CONSTRAINT fk_mps_records_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprises (id) ON DELETE CASCADE,
    CONSTRAINT fk_mps_records_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT uk_mps_records_enterprise_product_mode_period UNIQUE (enterprise_id, product_id, period_mode, period_start_date)
);

CREATE INDEX IF NOT EXISTS idx_mps_records_enterprise_id ON mps_records (enterprise_id);
CREATE INDEX IF NOT EXISTS idx_mps_records_product_id ON mps_records (product_id);
CREATE INDEX IF NOT EXISTS idx_mps_records_period_start_date ON mps_records (period_start_date);
