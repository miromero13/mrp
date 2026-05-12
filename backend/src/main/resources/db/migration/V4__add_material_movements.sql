CREATE TABLE IF NOT EXISTS material_movements (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    movement_type VARCHAR(32) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    material_id UUID NOT NULL,
    CONSTRAINT material_movements_pkey PRIMARY KEY (id),
    CONSTRAINT fk_material_movements_material FOREIGN KEY (material_id) REFERENCES materials (id)
);

CREATE TABLE IF NOT EXISTS material_movements_detail (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    quantity NUMERIC(14,2) NOT NULL,
    unit_price NUMERIC(14,2) NOT NULL,
    material_movement_id UUID NOT NULL,
    CONSTRAINT material_movements_detail_pkey PRIMARY KEY (id),
    CONSTRAINT fk_material_movements_detail_movement FOREIGN KEY (material_movement_id) REFERENCES material_movements (id) ON DELETE CASCADE
);
