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
--
-- Nota (decisión actual):
--   No se agregan más índices por ahora. Solo se conservan los índices ya
--   definidos abajo (los que ya venías usando/planificando).
--
-- Nota sobre departamentos:
--   Actualmente NO existe una restricción UNIQUE para (id_pais, nombre).
--   Eso significa que se podría insertar dos veces un mismo departamento
--   (ej: "ASUNCIÓN") dentro del mismo país si alguien lo hace manualmente.
--   No se aplica constraint adicional por decisión actual.
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

-- CLIENTES / VEHICULOS / COMPONENTES: acelera listados y joins por cliente
CREATE INDEX IF NOT EXISTS idx_vehiculos_id_cliente
ON public.vehiculos (id_cliente);

CREATE INDEX IF NOT EXISTS idx_componentes_id_cliente
ON public.componentes (id_cliente);

-- ORDENES y DETALLES: acelera listados, joins y recalculo de totales
CREATE INDEX IF NOT EXISTS idx_ordenes_trabajo_id_usuario
ON public.ordenes_trabajo (id_usuario);

-- Acelera joins y consultas de detalles por orden de trabajo
CREATE INDEX IF NOT EXISTS idx_ot_detalles_id_ot
ON public.orden_trabajo_detalles (id_orden_trabajo);

-- Optimiza joins y consultas de detalles por servicio aplicado
CREATE INDEX IF NOT EXISTS idx_ot_detalles_id_servicio
ON public.orden_trabajo_detalles (id_servicio);

-- PLANES y EVENTOS: acelera consultas y joins desde OT y plan
CREATE INDEX IF NOT EXISTS idx_planes_pago_id_ot
ON public.planes_pago (id_orden_trabajo);

CREATE INDEX IF NOT EXISTS idx_planes_pago_eventos_id_plan
ON public.planes_pago_eventos (id_plan_pago);

-- PAGOS: índice por usuario para listados/reportes
CREATE INDEX IF NOT EXISTS idx_pagos_id_usuario
ON public.pagos (id_usuario);

-- Un solo documento principal activo por cliente
CREATE UNIQUE INDEX IF NOT EXISTS ux_cliente_documentos_principal_por_cliente
ON public.cliente_documentos (id_cliente)
WHERE principal = true AND activo = true;

-- Evita duplicar documentos activos por tipo + número dentro del mismo cliente
CREATE UNIQUE INDEX IF NOT EXISTS ux_cliente_documentos_cliente_tipo_numero_activo
ON public.cliente_documentos (id_cliente, id_tipo_documento, numero)
WHERE activo = true;

-- Optimiza joins y filtros por cliente en documentos,
-- especialmente en consultas de perfil del cliente y validaciones de documentos
CREATE INDEX IF NOT EXISTS idx_cliente_documentos_id_cliente
ON public.cliente_documentos (id_cliente);

-- Acelera búsquedas y joins por tipo de documento,
-- útil para listados, validaciones y controles administrativos
CREATE INDEX IF NOT EXISTS idx_cliente_documentos_id_tipo_documento
ON public.cliente_documentos (id_tipo_documento);

-- USUARIOS: acelera listados y joins por rol asignado
CREATE INDEX IF NOT EXISTS idx_usuarios_id_rol
ON public.usuarios (id_rol);

-- ROLES / PERMISOS:
-- Nota: NO se crea idx_roles_permisos_id_rol porque la PK (id_rol, id_permiso)
-- ya cubre búsquedas por id_rol como primer componente (índice implícito).
-- =============================================================================
-- CHECK / UNIQUE CONSTRAINTS (IDEMPOTENTES)
-- =============================================================================

