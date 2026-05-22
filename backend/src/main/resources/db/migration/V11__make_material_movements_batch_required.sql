WITH existing_batch_by_material AS (
    SELECT DISTINCT ON (mm.material_id)
        mm.material_id,
        b.id AS batch_id
    FROM material_movements_detail d
    JOIN material_movements mm ON mm.id = d.material_movement_id
    JOIN batches b ON b.material_id = mm.material_id
    WHERE d.batch_id IS NULL
    ORDER BY mm.material_id, b.production_date ASC, b.created_at ASC
)
UPDATE material_movements_detail d
SET batch_id = e.batch_id
FROM material_movements mm
JOIN existing_batch_by_material e ON e.material_id = mm.material_id
WHERE d.material_movement_id = mm.id
  AND d.batch_id IS NULL;

WITH materials_without_batch AS (
    SELECT DISTINCT mm.material_id
    FROM material_movements_detail d
    JOIN material_movements mm ON mm.id = d.material_movement_id
    WHERE d.batch_id IS NULL
      AND NOT EXISTS (
          SELECT 1
          FROM batches b
          WHERE b.material_id = mm.material_id
      )
),
legacy_batches AS (
    INSERT INTO batches (
        id,
        created_at,
        updated_at,
        batch_number,
        material_id,
        product_id,
        production_date,
        expiration_date,
        current_stock,
        status
    )
    SELECT
        gen_random_uuid(),
        now(),
        now(),
        'LEGACY-' || m.code || '-' || substr(replace(m.id::text, '-', ''), 1, 8),
        m.id,
        NULL,
        CURRENT_DATE,
        NULL,
        0,
        'APPROVED'
    FROM materials m
    JOIN materials_without_batch mwb ON mwb.material_id = m.id
    RETURNING id, material_id
)
UPDATE material_movements_detail d
SET batch_id = lb.id
FROM material_movements mm
JOIN legacy_batches lb ON lb.material_id = mm.material_id
WHERE d.material_movement_id = mm.id
  AND d.batch_id IS NULL;

ALTER TABLE material_movements_detail
    ALTER COLUMN batch_id SET NOT NULL;
