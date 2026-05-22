CREATE TABLE IF NOT EXISTS batches (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    batch_number VARCHAR(255) NOT NULL,
    material_id UUID NULL,
    product_id UUID NULL,
    production_date DATE NOT NULL,
    expiration_date DATE NULL,
    current_stock NUMERIC(14,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT batches_pkey PRIMARY KEY (id),
    CONSTRAINT uk_batches_batch_number UNIQUE (batch_number),
    CONSTRAINT fk_batches_material FOREIGN KEY (material_id) REFERENCES materials (id),
    CONSTRAINT fk_batches_product FOREIGN KEY (product_id) REFERENCES products (id)
);

ALTER TABLE material_movements_detail
    ADD COLUMN IF NOT EXISTS batch_id UUID NULL;

ALTER TABLE material_movements_detail
    ADD CONSTRAINT fk_material_movements_detail_batch FOREIGN KEY (batch_id) REFERENCES batches (id);

CREATE INDEX IF NOT EXISTS idx_batches_material_id ON batches (material_id);
CREATE INDEX IF NOT EXISTS idx_batches_product_id ON batches (product_id);
CREATE INDEX IF NOT EXISTS idx_batches_status ON batches (status);
CREATE INDEX IF NOT EXISTS idx_material_movements_detail_batch_id ON material_movements_detail (batch_id);