-- Garantiza que una orden de trabajo esté asociada exclusivamente
-- a un vehículo o a un componente, según el tipo de ingreso definido
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_ot_sujeto_por_tipo'
          AND conrelid = 'public.ordenes_trabajo'::regclass
    ) THEN
        ALTER TABLE public.ordenes_trabajo
        ADD CONSTRAINT check_ot_sujeto_por_tipo
        CHECK (
            (tipo_ingreso = 'VEHICULO'   AND id_vehiculo   IS NOT NULL AND id_componente IS NULL) OR
            (tipo_ingreso = 'COMPONENTE' AND id_componente IS NOT NULL AND id_vehiculo   IS NULL)
        );
    END IF;
END $$;

-- Impide registrar servicios con precios base negativos
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_precio_base_no_negativo'
          AND conrelid = 'public.servicios'::regclass
    ) THEN
        ALTER TABLE public.servicios
        ADD CONSTRAINT check_precio_base_no_negativo
        CHECK (precio_base >= 0);
    END IF;
END $$;

-- Evita precios unitarios negativos en los detalles de orden
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_precio_unitario_no_negativo'
          AND conrelid = 'public.orden_trabajo_detalles'::regclass
    ) THEN
        ALTER TABLE public.orden_trabajo_detalles
        ADD CONSTRAINT check_precio_unitario_no_negativo
        CHECK (precio_unitario IS NULL OR precio_unitario >= 0);
    END IF;
END $$;

-- Asegura que la cantidad de trabajos o servicios sea siempre mayor a cero
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_cantidad_minima'
          AND conrelid = 'public.orden_trabajo_detalles'::regclass
    ) THEN
        ALTER TABLE public.orden_trabajo_detalles
        ADD CONSTRAINT check_cantidad_minima
        CHECK (cantidad > 0);
    END IF;
END $$;

-- Impide valores negativos en la garantía expresada en meses
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_garantia_meses_no_negativo'
          AND conrelid = 'public.orden_trabajo_detalles'::regclass
    ) THEN
        ALTER TABLE public.orden_trabajo_detalles
        ADD CONSTRAINT check_garantia_meses_no_negativo
        CHECK (garantia_meses IS NULL OR garantia_meses >= 0);
    END IF;
END $$;

-- Impide valores negativos en la garantía expresada en días
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_garantia_dias_no_negativo'
          AND conrelid = 'public.orden_trabajo_detalles'::regclass
    ) THEN
        ALTER TABLE public.orden_trabajo_detalles
        ADD CONSTRAINT check_garantia_dias_no_negativo
        CHECK (garantia_dias IS NULL OR garantia_dias >= 0);
    END IF;
END $$;

-- Evita duplicar componentes para un mismo cliente, tipo, marca y modelo
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'componentes_unique_cliente_tipo_marca_modelo'
          AND conrelid = 'public.componentes'::regclass
    ) THEN
        ALTER TABLE public.componentes
        ADD CONSTRAINT componentes_unique_cliente_tipo_marca_modelo
        UNIQUE (id_cliente, tipo_componente, id_marca, id_modelo);
    END IF;
END $$;

-- Asegura que la cantidad declarada de componentes sea siempre positiva
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_componentes_cantidad_positiva'
          AND conrelid = 'public.componentes'::regclass
    ) THEN
        ALTER TABLE public.componentes
        ADD CONSTRAINT check_componentes_cantidad_positiva
        CHECK (cantidad > 0);
    END IF;
END $$;

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

-- Asegura coherencia mínima del plan de pago según el tipo:
--   • CONTADO debe tener exactamente 1 cuota
--   • CUOTAS debe tener 1 o más cuotas
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'check_planes_pago_tipo_vs_cuotas'
          AND conrelid = 'public.planes_pago'::regclass
    ) THEN
        ALTER TABLE public.planes_pago
        ADD CONSTRAINT check_planes_pago_tipo_vs_cuotas
        CHECK (
            (tipo_plan = 'CONTADO' AND cantidad_cuotas = 1)
         OR (tipo_plan = 'CUOTAS'  AND cantidad_cuotas >= 1)
        );
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
    -- Usar valor ENUM RECIBIDO por defecto
    IF NEW.estado IS NULL THEN
        NEW.estado := 'RECIBIDO';
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

