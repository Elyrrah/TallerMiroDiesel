-- ======================================================================================================================
-- 02_DATA.sql
-- INCLUYE: TIPOS_DOCUMENTO + MARCAS + MODELOS + TIPOS_COMPONENTE + ROLES + PERMISOS + ROLES_PERMISOS + SERVICIOS + USUARIO
-- ======================================================================================================================

BEGIN;

-- =============================================================================
-- MARCAS
-- =============================================================================
INSERT INTO public.marcas (nombre, activo)
VALUES
('MERCEDES-BENZ', true),
('SCANIA', true),
('NISSAN', true),
('TOYOTA', true),
('MAZDA', true),
('CATERPILLAR', true),
('JOHN DEERE', true),
('YANMAR', true),
('VALTRA', true),
('MASSEY FERGUSON', true),
('CBT', true),
('FIAT', true),
('CHEVROLET', true),
('MITSUBISHI', true)
ON CONFLICT (nombre) DO UPDATE
SET activo = EXCLUDED.activo;

-- =============================================================================
-- MODELOS
-- =============================================================================
INSERT INTO public.modelos (id_marca, nombre, activo)
SELECT m.id_marca, v.modelo, true
FROM (
    VALUES
        -- SCANIA
        ('SCANIA', '112'),
        ('SCANIA', '113'),

        -- TOYOTA
        ('TOYOTA', 'VITZ'),
        ('TOYOTA', 'HILUX'),
        ('TOYOTA', 'COROLLA'),
        ('TOYOTA', 'LAND CRUISER'),

        -- NISSAN
        ('NISSAN', 'PATROL'),
        ('NISSAN', 'NAVARA'),
        ('NISSAN', 'FRONTIER'),

        -- CHEVROLET
        ('CHEVROLET', 'ASTRA'),
        ('CHEVROLET', 'S10'),

        -- MITSUBISHI
        ('MITSUBISHI', 'MONTERO'),
        ('MITSUBISHI', 'L200'),
        ('MITSUBISHI', 'PAJERO'),

        -- FIAT
        ('FIAT', 'STRADA'),
        ('FIAT', 'TORO'),

        -- MAZDA
        ('MAZDA', 'BT-50')
) AS v(marca, modelo)
JOIN public.marcas m
  ON m.nombre = v.marca
ON CONFLICT (id_marca, nombre) DO UPDATE
SET activo = EXCLUDED.activo;

-- =============================================================================
-- TIPOS DE COMPONENTES
-- =============================================================================
INSERT INTO public.tipos_componente (nombre, descripcion, activo) VALUES
('PICO INYECTOR',     'Pico/Inyector individual', true),
('BOMBA INYECTORA',   'Bomba inyectora',          true),
('BOMBA Y PICO',      'Conjunto bomba + pico',    true)
ON CONFLICT (nombre) DO UPDATE
SET descripcion = EXCLUDED.descripcion,
    activo = EXCLUDED.activo;

-- =============================================================================
-- SERVICIOS
-- =============================================================================
INSERT INTO public.servicios (codigo, nombre, descripcion, precio_base, activo) VALUES
('ADAPT-BI', 'ADAPTACION DE BOMBA INYECTORA',      NULL, 5000000, true),
('CAMB-BI',  'CAMBIO DE BOMBA INYECTORA',          NULL, 3500000, true),
('CAMB-PI',  'CAMBIO DE PICO INYECTOR',            NULL,  180000, true),
('MANT-BI',  'MANTENIMIENTO DE BOMBA INYECTORA',   NULL,  800000, true),
('MANT-BP',  'MANTENIMIENTO DE BOMBA Y PICO',      NULL, 2000000, true),
('MANT-PI',  'MANTENIMIENTO DE PICO INYECTOR',     NULL,   50000, true),
('REPA-BI',  'REPARACION DE BOMBA INYECTORA',      NULL, 4000000, true),
('REPA-BP',  'REPARACION DE BOMBA Y PICO',         NULL, 4000000, true)
ON CONFLICT (codigo) DO UPDATE
SET nombre      = EXCLUDED.nombre,
    descripcion = EXCLUDED.descripcion,
    precio_base = EXCLUDED.precio_base,
    activo      = EXCLUDED.activo;

