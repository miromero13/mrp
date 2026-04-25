ALTER TABLE users
    ADD COLUMN IF NOT EXISTS enterprise_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_schema = 'public'
          AND table_name = 'users'
          AND constraint_name = 'fk_users_enterprise'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT fk_users_enterprise
            FOREIGN KEY (enterprise_id) REFERENCES enterprises (id);
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'employee_shifts'
          AND column_name = 'employee_id'
    ) THEN
        ALTER TABLE employee_shifts RENAME COLUMN employee_id TO user_id;
    END IF;
END $$;

ALTER TABLE employee_shifts
    ALTER COLUMN user_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_schema = 'public'
          AND table_name = 'employee_shifts'
          AND constraint_name = 'fk_employee_shift_user'
    ) THEN
        ALTER TABLE employee_shifts
            ADD CONSTRAINT fk_employee_shift_user
            FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_schema = 'public'
          AND table_name = 'employee_shifts'
          AND constraint_name = 'fk_employee_shift_workshift'
    ) THEN
        ALTER TABLE employee_shifts
            ADD CONSTRAINT fk_employee_shift_workshift
            FOREIGN KEY (work_shift_id) REFERENCES work_shifts (id) ON DELETE CASCADE;
    END IF;
END $$;

DROP TABLE IF EXISTS employees CASCADE;