-- 2) Completa automáticamente el precio unitario del detalle
-- Nota (decisión actual):
--   Si el precio_unitario viene NULL o 0, se autocompleta con el precio_base.
--   Esto fuerza que "0" no sea considerado un valor manual válido.
CREATE OR REPLACE FUNCTION public.fn_ot_detalle_precio_auto()
RETURNS trigger AS $$
BEGIN
    IF NEW.precio_unitario IS NULL OR NEW.precio_unitario = 0 THEN
        SELECT s.precio_base
        INTO NEW.precio_unitario
        FROM public.servicios s
        WHERE s.id_servicio = NEW.id_servicio;
    END IF;

    NEW.precio_unitario := COALESCE(NEW.precio_unitario, 0);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3) Recalcula el total estimado de la orden
CREATE OR REPLACE FUNCTION public.fn_ot_actualizar_total_cabecera()
RETURNS trigger AS $$
DECLARE
    v_id_ot bigint;
BEGIN
    v_id_ot := COALESCE(NEW.id_orden_trabajo, OLD.id_orden_trabajo);

    UPDATE public.ordenes_trabajo
    SET total_estimado = (
        SELECT COALESCE(SUM(subtotal), 0)
        FROM public.orden_trabajo_detalles
        WHERE id_orden_trabajo = v_id_ot
    )
    WHERE id_orden_trabajo = v_id_ot;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 4) Controla la fecha de finalización según el estado de la orden (usando ENUM FINALIZADO)
CREATE OR REPLACE FUNCTION public.fn_ordenes_trabajo_fechas_estado()
RETURNS trigger AS $$
BEGIN
    IF NEW.estado = 'FINALIZADO' AND (OLD.estado IS NULL OR OLD.estado <> 'FINALIZADO') THEN
        NEW.fecha_fin := now();
    END IF;

    IF NEW.estado <> 'FINALIZADO' AND OLD.estado = 'FINALIZADO' THEN
        NEW.fecha_fin := NULL;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 5) Valida coherencia entre marca y modelo del vehículo
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

-- 6) Valida coherencia entre marca y modelo del componente
CREATE OR REPLACE FUNCTION public.fn_componentes_validar_modelo_marca()
RETURNS trigger AS $$
DECLARE
    v_marca_modelo bigint;
BEGIN
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

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 7) Impide que un cliente sea persona y empresa simultáneamente
CREATE OR REPLACE FUNCTION public.fn_clientes_evitar_doble_tipo()
RETURNS trigger AS $$
BEGIN
    IF TG_TABLE_NAME = 'clientes_persona' THEN
        IF EXISTS (SELECT 1 FROM public.clientes_empresa WHERE id_cliente = NEW.id_cliente) THEN
            RAISE EXCEPTION 'El cliente % ya está registrado como EMPRESA', NEW.id_cliente;
        END IF;
    END IF;

    IF TG_TABLE_NAME = 'clientes_empresa' THEN
        IF EXISTS (SELECT 1 FROM public.clientes_persona WHERE id_cliente = NEW.id_cliente) THEN
            RAISE EXCEPTION 'El cliente % ya está registrado como PERSONA', NEW.id_cliente;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 8) Calcula automáticamente las fechas de garantía al finalizar la orden
CREATE OR REPLACE FUNCTION public.fn_ot_detalles_calcular_garantia_al_terminar()
RETURNS trigger AS $$
DECLARE
    v_desde date;
