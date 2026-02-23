-- =============================================================================
-- 03 - POST
-- ÍNDICES · CONSTRAINTS · FUNCIONES · TRIGGERS
--
-- Este script define toda la lógica posterior a la creación de tablas:
--  • Índices para mejorar el rendimiento de consultas frecuentes
--  • Constraints de negocio que no pertenecen al esquema base
--  • Funciones que encapsulan reglas y automatismos
--  • Triggers que activan dichas funciones de forma transparente
--
-- El script es completamente idempotente y seguro de ejecutar múltiples veces.
-- =============================================================================

BEGIN;

-- =============================================================================
-- ÍNDICES
-- =============================================================================

-- Optimiza búsquedas por nombre y apellido en clientes persona
CREATE INDEX IF NOT EXISTS idx_clientes_persona_busqueda
ON public.clientes_persona (nombre, apellido);

-- Optimiza búsquedas por apodo o alias del cliente persona
CREATE INDEX IF NOT EXISTS idx_clientes_persona_apodo
ON public.clientes_persona (apodo);

-- Optimiza búsquedas por nombre de fantasía en clientes empresa
CREATE INDEX IF NOT EXISTS idx_clientes_empresa_nombre_fantasia
ON public.clientes_empresa (nombre_fantasia);

-- Optimiza búsquedas por razón social en clientes empresa
CREATE INDEX IF NOT EXISTS idx_clientes_empresa_razon_social
ON public.clientes_empresa (razon_social);

-- Acelera el filtrado y listado de órdenes de trabajo por estado
CREATE INDEX IF NOT EXISTS idx_ordenes_trabajo_estado
ON public.ordenes_trabajo (estado);

-- Optimiza consultas que buscan OT de un cliente específico y por estado
CREATE INDEX IF NOT EXISTS idx_ordenes_trabajo_cliente_estado
ON public.ordenes_trabajo (id_cliente, estado);

-- Mejora el rendimiento de búsquedas de OT por vehículo
CREATE INDEX IF NOT EXISTS idx_ordenes_trabajo_vehiculo
ON public.ordenes_trabajo (id_vehiculo);

-- Optimiza consultas de pagos filtrando por plan de pago y orden de trabajo
CREATE INDEX IF NOT EXISTS idx_pagos_plan_ot
ON public.pagos (id_plan_pago, id_orden_trabajo);

-- Vehiculos se vinculan a cliente a través de ordenes_trabajo
CREATE INDEX IF NOT EXISTS idx_ordenes_trabajo_fecha_ingreso
ON public.ordenes_trabajo (fecha_ingreso);

-- Componentes se vinculan a cliente a través de ordenes_trabajo
CREATE INDEX IF NOT EXISTS idx_ordenes_trabajo_componente
ON public.ordenes_trabajo (id_componente);

-- ORDENES y DETALLES: acelera listados, joins y recalculo de totales
CREATE INDEX IF NOT EXISTS idx_ordenes_trabajo_id_usuario
ON public.ordenes_trabajo (id_usuario);

-- Acelera joins y consultas de detalles por orden de trabajo
CREATE INDEX IF NOT EXISTS idx_ot_detalles_id_ot
ON public.orden_trabajo_detalles (id_orden_trabajo);

-- Acelera recálculo de totales usando (id_orden_trabajo, activo)
CREATE INDEX IF NOT EXISTS idx_ot_detalles_id_ot_activo
ON public.orden_trabajo_detalles (id_orden_trabajo, activo);

-- Optimiza joins y consultas de detalles por servicio aplicado
CREATE INDEX IF NOT EXISTS idx_ot_detalles_id_servicio
ON public.orden_trabajo_detalles (id_servicio);

-- PLANES: acelera consultas y joins desde OT y plan
CREATE INDEX IF NOT EXISTS idx_planes_pago_id_ot
ON public.planes_pago (id_orden_trabajo);

-- EVENTOS: acelera consultas y joins desde OT y plan
CREATE INDEX IF NOT EXISTS idx_planes_pago_eventos_id_plan
ON public.planes_pago_eventos (id_plan_pago);

-- PAGOS: índice por usuario para listados/reportes
CREATE INDEX IF NOT EXISTS idx_pagos_id_usuario
ON public.pagos (id_usuario);

-- Mejora reportes y listados de pagos por OT ordenados por fecha
CREATE INDEX IF NOT EXISTS idx_pagos_id_ot_fecha
ON public.pagos (id_orden_trabajo, fecha_pago);

-- Evita duplicar documentos activos por tipo + número dentro del mismo cliente
CREATE UNIQUE INDEX IF NOT EXISTS ux_cliente_documentos_cliente_tipo_numero_activo
ON public.cliente_documentos (id_cliente, id_tipo_documento, numero)
WHERE activo = true;

