-- -- ======================================================================================================================
-- 02 DATA
-- INCLUYE: TIPOS_DOCUMENTOS + MARCAS + ROLES + PERMISOS + USUARIO 
-- -- ======================================================================================================================

BEGIN

-- =============================================================================
-- TIPOS DE DOCUMENTO
-- =============================================================================

INSERT INTO public.tipos_documento (codigo, nombre, aplica_a, activo) VALUES
('CI',        'CEDULA DE IDENTIDAD',                      'PERSONA', true),
('DNI',       'DOCUMENTO NACIONAL DE IDENTIDAD',          'PERSONA', true),
('CPF',       'CADASTRO DE PESSOAS FISICAS',              'PERSONA', true),
('PASAPORTE', 'PASAPORTE',                                'PERSONA', true),
('RUC',       'REGISTRO UNICO DEL CONTRIBUYENTE',         'AMBOS',   true),
('CNPJ',      'CADASTRO NACIONAL DA PESSOA JURIDICA',     'EMPRESA', true),
('CUIT',      'CLAVE UNICA DE IDENTIFICACION TRIBUTARIA', 'EMPRESA', true),
('NIT',       'NUMERO DE IDENTIFICACION TRIBUTARIA',      'EMPRESA', true)
ON CONFLICT (codigo) DO NOTHING;

-- =============================================================================
-- MARCAS DE VEHICULOS
-- =============================================================================
INSERT INTO public.marcas (nombre)
VALUES
('MERCEDES-BENZ'),
('SCANIA'),
('NISSAN'),
('TOYOTA'),
('MAZDA'),
('CATERPILLAR'),
('JOHN DEERE'),
('YANMAR'),
('VALTRA'),
('MASSEY FERGUSON'),
('CBT'),
('FIAT'),
('CHEVROLET'),
('MITSUBISHI')
ON CONFLICT (nombre) DO NOTHING;

-- =============================================================================
-- MODELOS DE VEHICULOS
-- =============================================================================
INSERT INTO public.modelos (id_marca, nombre)
SELECT m.id_marca, v.modelo
FROM (
    VALUES
        -- =====================
        -- SCANIA
        -- =====================
        ('SCANIA', '112'),
        ('SCANIA', '113'),

        -- =====================
        -- TOYOTA
        -- =====================
        ('TOYOTA', 'VITZ'),
        ('TOYOTA', 'HILUX'),
        ('TOYOTA', 'COROLLA'),
        ('TOYOTA', 'LAND CRUISER'),

        -- =====================
        -- NISSAN
        -- =====================
        ('NISSAN', 'PATROL'),
        ('NISSAN', 'NAVARA'),
        ('NISSAN', 'FRONTIER'),

        -- =====================
        -- CHEVROLET
        -- =====================
        ('CHEVROLET', 'ASTRA'),
        ('CHEVROLET', 'S10'),

        -- =====================
        -- MITSUBISHI
        -- =====================
        ('MITSUBISHI', 'MONTERO'),
        ('MITSUBISHI', 'L200'),
        ('MITSUBISHI', 'PAJERO'),

        -- =====================
        -- FIAT
        -- =====================
        ('FIAT', 'STRADA'),
        ('FIAT', 'TORO'),

        -- =====================
        -- MAZDA
        -- =====================
        ('MAZDA', 'BT-50')
) AS v(marca, modelo)
JOIN public.marcas m
  ON m.nombre = v.marca
ON CONFLICT (id_marca, nombre) DO NOTHING;


-- =============================================================================
-- ROLES INICIALES
-- =============================================================================
INSERT INTO public.roles (codigo, nombre, descripcion) VALUES
('DESARROLLADOR', 'Desarrollador', 'Acceso completo para desarrollo y pruebas'),
('ADMINISTRADOR', 'Administrador', 'Administración del sistema y catálogos'),
('OPERADOR', 'Operador', 'Operación diaria (carga y consultas)')
ON CONFLICT (codigo) DO UPDATE
SET nombre = EXCLUDED.nombre,
    descripcion = EXCLUDED.descripcion;

-- =============================================================================
-- PERMISOS
-- =============================================================================
INSERT INTO public.permisos (codigo, nombre, descripcion)
VALUES
-- =========================
-- GEOGRAFIA / PAISES
-- =========================
('GEOGRAFIA.PAIS.LEER',      'País - Leer',      'Listar/Ver/Buscar países'),
('GEOGRAFIA.PAIS.ESCRIBIR',  'País - Escribir',  'Crear/Editar países'),
('GEOGRAFIA.PAIS.ESTADO',    'País - Estado',    'Activar/Desactivar países'),

-- =========================
-- GEOGRAFIA / DEPARTAMENTOS
-- =========================
('GEOGRAFIA.DEPARTAMENTO.LEER',     'Departamento - Leer',     'Listar/Ver/Buscar departamentos'),
('GEOGRAFIA.DEPARTAMENTO.ESCRIBIR', 'Departamento - Escribir', 'Crear/Editar departamentos'),
('GEOGRAFIA.DEPARTAMENTO.ESTADO',   'Departamento - Estado',   'Activar/Desactivar departamentos'),

-- =========================
-- GEOGRAFIA / CIUDADES
-- =========================
('GEOGRAFIA.CIUDAD.LEER',     'Ciudad - Leer',     'Listar/Ver/Buscar ciudades'),
('GEOGRAFIA.CIUDAD.ESCRIBIR', 'Ciudad - Escribir', 'Crear/Editar ciudades'),
('GEOGRAFIA.CIUDAD.ESTADO',   'Ciudad - Estado',   'Activar/Desactivar ciudades'),