BEGIN
    IF NEW.estado = 'FINALIZADO' AND (OLD.estado IS NULL OR OLD.estado <> 'FINALIZADO') THEN
        IF NEW.fecha_fin IS NULL THEN
            NEW.fecha_fin := now();
        END IF;

        v_desde := (NEW.fecha_fin)::date;

        UPDATE public.orden_trabajo_detalles
        SET garantia_desde = v_desde,
            garantia_hasta = (
                v_desde
                + COALESCE((garantia_meses || ' months')::interval, interval '0 months')
                + COALESCE((garantia_dias  || ' days')::interval,  interval '0 days')
            )::date
        WHERE id_orden_trabajo = NEW.id_orden_trabajo
          AND (
                (garantia_meses IS NOT NULL AND garantia_meses > 0)
             OR (garantia_dias  IS NOT NULL AND garantia_dias  > 0)
          );

        UPDATE public.orden_trabajo_detalles
        SET garantia_desde = NULL,
            garantia_hasta = NULL
        WHERE id_orden_trabajo = NEW.id_orden_trabajo
          AND NOT (
                (garantia_meses IS NOT NULL AND garantia_meses > 0)
             OR (garantia_dias  IS NOT NULL AND garantia_dias  > 0)
          );
    END IF;

    IF NEW.estado <> 'FINALIZADO' AND OLD.estado = 'FINALIZADO' THEN
        UPDATE public.orden_trabajo_detalles
        SET garantia_desde = NULL,
            garantia_hasta = NULL
        WHERE id_orden_trabajo = NEW.id_orden_trabajo;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 9) Impide borrar clientes que tengan OT abiertas
CREATE OR REPLACE FUNCTION public.fn_clientes_no_borrar_ot()
RETURNS trigger AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM public.ordenes_trabajo
        WHERE id_cliente = OLD.id_cliente
          AND estado <> 'FINALIZADO'
    ) THEN
        RAISE EXCEPTION 'No se puede borrar el cliente %, tiene órdenes de trabajo abiertas', OLD.id_cliente;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- 10) Completa automáticamente el distrito a partir de la localidad (si no fue provisto)
--     y valida coherencia entre distrito y localidad cuando ambos existen.
CREATE OR REPLACE FUNCTION public.fn_clientes_set_distrito_por_localidad()
RETURNS trigger AS $$
DECLARE
    v_distrito_de_localidad bigint;
BEGIN
    -- Regla: al menos uno debe estar definido
    IF NEW.id_distrito IS NULL AND NEW.id_localidad IS NULL THEN
        RAISE EXCEPTION 'Debe especificar al menos distrito o localidad';
    END IF;

    -- Si hay localidad, obtener el distrito real de esa localidad
    IF NEW.id_localidad IS NOT NULL THEN
        SELECT l.id_distrito
        INTO v_distrito_de_localidad
        FROM public.localidades l
        WHERE l.id_localidad = NEW.id_localidad;

        IF v_distrito_de_localidad IS NULL THEN
            RAISE EXCEPTION 'Localidad inválida: %', NEW.id_localidad;
        END IF;

        -- Si no viene distrito, autocompletar
        IF NEW.id_distrito IS NULL THEN
            NEW.id_distrito := v_distrito_de_localidad;
        ELSE
            -- Si vienen ambos, validar coherencia
            IF NEW.id_distrito <> v_distrito_de_localidad THEN
                RAISE EXCEPTION
                    'La localidad % no pertenece al distrito %', NEW.id_localidad, NEW.id_distrito;
            END IF;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- TRIGGERS
-- =============================================================================

-- Asigna estado inicial y número correlativo al crear una orden de trabajo
DROP TRIGGER IF EXISTS trg_ordenes_trabajo_inicio ON public.ordenes_trabajo;
CREATE TRIGGER trg_ordenes_trabajo_inicio
BEFORE INSERT ON public.ordenes_trabajo
FOR EACH ROW EXECUTE FUNCTION public.fn_ordenes_trabajo_inicio();

-- Completa el precio unitario del detalle antes de guardar el registro
DROP TRIGGER IF EXISTS trg_ot_detalle_precio_auto ON public.orden_trabajo_detalles;
CREATE TRIGGER trg_ot_detalle_precio_auto
BEFORE INSERT OR UPDATE ON public.orden_trabajo_detalles
FOR EACH ROW EXECUTE FUNCTION public.fn_ot_detalle_precio_auto();

