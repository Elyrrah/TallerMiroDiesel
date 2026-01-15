-- =============================================================================
-- 00_Type.sql
-- Tipos ENUM para la base de datos del Taller Miro Diesel
-- (Nombres alineados con 01_SCHEMA.sql)
-- =============================================================================

-- =============================================================================
-- ORDENES DE TRABAJO
-- =============================================================================

-- Tipo de ingreso de la orden
CREATE TYPE public.tipo_ingreso_orden_enum AS ENUM (
    'VEHICULO',
    'COMPONENTE'
);

-- Estado de la orden de trabajo
CREATE TYPE public.estado_orden_trabajo_enum AS ENUM (
    'RECIBIDO',
    'EN_PROCESO',
    'FINALIZADO',
    'CANCELADO'
);

-- =============================================================================
-- PLANES DE PAGO
-- =============================================================================

-- Tipo de plan de pago
CREATE TYPE public.tipo_plan_pago_enum AS ENUM (
    'CONTADO',
    'CUOTAS'
);

-- Estado del plan de pago
CREATE TYPE public.estado_plan_pago_enum AS ENUM (
    'ACTIVO',
    'VENCIDO',
    'CANCELADO'
);

-- Tipo de evento del plan de pago
CREATE TYPE public.tipo_evento_plan_pago_enum AS ENUM (
    'PAGO_REALIZADO',
    'PAGO_PENDIENTE',
    'PAGO_VENCIDO',
    'OTRO'
);

-- =============================================================================
-- PAGOS
-- =============================================================================

-- Forma de pago
CREATE TYPE public.forma_pago_enum AS ENUM (
    'EFECTIVO',
    'TRANSFERENCIA',
    'TARJETA',
    'CHEQUE_AL_DIA',
    'CHEQUE_A_PLAZO',
    'OTRO'
);

-- =============================================================================
-- FIN DEL SCRIPT 00_TYPE
-- =============================================================================