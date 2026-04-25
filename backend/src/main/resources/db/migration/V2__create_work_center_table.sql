CREATE TABLE work_centers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    plant VARCHAR(100) NOT NULL,
    production_line VARCHAR(100),
    resource_type VARCHAR(50) NOT NULL,
    capacity NUMERIC(10, 2) NOT NULL,
    cost_per_hour NUMERIC(10, 2) NOT NULL,
    target_efficiency NUMERIC(5, 2) NOT NULL,
    current_oee NUMERIC(5, 2),
    is_bottleneck BOOLEAN DEFAULT FALSE,
    is_critical_resource BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    calendar VARCHAR(255),
    last_maintenance_date DATE,
    next_maintenance_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_work_centers_plant ON work_centers(plant);
CREATE INDEX idx_work_centers_production_line ON work_centers(production_line);
CREATE INDEX idx_work_centers_status ON work_centers(status);
CREATE INDEX idx_work_centers_code ON work_centers(code);
