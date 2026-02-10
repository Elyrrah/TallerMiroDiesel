-- =============================================================================
-- 00_Type.sql
-- Tipos ENUM para la base de datos del Taller Miro Diesel
-- =============================================================================

-- =============================================================================
-- CLIENTES: FUENTE DE REFERENCIA DEL CLIENTE
-- =============================================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'fuente_referencia_cliente_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.fuente_referencia_cliente_enum AS ENUM (
            'NINGUNA',
            'RECOMENDACION',
            'MECANICO'
        );
    END IF;
END $$;


-- =============================================================================
-- TIPO DE APLICACION DEL DOCUMENTO
-- =============================================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'tipo_documento_aplica_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.tipo_documento_aplica_enum AS ENUM (
            'PERSONA',
            'EMPRESA',
            'AMBOS'
        );
    END IF;
END $$;

-- =============================================================================
-- COMPONENTE TIPO
-- =============================================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'tipo_componente_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.tipo_componente_enum AS ENUM (
            'PICO_INYECTOR',
            'BOMBA_INYECTORA',
            'AMBOS'
        );
    END IF;
END $$;

-- =============================================================================
-- ORDENES DE TRABAJO
-- =============================================================================

-- Tipo de ingreso de la orden
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'tipo_ingreso_orden_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.tipo_ingreso_orden_enum AS ENUM (
            'VEHICULO',
            'COMPONENTE'
        );
    END IF;
END $$;

-- Estado de la orden de trabajo
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'estado_orden_trabajo_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.estado_orden_trabajo_enum AS ENUM (
            'RECIBIDO',
            'EN_PROCESO',
            'FINALIZADO',
            'CANCELADO'
        );
    END IF;
END $$;

-- =============================================================================
-- PLANES DE PAGO
-- =============================================================================

-- Tipo de plan de pago
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'tipo_plan_pago_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.tipo_plan_pago_enum AS ENUM (
            'CONTADO',
            'CUOTAS'
        );
    END IF;
END $$;

-- Estado del plan de pago
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'estado_plan_pago_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.estado_plan_pago_enum AS ENUM (
            'ACTIVO',
            'VENCIDO',
            'CANCELADO'
        );
    END IF;
END $$;

-- Tipo de evento del plan de pago
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'tipo_evento_plan_pago_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.tipo_evento_plan_pago_enum AS ENUM (
            'PAGO_REALIZADO',
            'PAGO_PENDIENTE',
            'PAGO_VENCIDO',
            'OTRO'
        );
    END IF;
END $$;

-- =============================================================================
-- PAGOS
-- =============================================================================

-- Forma de pago
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'forma_pago_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.forma_pago_enum AS ENUM (
            'EFECTIVO',
            'TRANSFERENCIA',
            'TARJETA',
            'CHEQUE_AL_DIA',
            'CHEQUE_A_PLAZO',
            'OTRO'
        );
    END IF;
END $$;

-- =============================================================================
-- FIN DEL SCRIPT 00_TYPE
-- =============================================================================
