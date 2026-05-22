CREATE TABLE IF NOT EXISTS demands (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    enterprise_id UUID NOT NULL,
    product_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    quantity NUMERIC(14,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT demands_pkey PRIMARY KEY (id),
    CONSTRAINT fk_demands_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprises (id) ON DELETE CASCADE,
    CONSTRAINT fk_demands_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE INDEX IF NOT EXISTS idx_demands_enterprise_id ON demands (enterprise_id);
CREATE INDEX IF NOT EXISTS idx_demands_product_id ON demands (product_id);
CREATE INDEX IF NOT EXISTS idx_demands_due_date ON demands (due_date);