-- =========================
-- GEOGRAFIA / DISTRITOS
-- =========================
('GEOGRAFIA.DISTRITO.LEER',     'Distrito - Leer',     'Listar/Ver/Buscar distritos'),
('GEOGRAFIA.DISTRITO.ESCRIBIR', 'Distrito - Escribir', 'Crear/Editar distritos'),
('GEOGRAFIA.DISTRITO.ESTADO',   'Distrito - Estado',   'Activar/Desactivar distritos'),

-- =========================
-- CATALOGOS / TIPO DOCUMENTO
-- =========================
('CATALOGOS.TIPO_DOCUMENTO.LEER','Tipo Documento - Leer', 'Listar/Ver/Buscar tipos de documento'),
('CATALOGOS.TIPO_DOCUMENTO.ESCRIBIR', 'Tipo Documento - Escribir', 'Crear/Editar tipos de documento'),
('CATALOGOS.TIPO_DOCUMENTO.ESTADO', 'Tipo Documento - Estado', 'Activar/Desactivar tipos de documento'),

-- =========================
-- CATALOGOS / MARCAS
-- =========================
('CATALOGOS.MARCA.LEER', 'Marca - Leer', 'Listar/Ver/Buscar marcas'),
('CATALOGOS.MARCA.ESCRIBIR', 'Marca - Escribir', 'Crear/Editar marcas'),
('CATALOGOS.MARCA.ESTADO', 'Marca - Estado', 'Activar/Desactivar marcas'),

-- =========================
-- CATALOGOS / MODELOS
-- =========================
('CATALOGOS.MODELO.LEER', 'Modelo - Leer', 'Listar/Ver/Buscar modelos'),
('CATALOGOS.MODELO.ESCRIBIR', 'Modelo - Escribir', 'Crear/Editar modelos'),
('CATALOGOS.MODELO.ESTADO', 'Modelo - Estado', 'Activar/Desactivar modelos'),

-- =========================
-- SISTEMA / USUARIOS
-- =========================
('SISTEMA.USUARIO.LEER', 'Usuario - Leer', 'Listar/Ver/Buscar usuarios'),
('SISTEMA.USUARIO.ESCRIBIR', 'Usuario - Escribir', 'Crear/Editar usuarios'),
('SISTEMA.USUARIO.ESTADO', 'Usuario - Estado', 'Activar/Desactivar usuarios'),

-- =========================
-- CATALOGOS / SERVICIO
-- =========================
('CATALOGOS.SERVICIO.LEER', 'Servicio - Leer', 'Listar/Ver/Buscar servicios'),
('CATALOGOS.SERVICIO.ESCRIBIR', 'Servicio - Escribir', 'Crear/Editar servicios'),
('CATALOGOS.SERVICIO.ESTADO', 'Servicio - Estado', 'Activar/Desactivar servicios')


-- CONTINUARA -------------------------------------------------------------------------------------------------------------------------------

ON CONFLICT (codigo) DO UPDATE
SET nombre = EXCLUDED.nombre,
    descripcion = EXCLUDED.descripcion;


-- =============================================================================
-- RELACION ROLES PERMISOS
-- =============================================================================

-- El desarrollador tiene acceso a todo
INSERT INTO public.roles_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM public.roles r
JOIN public.permisos p ON true
WHERE r.codigo = 'DESARROLLADOR'
ON CONFLICT (id_rol, id_permiso) DO NOTHING;

-- El admin tiene acceso a lectura parcial e insertar datos
INSERT INTO public.roles_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM public.roles r
JOIN public.permisos p
  ON p.codigo NOT LIKE 'SISTEMA.USUARIO.%'
WHERE r.codigo = 'ADMINISTRADOR'
ON CONFLICT (id_rol, id_permiso) DO NOTHING;

-- El operador tiene acceso a lectura parcial
INSERT INTO public.roles_permisos (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM public.roles r
JOIN public.permisos p
  ON p.codigo LIKE '%.LEER'
 AND p.codigo NOT LIKE 'SISTEMA.USUARIO.%'
WHERE r.codigo = 'OPERADOR'
ON CONFLICT (id_rol, id_permiso) DO NOTHING;

-- =============================================================================
-- USUARIO INICIAL DEL SISTEMA
--
-- Nota:
--   Usuario base para acceso inicial y tareas de administracion/desarrollo.
--   El password_hash es un placeholder y debe ser reemplazado por un hash real
--   generado desde la aplicacion.
-- =============================================================================

INSERT INTO public.usuarios (
    id_rol,
    nombre,
    apellido,
    nombre_usuario,
    email,
    password_hash,
    id_tipo_documento,
    numero_documento,
    fecha_nacimiento,
    activo
)
SELECT
    r.id_rol,
    'JUAN',
    'ELIAS',
    'ELYRRAH',
    'elyrrah.006@gmail.com',
    'PLACE_HOLDER',
    td.id_tipo_documento,
    '6211195',
    DATE '2004-12-06',
    true
FROM public.tipos_documento td
JOIN public.roles r ON r.codigo = 'DESARROLLADOR'
WHERE td.codigo = 'CI'
ON CONFLICT (email) DO NOTHING;

COMMIT;

-- =============================================================================
-- FIN DEL SCRIPT 02_DATA
-- =============================================================================