-- Recalcula el total estimado de la orden ante cualquier cambio en los detalles
DROP TRIGGER IF EXISTS trg_ot_detalle_actualizar_total ON public.orden_trabajo_detalles;
CREATE TRIGGER trg_ot_detalle_actualizar_total
AFTER INSERT OR UPDATE OR DELETE ON public.orden_trabajo_detalles
FOR EACH ROW EXECUTE FUNCTION public.fn_ot_actualizar_total_cabecera();

-- Actualiza o limpia la fecha de finalización según el estado de la orden
DROP TRIGGER IF EXISTS trg_ordenes_trabajo_fechas ON public.ordenes_trabajo;
CREATE TRIGGER trg_ordenes_trabajo_fechas
BEFORE UPDATE OF estado ON public.ordenes_trabajo
FOR EACH ROW EXECUTE FUNCTION public.fn_ordenes_trabajo_fechas_estado();

-- Valida que el modelo seleccionado pertenezca a la marca del vehículo
DROP TRIGGER IF EXISTS trg_vehiculos_validar_modelo_marca ON public.vehiculos;
CREATE TRIGGER trg_vehiculos_validar_modelo_marca
BEFORE INSERT OR UPDATE OF id_marca, id_modelo ON public.vehiculos
FOR EACH ROW EXECUTE FUNCTION public.fn_vehiculos_validar_modelo_marca();

-- Valida que el modelo seleccionado pertenezca a la marca del componente
DROP TRIGGER IF EXISTS trg_componentes_validar_modelo_marca ON public.componentes;
CREATE TRIGGER trg_componentes_validar_modelo_marca
BEFORE INSERT OR UPDATE OF id_marca, id_modelo
ON public.componentes
FOR EACH ROW EXECUTE FUNCTION public.fn_componentes_validar_modelo_marca();

-- Evita registrar un cliente persona si ya existe como empresa
DROP TRIGGER IF EXISTS trg_clientes_persona_no_empresa ON public.clientes_persona;
CREATE TRIGGER trg_clientes_persona_no_empresa
BEFORE INSERT OR UPDATE OF id_cliente ON public.clientes_persona
FOR EACH ROW EXECUTE FUNCTION public.fn_clientes_evitar_doble_tipo();

-- Evita registrar un cliente empresa si ya existe como persona
DROP TRIGGER IF EXISTS trg_clientes_empresa_no_persona ON public.clientes_empresa;
CREATE TRIGGER trg_clientes_empresa_no_persona
BEFORE INSERT OR UPDATE OF id_cliente ON public.clientes_empresa
FOR EACH ROW EXECUTE FUNCTION public.fn_clientes_evitar_doble_tipo();

-- Calcula automáticamente la garantía de los trabajos al finalizar la orden
DROP TRIGGER IF EXISTS trg_ot_calcular_garantia ON public.ordenes_trabajo;
CREATE TRIGGER trg_ot_calcular_garantia
BEFORE UPDATE OF estado ON public.ordenes_trabajo
FOR EACH ROW EXECUTE FUNCTION public.fn_ot_detalles_calcular_garantia_al_terminar();

-- Trigger preventivo de borrado de cliente
DROP TRIGGER IF EXISTS trg_clientes_no_borrar_ot ON public.clientes;
CREATE TRIGGER trg_clientes_no_borrar_ot
BEFORE DELETE ON public.clientes
FOR EACH ROW EXECUTE FUNCTION public.fn_clientes_no_borrar_ot();

-- Autocompleta distrito cuando se selecciona localidad en el cliente
-- y valida consistencia entre distrito y localidad cuando ambos existen
DROP TRIGGER IF EXISTS trg_clientes_set_distrito_por_localidad ON public.clientes;
CREATE TRIGGER trg_clientes_set_distrito_por_localidad
BEFORE INSERT OR UPDATE OF id_localidad, id_distrito ON public.clientes
FOR EACH ROW EXECUTE FUNCTION public.fn_clientes_set_distrito_por_localidad();

COMMIT;

-- =============================================================================
-- FIN DEL SCRIPT 03 - POST
-- =============================================================================
