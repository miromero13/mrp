ALTER TABLE materials
    ADD COLUMN IF NOT EXISTS unit_price NUMERIC(14,2) NOT NULL DEFAULT 0.00;

ALTER TABLE enterprise_machines
    ADD COLUMN IF NOT EXISTS serial_number VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE product_versions
    ADD COLUMN IF NOT EXISTS approved BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS used_in_production BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS bom_items (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    product_version_id UUID NOT NULL,
    material_id UUID NOT NULL,
    quantity NUMERIC(14,4) NOT NULL,
    scrap_factor NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT bom_items_pkey PRIMARY KEY (id),
    CONSTRAINT fk_bom_items_product_version FOREIGN KEY (product_version_id) REFERENCES product_versions (id) ON DELETE CASCADE,
    CONSTRAINT fk_bom_items_material FOREIGN KEY (material_id) REFERENCES materials (id),
    CONSTRAINT uk_bom_items_version_material UNIQUE (product_version_id, material_id)
);

DROP TABLE IF EXISTS product_materials;
