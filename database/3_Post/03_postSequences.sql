-- =============================================================================
-- 04 - POST (MANTENIMIENTO)
-- SECUENCIAS / SETVAL
--
-- Este script ajusta todas las secuencias asociadas a columnas con sequence
-- (SERIAL / IDENTITY) para que continúen desde el máximo ID existente.
-- =============================================================================

BEGIN;

-- Actualiza todos los id's para seguir del máximo ingresado
DO $$
DECLARE
    r record;
    seq_name text;
    max_id bigint;
BEGIN
    -- Recorre todas las columnas que tienen una sequence asociada (SERIAL / IDENTITY)
    FOR r IN
        SELECT
            n.nspname  AS schema_name,
            c.relname  AS table_name,
            a.attname  AS column_name
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_attribute a ON a.attrelid = c.oid
        WHERE n.nspname = 'public'
          AND c.relkind = 'r'              -- tablas reales
          AND a.attnum > 0
          AND NOT a.attisdropped
          AND pg_get_serial_sequence(format('%I.%I', n.nspname, c.relname), a.attname) IS NOT NULL
    LOOP
        seq_name := pg_get_serial_sequence(format('%I.%I', r.schema_name, r.table_name), r.column_name);

        -- MAX(col) de la tabla (si está vacía, usa 0)
        EXECUTE format('SELECT COALESCE(MAX(%I), 0) FROM %I.%I', r.column_name, r.schema_name, r.table_name)
        INTO max_id;

        -- Ajusta la secuencia a MAX+1
        EXECUTE format('SELECT setval(%L, %s, false)', seq_name, (max_id + 1));
    END LOOP;
END $$;

COMMIT;

-- =============================================================================
-- FIN DEL SCRIPT 04 - POST (MANTENIMIENTO)
-- =============================================================================
