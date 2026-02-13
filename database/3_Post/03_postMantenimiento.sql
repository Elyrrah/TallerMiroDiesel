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
            a.attname  AS column_name,
            a.attidentity AS attidentity
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_attribute a ON a.attrelid = c.oid
        WHERE n.nspname = 'public'
          AND c.relkind = 'r'              -- tablas reales
          AND a.attnum > 0
          AND NOT a.attisdropped
          AND (
                -- IDENTITY
                a.attidentity IN ('a','d')
                OR
                -- SERIAL / DEFAULT nextval(...)
                pg_get_serial_sequence(format('%I.%I', n.nspname, c.relname), a.attname) IS NOT NULL
          )
    LOOP
        -- 1) Intenta obtener la secuencia vía pg_get_serial_sequence (SERIAL / algunos IDENTITY)
        seq_name := pg_get_serial_sequence(format('%I.%I', r.schema_name, r.table_name), r.column_name);

        -- 2) Si no la encuentra, resuelve la secuencia de IDENTITY por catálogo (pg_depend)
        IF seq_name IS NULL THEN
            SELECT quote_ident(ns.nspname) || '.' || quote_ident(s.relname)
            INTO seq_name
            FROM pg_depend d
            JOIN pg_class s ON s.oid = d.objid
            JOIN pg_namespace ns ON ns.oid = s.relnamespace
            WHERE d.refobjid = format('%I.%I', r.schema_name, r.table_name)::regclass
              AND d.refobjsubid = (
                    SELECT attnum
                    FROM pg_attribute
                    WHERE attrelid = format('%I.%I', r.schema_name, r.table_name)::regclass
                      AND attname = r.column_name
              )
              AND s.relkind = 'S'
            LIMIT 1;
        END IF;

        -- Si aún no hay secuencia asociada, continua
        IF seq_name IS NULL THEN
            CONTINUE;
        END IF;

        -- MAX(col) de la tabla (si está vacía, usa 0)
        EXECUTE format(
            'SELECT COALESCE(MAX(%I), 0) FROM %I.%I',
            r.column_name, r.schema_name, r.table_name
        )
        INTO max_id;

        -- Ajusta la secuencia a MAX+1
        EXECUTE format('SELECT setval(%L, %s, false)', seq_name, (max_id + 1));
    END LOOP;
END $$;

COMMIT;

-- =============================================================================
-- FIN DEL SCRIPT 04 - POST (MANTENIMIENTO)
-- =============================================================================