-- Optimiza joins y filtros por cliente en documentos
CREATE INDEX IF NOT EXISTS idx_cliente_documentos_id_cliente
ON public.cliente_documentos (id_cliente);

-- Acelera búsquedas y joins por tipo de documento
CREATE INDEX IF NOT EXISTS idx_cliente_documentos_id_tipo_documento
ON public.cliente_documentos (id_tipo_documento);

-- USUARIOS: acelera listados y joins por rol asignado
CREATE INDEX IF NOT EXISTS idx_usuarios_id_rol
ON public.usuarios (id_rol);

-- =============================================================================
-- CHECK / UNIQUE CONSTRAINTS (IDEMPOTENTES)
-- =============================================================================

-- Impide registrar pagos con monto cero o negativo
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_pagos_monto_positivo'
          AND conrelid = 'public.pagos'::regclass
    ) THEN
        ALTER TABLE public.pagos
        ADD CONSTRAINT check_pagos_monto_positivo
        CHECK (monto > 0);
    END IF;
END $$;

-- Impide registrar planes de pago con monto de cuota negativo
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_planes_pago_monto_cuota_no_negativo'
          AND conrelid = 'public.planes_pago'::regclass
    ) THEN
        ALTER TABLE public.planes_pago
        ADD CONSTRAINT check_planes_pago_monto_cuota_no_negativo
        CHECK (monto_cuota >= 0);
    END IF;
END $$;

-- Asegura cantidad_cuotas mínima 1
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_planes_pago_cantidad_cuotas_min_1'
          AND conrelid = 'public.planes_pago'::regclass
    ) THEN
        ALTER TABLE public.planes_pago
        ADD CONSTRAINT check_planes_pago_cantidad_cuotas_min_1
        CHECK (cantidad_cuotas >= 1);
    END IF;
END $$;

-- =============================================================================
-- FUNCIONES
-- =============================================================================

-- 1) Inicializa órdenes de trabajo asignando estado y número correlativo mensual
CREATE OR REPLACE FUNCTION public.fn_ordenes_trabajo_inicio()
RETURNS trigger AS $$
DECLARE
    v_anio text;
    v_mes text;
    v_prefix text;
    v_ultimo integer;
BEGIN
    IF NEW.estado IS NULL THEN
        NEW.estado := 'ABIERTA';
    END IF;

    IF NEW.numero_orden IS NULL OR NEW.numero_orden = '' THEN
        v_anio := to_char(now(), 'YYYY');
        v_mes  := to_char(now(), 'MM');
        v_prefix := 'OT-' || v_anio || '-' || v_mes || '-';

        PERFORM pg_advisory_xact_lock(hashtext(v_prefix));

        SELECT COALESCE(
            MAX(substring(numero_orden from '([0-9]{3})$')::int),
            0
        )
        INTO v_ultimo
        FROM public.ordenes_trabajo
        WHERE numero_orden LIKE v_prefix || '%';

        NEW.numero_orden := v_prefix || lpad((v_ultimo + 1)::text, 3, '0');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 2) Completa automáticamente el precio unitario del detalle + Calcula subtotal
CREATE OR REPLACE FUNCTION public.fn_ot_detalle_precio_y_subtotal_auto()
RETURNS trigger AS $$
BEGIN
    IF NEW.precio_unitario IS NULL OR NEW.precio_unitario = 0 THEN
        SELECT s.precio_base
        INTO NEW.precio_unitario
        FROM public.servicios s
        WHERE s.id_servicio = NEW.id_servicio;
    END IF;

    NEW.cantidad := COALESCE(NEW.cantidad, 1);
    NEW.precio_unitario := COALESCE(NEW.precio_unitario, 0);
    NEW.subtotal := (NEW.cantidad * NEW.precio_unitario);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3) Controla la fecha de finalización según el estado de la orden
CREATE OR REPLACE FUNCTION public.fn_ordenes_trabajo_fechas_estado()
RETURNS trigger AS $$
BEGIN
    IF NEW.estado = 'FINALIZADA' AND (OLD.estado IS NULL OR OLD.estado <> 'FINALIZADA') THEN
        NEW.fecha_fin := now();
    END IF;

    IF NEW.estado <> 'FINALIZADA' AND OLD.estado = 'FINALIZADA' THEN
        NEW.fecha_fin := NULL;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 4) Valida coherencia entre marca y modelo del vehículo
CREATE OR REPLACE FUNCTION public.fn_vehiculos_validar_modelo_marca()
RETURNS trigger AS $$
DECLARE
    v_marca_modelo bigint;
BEGIN
    IF NEW.id_modelo IS NOT NULL THEN
        SELECT id_marca
        INTO v_marca_modelo
        FROM public.modelos
        WHERE id_modelo = NEW.id_modelo;

        IF v_marca_modelo IS NULL THEN
            RAISE EXCEPTION 'Modelo inválido: %', NEW.id_modelo;
        END IF;

        IF v_marca_modelo <> NEW.id_marca THEN
            RAISE EXCEPTION
                'El modelo % no pertenece a la marca %', NEW.id_modelo, NEW.id_marca;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 5) Valida coherencia entre marca y modelo del componente
