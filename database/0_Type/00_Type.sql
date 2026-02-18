-- =============================================================================
-- 00_Type.sql
-- Tipos ENUM para la base de datos del Taller Miro Diesel
-- =============================================================================

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
            'ABIERTA',
            'EN_PROCESO',
            'EN_ESPERA',
            'FINALIZADA',
            'ENTREGADA',
            'CANCELADA'
        );
    END IF;
END $$;

-- Estado de pago de la orden de trabajo
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'estado_pago_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.estado_pago_enum AS ENUM (
            'PENDIENTE',
            'PARCIAL',
            'PAGADO'
        );
    END IF;
END $$;

-- =============================================================================
-- PLANES DE PAGO
-- =============================================================================
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
            'PAUSADO',
            'FINALIZADO',
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
            'CREACION',
            'COMPROMISO_PAGO',
            'CAMBIO_ESTADO',
            'MODIFICACION',
            'CANCELACION'
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
            'CHEQUE',
            'TARJETA_CREDITO',
            'TARJETA_DEBITO',
            'OTRO'
        );
    END IF;
END $$;

-- =============================================================================
-- CLIENTES
-- =============================================================================
-- Fuente de referencia del cliente en la orden de trabajo
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'fuente_referencia_enum'
          AND n.nspname = 'public'
    ) THEN
        CREATE TYPE public.fuente_referencia_enum AS ENUM (
            'NINGUNA',
            'CLIENTE',
            'MECANICO'
        );
    END IF;
END $$;

-- =============================================================================
-- FIN DEL SCRIPT 00_TYPE
-- =============================================================================