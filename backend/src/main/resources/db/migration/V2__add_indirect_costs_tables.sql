CREATE TABLE IF NOT EXISTS cost_centers (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT cost_centers_pkey PRIMARY KEY (id),
    CONSTRAINT uk_cost_centers_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS indirect_cost_categories (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT indirect_cost_categories_pkey PRIMARY KEY (id),
    CONSTRAINT uk_indirect_cost_categories_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS indirect_costs (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    category_id UUID NOT NULL,
    amount NUMERIC(38, 2) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    distribution_criterion VARCHAR(255) NOT NULL,
    cost_center_id UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    CONSTRAINT indirect_costs_pkey PRIMARY KEY (id),
    CONSTRAINT fk_indirect_costs_category FOREIGN KEY (category_id) REFERENCES indirect_cost_categories (id),
    CONSTRAINT fk_indirect_costs_cost_center FOREIGN KEY (cost_center_id) REFERENCES cost_centers (id)
);

CREATE TABLE IF NOT EXISTS indirect_cost_history (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    indirect_cost_id UUID NOT NULL,
    amount_previous NUMERIC(38, 2),
    amount_new NUMERIC(38, 2),
    start_date_previous DATE,
    start_date_new DATE,
    end_date_previous DATE,
    end_date_new DATE,
    distribution_criterion_previous VARCHAR(255),
    distribution_criterion_new VARCHAR(255),
    modified_by VARCHAR(255),
    CONSTRAINT indirect_cost_history_pkey PRIMARY KEY (id)
);
