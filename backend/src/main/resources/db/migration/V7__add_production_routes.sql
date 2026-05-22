CREATE TABLE IF NOT EXISTS production_routes (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    product_version_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT production_routes_pkey PRIMARY KEY (id),
    CONSTRAINT fk_production_routes_product_version FOREIGN KEY (product_version_id) REFERENCES product_versions (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS route_operations (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    production_route_id UUID NOT NULL,
    machine_id UUID NULL,
    operation_order INTEGER NOT NULL,
    description VARCHAR(255) NOT NULL,
    setup_time_minutes NUMERIC(8,2) NOT NULL,
    run_time_minutes NUMERIC(8,2) NOT NULL,
    CONSTRAINT route_operations_pkey PRIMARY KEY (id),
    CONSTRAINT fk_route_operations_route FOREIGN KEY (production_route_id) REFERENCES production_routes (id) ON DELETE CASCADE,
    CONSTRAINT fk_route_operations_machine FOREIGN KEY (machine_id) REFERENCES enterprise_machines (id),
    CONSTRAINT uk_route_operations_route_order UNIQUE (production_route_id, operation_order)
);

CREATE INDEX IF NOT EXISTS idx_production_routes_product_version_id ON production_routes (product_version_id);
CREATE INDEX IF NOT EXISTS idx_route_operations_route_id ON route_operations (production_route_id);
