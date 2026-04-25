CREATE TABLE IF NOT EXISTS enterprise_machines (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    cost NUMERIC(14,2) NOT NULL,
    enterprise_id UUID NOT NULL,
    CONSTRAINT enterprise_machines_pkey PRIMARY KEY (id),
    CONSTRAINT fk_enterprise_machines_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprises (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS enterprise_indirect_costs (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    amount NUMERIC(14,2) NOT NULL,
    enterprise_id UUID NOT NULL,
    CONSTRAINT enterprise_indirect_costs_pkey PRIMARY KEY (id),
    CONSTRAINT fk_enterprise_indirect_costs_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprises (id) ON DELETE CASCADE
);