CREATE OR REPLACE FUNCTION public.fn_componentes_validar_modelo_marca()
RETURNS trigger AS $$
DECLARE
    v_marca_modelo bigint;
BEGIN
    IF NEW.id_modelo IS NOT NULL THEN
        SELECT id_marca
        INTO v_marca_modelo
        FROM public.modelos
        WHERE id_modelo = NEW.id_modelo;

        IF v_marca_modelo IS NULL THEN
            RAISE EXCEPTION 'Modelo inválido: %', NEW.id_modelo;
        END IF;

        IF v_marca_modelo <> NEW.id_marca THEN
            RAISE EXCEPTION
                'El modelo % no pertenece a la marca %', NEW.id_modelo, NEW.id_marca;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 6) Impide borrar clientes que tengan OT abiertas
CREATE OR REPLACE FUNCTION public.fn_clientes_no_borrar_ot()
RETURNS trigger AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM public.ordenes_trabajo
        WHERE id_cliente = OLD.id_cliente
          AND estado NOT IN ('FINALIZADA', 'ENTREGADA', 'CANCELADA')
    ) THEN
        RAISE EXCEPTION 'No se puede borrar el cliente %, tiene órdenes de trabajo abiertas', OLD.id_cliente;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- TRIGGERS
-- =============================================================================

-- Asigna estado inicial ABIERTA y genera número correlativo mensual al crear una OT
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger
        WHERE tgname = 'trg_ot_01_inicio'
          AND tgrelid = 'public.ordenes_trabajo'::regclass
    ) THEN
        CREATE TRIGGER trg_ot_01_inicio
        BEFORE INSERT ON public.ordenes_trabajo
        FOR EACH ROW EXECUTE FUNCTION public.fn_ordenes_trabajo_inicio();
    END IF;
END $$;

-- Completa el precio unitario desde el catálogo de servicios y calcula el subtotal del detalle
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger
        WHERE tgname = 'trg_otd_01_precio_y_subtotal_auto'
          AND tgrelid = 'public.orden_trabajo_detalles'::regclass
    ) THEN
        CREATE TRIGGER trg_otd_01_precio_y_subtotal_auto
        BEFORE INSERT OR UPDATE ON public.orden_trabajo_detalles
        FOR EACH ROW EXECUTE FUNCTION public.fn_ot_detalle_precio_y_subtotal_auto();
    END IF;
END $$;

-- Registra fecha_fin al pasar la OT a FINALIZADA y la limpia si se revierte ese estado
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger
        WHERE tgname = 'trg_ot_10_fechas_estado'
          AND tgrelid = 'public.ordenes_trabajo'::regclass
    ) THEN
        CREATE TRIGGER trg_ot_10_fechas_estado
        BEFORE UPDATE OF estado ON public.ordenes_trabajo
        FOR EACH ROW EXECUTE FUNCTION public.fn_ordenes_trabajo_fechas_estado();
    END IF;
END $$;

-- Valida que el modelo del vehículo pertenezca a la marca indicada
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger
        WHERE tgname = 'trg_veh_01_validar_modelo_marca'
          AND tgrelid = 'public.vehiculos'::regclass
    ) THEN
        CREATE TRIGGER trg_veh_01_validar_modelo_marca
        BEFORE INSERT OR UPDATE OF id_marca, id_modelo ON public.vehiculos
        FOR EACH ROW EXECUTE FUNCTION public.fn_vehiculos_validar_modelo_marca();
    END IF;
END $$;

-- Valida que el modelo del componente pertenezca a la marca indicada
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger
        WHERE tgname = 'trg_comp_01_validar_modelo_marca'
          AND tgrelid = 'public.componentes'::regclass
    ) THEN
        CREATE TRIGGER trg_comp_01_validar_modelo_marca
        BEFORE INSERT OR UPDATE OF id_marca, id_modelo ON public.componentes
        FOR EACH ROW EXECUTE FUNCTION public.fn_componentes_validar_modelo_marca();
    END IF;
END $$;

-- Bloquea el borrado de clientes que tengan órdenes de trabajo activas
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger
        WHERE tgname = 'trg_cli_01_no_borrar_ot'
          AND tgrelid = 'public.clientes'::regclass
    ) THEN
        CREATE TRIGGER trg_cli_01_no_borrar_ot
        BEFORE DELETE ON public.clientes
        FOR EACH ROW EXECUTE FUNCTION public.fn_clientes_no_borrar_ot();
    END IF;
END $$;

COMMIT;

-- =============================================================================
-- FIN DEL SCRIPT 03 - POST
-- =============================================================================