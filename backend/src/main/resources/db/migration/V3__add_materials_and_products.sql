CREATE TABLE IF NOT EXISTS materials (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    code VARCHAR(255) NOT NULL,
    unit_of_measure VARCHAR(255) NOT NULL,
    stock_min NUMERIC(14,2) NOT NULL,
    stock_current NUMERIC(14,2) NOT NULL,
    enterprise_id UUID NOT NULL,
    CONSTRAINT materials_pkey PRIMARY KEY (id),
    CONSTRAINT fk_materials_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprises (id),
    CONSTRAINT uk_material_code_enterprise UNIQUE (enterprise_id, code)
);

CREATE TABLE IF NOT EXISTS products (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    production_cost NUMERIC(14,2) NOT NULL,
    sale_price NUMERIC(14,2) NOT NULL,
    enterprise_id UUID NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id),
    CONSTRAINT fk_products_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprises (id),
    CONSTRAINT uk_product_name_enterprise UNIQUE (enterprise_id, name)
);

CREATE TABLE IF NOT EXISTS product_materials (
    product_id UUID NOT NULL,
    material_id UUID NOT NULL,
    CONSTRAINT product_materials_pkey PRIMARY KEY (product_id, material_id),
    CONSTRAINT fk_product_materials_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_product_materials_material FOREIGN KEY (material_id) REFERENCES materials (id)
);

CREATE TABLE IF NOT EXISTS product_versions (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    product_id UUID NOT NULL,
    CONSTRAINT product_versions_pkey PRIMARY KEY (id),
    CONSTRAINT fk_product_versions_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
