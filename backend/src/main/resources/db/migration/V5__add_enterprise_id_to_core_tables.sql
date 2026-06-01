-- V5__add_enterprise_id_to_core_tables.sql

ALTER TABLE users ADD COLUMN enterprise_id UUID;
ALTER TABLE users ADD CONSTRAINT fk_users_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise (id);

ALTER TABLE materials ADD COLUMN enterprise_id UUID;
ALTER TABLE materials ADD CONSTRAINT fk_materials_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise (id);

ALTER TABLE products ADD COLUMN enterprise_id UUID;
ALTER TABLE products ADD CONSTRAINT fk_products_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise (id);

ALTER TABLE inventory_moment ADD COLUMN enterprise_id UUID;
ALTER TABLE inventory_moment ADD CONSTRAINT fk_inventory_moment_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise (id);

-- ==========================================
-- SEEDS (Datos de Prueba)
-- ==========================================

-- 1. Insertar Empresa de prueba
INSERT INTO enterprise (id, created_at, updated_at, name, nit, address)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW(), 'Empresa Demo S.A.', '123456789-0', 'Calle Falsa 123')
ON CONFLICT (nit) DO NOTHING;

-- 2. Insertar Lote de prueba para un Material (Madera de Roble, insertado en V3)
INSERT INTO batches (
    id, created_at, updated_at, batch_number, status, 
    production_date, expiration_date, current_stock, 
    material_id, product_id, enterprise_id
)
VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 
    NOW(), 
    NOW(), 
    'LOTE-MAT-1001', 
    'APPROVED', 
    CURRENT_DATE - INTERVAL '10 days', 
    CURRENT_DATE + INTERVAL '365 days', 
    100.00, 
    '11111111-1111-1111-1111-111111111111', 
    NULL, 
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
)
ON CONFLICT (batch_number) DO NOTHING;

-- 3. Insertar Lote de prueba para un Producto Terminado (Mesa de Comedor, insertado en V3)
INSERT INTO batches (
    id, created_at, updated_at, batch_number, status, 
    production_date, expiration_date, current_stock, 
    material_id, product_id, enterprise_id
)
VALUES (
    'cccccccc-cccc-cccc-cccc-cccccccccccc', 
    NOW(), 
    NOW(), 
    'LOTE-PROD-2001', 
    'APPROVED', 
    CURRENT_DATE - INTERVAL '2 days', 
    CURRENT_DATE + INTERVAL '180 days', 
    50.00, 
    NULL, 
    '44444444-4444-4444-4444-444444444444', 
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
)
ON CONFLICT (batch_number) DO NOTHING;
