-- V4__create_enterprise_and_batches.sql

CREATE TABLE IF NOT EXISTS enterprise (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    nit VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    CONSTRAINT enterprise_pkey PRIMARY KEY (id),
    CONSTRAINT uk_enterprise_nit UNIQUE (nit)
);

CREATE TABLE IF NOT EXISTS batches (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    batch_number VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'APPROVED',
    production_date DATE,
    expiration_date DATE,
    current_stock NUMERIC(19, 4) DEFAULT 0,
    material_id UUID,
    product_id UUID,
    enterprise_id UUID,
    CONSTRAINT batches_pkey PRIMARY KEY (id),
    CONSTRAINT uk_batches_batch_number UNIQUE (batch_number),
    CONSTRAINT fk_batches_material FOREIGN KEY (material_id) REFERENCES materials (id),
    CONSTRAINT fk_batches_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_batches_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise (id),
    CONSTRAINT chk_batch_material_or_product CHECK (
        (material_id IS NOT NULL AND product_id IS NULL) OR
        (material_id IS NULL AND product_id IS NOT NULL)
    )
);

-- Default Batch for legacy data
INSERT INTO batches (id, created_at, updated_at, batch_number, status, current_stock, material_id)
VALUES ('00000000-0000-0000-0000-000000000000', NOW(), NOW(), 'LOTE-DEFAULT', 'APPROVED', 0, '11111111-1111-1111-1111-111111111111')
ON CONFLICT (batch_number) DO NOTHING;

-- Now update inventory_moment_detail
ALTER TABLE inventory_moment_detail ADD COLUMN batch_id UUID;
UPDATE inventory_moment_detail SET batch_id = '00000000-0000-0000-0000-000000000000' WHERE batch_id IS NULL;
ALTER TABLE inventory_moment_detail ALTER COLUMN batch_id SET NOT NULL;
ALTER TABLE inventory_moment_detail ADD CONSTRAINT fk_inventory_moment_detail_batch FOREIGN KEY (batch_id) REFERENCES batches (id);
