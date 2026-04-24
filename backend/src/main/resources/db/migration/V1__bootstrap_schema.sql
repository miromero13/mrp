
CREATE TABLE IF NOT EXISTS enterprises (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    nit VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    CONSTRAINT enterprise_pkey PRIMARY KEY (id),
    CONSTRAINT uk_enterprise_nit UNIQUE (nit)
);

CREATE TABLE IF NOT EXISTS permission (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT permission_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS role (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id),
    CONSTRAINT uk_role_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    address VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    gender VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    role_id UUID NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES role (id)
);

CREATE TABLE IF NOT EXISTS permission_role (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    CONSTRAINT permission_role_pkey PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_permission_role_role FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT fk_permission_role_permission FOREIGN KEY (permission_id) REFERENCES permission (id)
);

CREATE TABLE employees (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

   user_id UUID UNIQUE, -- relación 1-1 con users

   internal_code VARCHAR(50) NOT NULL,
   position VARCHAR(100),
   hourly_rate DECIMAL(10,2),

   hire_date DATE,
   status BOOLEAN NOT NULL,

   created_at TIMESTAMP NOT NULL DEFAULT NOW(),
   updated_at TIMESTAMP,

   CONSTRAINT fk_employee_user
       FOREIGN KEY (user_id)
           REFERENCES users(id)
           ON DELETE CASCADE
);

CREATE TABLE work_shifts (
     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

     name VARCHAR(100) NOT NULL,

     start_date TIMESTAMP NOT NULL,
     end_date TIMESTAMP NOT NULL,

     enterprise_id UUID NOT NULL,

     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMP,

     CONSTRAINT fk_workshift_enterprise
         FOREIGN KEY (enterprise_id)
             REFERENCES enterprises(id)
             ON DELETE CASCADE
);

CREATE TABLE employee_shifts (
     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

     employee_id UUID NOT NULL,
     work_shift_id UUID NOT NULL,

     day_of_week VARCHAR(20) NOT NULL, -- 1=Lunes ... 7=Domingo

     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMP,

     CONSTRAINT fk_employee_shift_employee
         FOREIGN KEY (employee_id)
             REFERENCES employees(id)
             ON DELETE CASCADE,

     CONSTRAINT fk_employee_shift_workshift
         FOREIGN KEY (work_shift_id)
             REFERENCES work_shifts(id)
             ON DELETE CASCADE
);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'permiso_rol'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'permission_role'
    ) THEN
        ALTER TABLE permiso_rol RENAME TO permission_role;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'permission_role'
          AND column_name = 'rol_id'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'permission_role'
          AND column_name = 'role_id'
    ) THEN
        ALTER TABLE permission_role RENAME COLUMN rol_id TO role_id;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'permission_role'
          AND column_name = 'permiso_id'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'permission_role'
          AND column_name = 'permission_id'
    ) THEN
        ALTER TABLE permission_role RENAME COLUMN permiso_id TO permission_id;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'role'
          AND column_name = 'nombre'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'role'
          AND column_name = 'name'
    ) THEN
        ALTER TABLE role RENAME COLUMN nombre TO name;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'rol_id'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'role_id'
    ) THEN
        ALTER TABLE users RENAME COLUMN rol_id TO role_id;
    END IF;
END $$;