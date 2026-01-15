-- Database Update Script for ModelData Evolution
-- Execute this script in your PostgreSQL query tool to update the database schema.

-- 1. Table: madre
-- Expanded to include detailed information about the mother.
CREATE TABLE IF NOT EXISTS madre (
    id_madre SERIAL PRIMARY KEY,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100),
    edad INT,
    estado_civil VARCHAR(50)
);

-- If table exists but missing columns (for safety if running on partial DB)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='madre' AND column_name='nombres') THEN
        ALTER TABLE madre ADD COLUMN nombres VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='madre' AND column_name='edad') THEN
        ALTER TABLE madre ADD COLUMN edad INT;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='madre' AND column_name='estado_civil') THEN
        ALTER TABLE madre ADD COLUMN estado_civil VARCHAR(50);
    END IF;
END $$;

-- 2. Table: nacimiento
-- Stores individual birth records (microdata).
CREATE TABLE IF NOT EXISTS nacimiento (
    id_nacimiento SERIAL PRIMARY KEY,
    id_madre INT NOT NULL,
    id_provincia VARCHAR(10) NOT NULL,
    id_instruccion VARCHAR(10) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    anio INT NOT NULL,
    sexo VARCHAR(20),
    tipo_parto VARCHAR(50),
    CONSTRAINT fk_madre FOREIGN KEY (id_madre) REFERENCES madre(id_madre),
    CONSTRAINT fk_provincia FOREIGN KEY (id_provincia) REFERENCES provincias(id_provincia),
    CONSTRAINT fk_instruccion FOREIGN KEY (id_instruccion) REFERENCES instrucciones(id_instruccion)
);

-- If table exists but missing columns
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='nacimiento' AND column_name='sexo') THEN
        ALTER TABLE nacimiento ADD COLUMN sexo VARCHAR(20);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='nacimiento' AND column_name='tipo_parto') THEN
        ALTER TABLE nacimiento ADD COLUMN tipo_parto VARCHAR(50);
    END IF;
END $$;

-- Indexing for performance on consolidation queries
CREATE INDEX IF NOT EXISTS idx_nacimiento_anio ON nacimiento(anio);
CREATE INDEX IF NOT EXISTS idx_nacimiento_provincia ON nacimiento(id_provincia);
CREATE INDEX IF NOT EXISTS idx_nacimiento_instruccion ON nacimiento(id_instruccion);


-- LOGIN SYSTEM TABLES --
    
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- BCrypt Hash
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    id_rol INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE -- e.g. 'ADMIN', 'OPERATOR'
);

CREATE TABLE IF NOT EXISTS usuario_rol (
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol) ON DELETE CASCADE
);

-- INITIAL SEED DATA
INSERT INTO roles (nombre_rol)
VALUES ('ADMIN'), ('OPERADOR')
ON CONFLICT (nombre_rol) DO NOTHING;

-- 3. Verification of existing aggregated tables (Ensure they exist)
CREATE TABLE IF NOT EXISTS nacimientos_provincias (
    id_nacimiento SERIAL PRIMARY KEY,
    anio INT NOT NULL,
    id_provincia VARCHAR(10) NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (id_provincia) REFERENCES provincias(id_provincia)
);

CREATE TABLE IF NOT EXISTS nacimientos_instruccion (
    id_nacimiento SERIAL PRIMARY KEY,
    anio INT NOT NULL,
    id_instruccion VARCHAR(10) NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (id_instruccion) REFERENCES instrucciones(id_instruccion)
);