-- =============================================================================
-- TIPOS DE DOCUMENTO
-- =============================================================================
INSERT INTO public.tipos_documento (codigo, nombre, aplica_a, activo) VALUES
('CI',        'CEDULA DE IDENTIDAD',                       'PERSONA', true),
('RUC',       'REGISTRO UNICO DEL CONTRIBUYENTE',          'EMPRESA', true),
('DNI',       'DOCUMENTO NACIONAL DE IDENTIDAD',           'PERSONA', true),
('CPF',       'CADASTRO DE PESSOAS FISICAS',               'PERSONA', true),
('CNPJ',      'CADASTRO NACIONAL DA PESSOA JURIDICA',      'EMPRESA', true),
('CUIT',      'CLAVE UNICA DE IDENTIFICACION TRIBUTARIA',  'AMBOS',   true),
('NIT',       'NUMERO DE IDENTIFICACION TRIBUTARIA',       'AMBOS',   true),
('PASAPORTE', 'PASAPORTE',                                 'PERSONA', true)
ON CONFLICT (codigo) DO UPDATE
SET nombre   = EXCLUDED.nombre,
    aplica_a = EXCLUDED.aplica_a,
    activo   = EXCLUDED.activo;

-- =============================================================================
-- ROLES
-- =============================================================================
INSERT INTO public.roles (nombre, descripcion, activo) VALUES
('DESARROLLADOR', 'Acceso completo para desarrollo y pruebas', true),
('ADMINISTRADOR', 'Administración del sistema y catálogos',    true),
('OPERADOR',      'Operación diaria (carga y consultas)',      true)
ON CONFLICT (nombre) DO UPDATE
SET descripcion = EXCLUDED.descripcion,
    activo = EXCLUDED.activo;

-- =============================================================================
-- PERMISOS
-- =============================================================================
INSERT INTO public.permisos (nombre, descripcion, activo)
VALUES
-- GEOGRAFIA
('GEOGRAFIA.PAIS.LEER',            'País - Leer | Listar/Ver/Buscar países', true),
('GEOGRAFIA.PAIS.ESCRIBIR',        'País - Escribir | Crear/Editar países', true),
('GEOGRAFIA.PAIS.ESTADO',          'País - Estado | Activar/Desactivar países', true),

('GEOGRAFIA.DEPARTAMENTO.LEER',    'Departamento - Leer | Listar/Ver/Buscar departamentos', true),
('GEOGRAFIA.DEPARTAMENTO.ESCRIBIR','Departamento - Escribir | Crear/Editar departamentos', true),
('GEOGRAFIA.DEPARTAMENTO.ESTADO',  'Departamento - Estado | Activar/Desactivar departamentos', true),

('GEOGRAFIA.CIUDAD.LEER',          'Ciudad - Leer | Listar/Ver/Buscar ciudades', true),
('GEOGRAFIA.CIUDAD.ESCRIBIR',      'Ciudad - Escribir | Crear/Editar ciudades', true),
('GEOGRAFIA.CIUDAD.ESTADO',        'Ciudad - Estado | Activar/Desactivar ciudades', true),

('GEOGRAFIA.DISTRITO.LEER',        'Distrito - Leer | Listar/Ver/Buscar distritos', true),
('GEOGRAFIA.DISTRITO.ESCRIBIR',    'Distrito - Escribir | Crear/Editar distritos', true),
('GEOGRAFIA.DISTRITO.ESTADO',      'Distrito - Estado | Activar/Desactivar distritos', true),

-- CATALOGOS / MARCAS
('CATALOGOS.MARCA.LEER',     'Marca - Leer | Listar/Ver/Buscar marcas', true),
('CATALOGOS.MARCA.ESCRIBIR', 'Marca - Escribir | Crear/Editar marcas', true),
('CATALOGOS.MARCA.ESTADO',   'Marca - Estado | Activar/Desactivar marcas', true),

-- CATALOGOS / MODELOS
('CATALOGOS.MODELO.LEER',     'Modelo - Leer | Listar/Ver/Buscar modelos', true),
('CATALOGOS.MODELO.ESCRIBIR', 'Modelo - Escribir | Crear/Editar modelos', true),
('CATALOGOS.MODELO.ESTADO',   'Modelo - Estado | Activar/Desactivar modelos', true),

