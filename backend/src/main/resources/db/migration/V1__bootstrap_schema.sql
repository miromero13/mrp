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