-- SISTEMA / USUARIOS
('SISTEMA.USUARIO.LEER',     'Usuario - Leer | Listar/Ver/Buscar usuarios', true),
('SISTEMA.USUARIO.ESCRIBIR', 'Usuario - Escribir | Crear/Editar usuarios', true),
('SISTEMA.USUARIO.ESTADO',   'Usuario - Estado | Activar/Desactivar usuarios', true),

-- CATALOGOS / SERVICIO
('CATALOGOS.SERVICIO.LEER',     'Servicio - Leer | Listar/Ver/Buscar servicios', true),
('CATALOGOS.SERVICIO.ESCRIBIR', 'Servicio - Escribir | Crear/Editar servicios', true),
('CATALOGOS.SERVICIO.ESTADO',   'Servicio - Estado | Activar/Desactivar servicios', true),

-- CATALOGOS / TIPO DOCUMENTO
('CATALOGOS.TIPO_DOCUMENTO.LEER',     'Tipo Documento - Leer | Listar/Ver/Buscar tipos de documento', true),
('CATALOGOS.TIPO_DOCUMENTO.ESCRIBIR', 'Tipo Documento - Escribir | Crear/Editar tipos de documento', true),
('CATALOGOS.TIPO_DOCUMENTO.ESTADO',   'Tipo Documento - Estado | Activar/Desactivar tipos de documento', true)

ON CONFLICT (nombre) DO UPDATE
SET descripcion = EXCLUDED.descripcion,
    activo = EXCLUDED.activo;

-- =============================================================================
-- RELACION ROLES_PERMISOS
-- =============================================================================

-- El desarrollador tiene acceso a todo
INSERT INTO public.roles_permisos (id_rol, id_permiso, activo)
SELECT r.id_rol, p.id_permiso, true
FROM public.roles r
JOIN public.permisos p ON true
WHERE r.nombre = 'DESARROLLADOR'
ON CONFLICT (id_rol, id_permiso) DO NOTHING;

-- El admin tiene acceso a todo menos gestión de usuarios
INSERT INTO public.roles_permisos (id_rol, id_permiso, activo)
SELECT r.id_rol, p.id_permiso, true
FROM public.roles r
JOIN public.permisos p
  ON p.nombre NOT LIKE 'SISTEMA.USUARIO.%'
WHERE r.nombre = 'ADMINISTRADOR'
ON CONFLICT (id_rol, id_permiso) DO NOTHING;

-- El operador tiene acceso a lectura (%.LEER) y sin usuarios
INSERT INTO public.roles_permisos (id_rol, id_permiso, activo)
SELECT r.id_rol, p.id_permiso, true
FROM public.roles r
JOIN public.permisos p
  ON p.nombre LIKE '%.LEER'
 AND p.nombre NOT LIKE 'SISTEMA.USUARIO.%'
WHERE r.nombre = 'OPERADOR'
ON CONFLICT (id_rol, id_permiso) DO NOTHING;

-- =============================================================================
-- USUARIO INICIAL DEL SISTEMA
-- =============================================================================
INSERT INTO public.usuarios (
    username,
    password,
    nombre,
    apellido,
    id_tipo_documento,
    numero_documento,
    email,
    telefono,
    id_rol,
    activo
)
SELECT
    'ELYRRAH',
    '$2a$10$kiLX4Pvfka6K8bDuXOfTQu.A.E8CpGw3VrdnPlMYNdk72x2VMXUyi',
    'JUAN',
    'ELIAS',
    td.id_tipo_documento,
    '6211195',
    'elyrrah.006@gmail.com',
    NULL,
    r.id_rol,
    true
FROM public.tipos_documento td
JOIN public.roles r ON r.nombre = 'DESARROLLADOR'
WHERE td.codigo = 'CI'  -- CORRECCIÓN: era td.abreviatura = 'CI'
ON CONFLICT (username) DO NOTHING;

COMMIT;

-- =============================================================================
-- FIN DEL SCRIPT 02_DATA
-- =============================================================================