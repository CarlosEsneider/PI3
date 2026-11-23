-- ==============================================================================
-- PROYECTO INTEGRADOR CUARTO SEMESTRE (UNIAJC) - SIGEIV-VOLCANO FASE II
-- ESQUEMA COMPLETO PARA POSTGRESQL / SUPABASE
-- ==============================================================================

-- 1. TABLA ROL
CREATE TABLE IF NOT EXISTS rol (
    id_rol      SERIAL PRIMARY KEY,
    nombre_rol  VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
);

INSERT INTO rol (id_rol, nombre_rol, descripcion) VALUES
(1, 'Administrador / Gerente General', 'Acceso total y gobierno general del sistema. Autorización indelegable de bajas y ajustes.'),
(2, 'Jefe de Producción y Calidad',    'Responsable de manufactura, taller y control de acabados. Autoriza lotes de producción y emite dictámenes.'),
(3, 'Encargado de Almacén y Bodega',   'Responsable del inventario físico en bodega. Registra recepciones, despachos y conteos.'),
(4, 'Asesor Comercial / Ventas',       'Gestión comercial con clientes. Emite y autoriza remisiones de venta.'),
(5, 'Auditor / Consultor Externo',     'Fiscalización, trazabilidad forense y consulta exclusiva de reportes y kardex.')
ON CONFLICT (id_rol) DO UPDATE 
SET nombre_rol = EXCLUDED.nombre_rol, descripcion = EXCLUDED.descripcion;

SELECT setval('rol_id_rol_seq', (SELECT MAX(id_rol) FROM rol));

-- 2. TABLA USUARIO
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario      SERIAL PRIMARY KEY,
    nombre_usuario  VARCHAR(100) NOT NULL,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    contrasena      VARCHAR(255) NOT NULL, -- SHA-256 (64 hex characters)
    email           VARCHAR(150) DEFAULT '',
    id_rol          INT          NOT NULL REFERENCES rol(id_rol) ON UPDATE CASCADE ON DELETE RESTRICT,
    puede_autorizar BOOLEAN      DEFAULT FALSE,
    activo          BOOLEAN      DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_usuario_username ON usuario(username);
CREATE INDEX IF NOT EXISTS idx_usuario_rol      ON usuario(id_rol);

INSERT INTO usuario (id_usuario, nombre_usuario, username, contrasena, email, id_rol, puede_autorizar, activo) VALUES
(1,  'Carlos Espitia',   'admin',       '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'gerencia@volcanobarriles.com',   1, TRUE,  TRUE),
(2,  'Esteban Muñoz',    'esteban',     '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'direccion@volcanobarriles.com',  1, TRUE,  TRUE),
(3,  'Leandro Caldón',   'leandro',     '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin2@volcanobarriles.com',     1, TRUE,  TRUE),
(4,  'Mauricio Ledesma', 'mauricio',    '8b2c86e62b5e1e1ded8bcded37b72573a009e04bb54e53e36de3dcd63795de38', 'produccion@volcanobarriles.com', 2, TRUE,  TRUE),
(5,  'Ana García',       'ana.garcia',  '8b2c86e62b5e1e1ded8bcded37b72573a009e04bb54e53e36de3dcd63795de38', 'almacen@volcanobarriles.com',    3, FALSE, TRUE),
(6,  'Pedro López',      'pedro.lopez', '8b2c86e62b5e1e1ded8bcded37b72573a009e04bb54e53e36de3dcd63795de38', 'ventas@volcanobarriles.com',     4, TRUE,  TRUE),
(7,  'María Torres',     'maria.t',     '8b2c86e62b5e1e1ded8bcded37b72573a009e04bb54e53e36de3dcd63795de38', 'ventas2@volcanobarriles.com',    4, TRUE,  TRUE),
(8,  'Juan Ramírez',     'juan.r',      '47b48fbe11b42f7e61cba37f47377d8ba13040df57abba1fe9fa4750a4bbc6f4', 'auditoria@volcanobarriles.com', 5, FALSE, TRUE),
(9,  'Laura Díaz',       'laura.d',     '47b48fbe11b42f7e61cba37f47377d8ba13040df57abba1fe9fa4750a4bbc6f4', 'auditoria2@volcanobarriles.com',5, FALSE, TRUE),
(10, 'Roberto Sánchez',  'roberto.s',   '47b48fbe11b42f7e61cba37f47377d8ba13040df57abba1fe9fa4750a4bbc6f4', 'calidad@volcanobarriles.com',   2, TRUE,  TRUE)
ON CONFLICT (id_usuario) DO UPDATE 
SET nombre_usuario = EXCLUDED.nombre_usuario,
    username = EXCLUDED.username,
    contrasena = EXCLUDED.contrasena,
    email = EXCLUDED.email,
    id_rol = EXCLUDED.id_rol,
    puede_autorizar = EXCLUDED.puede_autorizar,
    activo = EXCLUDED.activo;

SELECT setval('usuario_id_usuario_seq', (SELECT MAX(id_usuario) FROM usuario));

-- 3. TABLA CATEGORIA
CREATE TABLE IF NOT EXISTS categoria (
    id_categoria      SERIAL PRIMARY KEY,
    nombre_categoria  VARCHAR(80) NOT NULL UNIQUE,
    descripcion       TEXT
);

INSERT INTO categoria (id_categoria, nombre_categoria, descripcion) VALUES
(1,  'Barriles de Asado',     'Barriles metálicos adaptados para asar carnes y alimentos en acero inoxidable'),
(2,  'Accesorios de Parrilla','Utensilios y herramientas complementarias para asados'),
(3,  'Carbón y Combustible',  'Carbón vegetal, briquetas y encendedores para parrillas'),
(4,  'Salsas y Adobos',       'Salsas artesanales, marinados y condimentos para asados'),
(5,  'Mesas y Soportes',      'Mesas laterales de teca y soportes estructurales para barriles'),
(6,  'Protección y Limpieza', 'Guantes térmicos, delantales, cepillos y fundas impermeables'),
(7,  'Iluminación Exterior',  'Luces magnéticas y lámparas LED para zonas de asado'),
(8,  'Repuestos',             'Rejillas, termómetros y partes de repuesto para barriles'),
(9,  'Kits de Asado',         'Conjuntos completos en maletines con herramientas parrilleras'),
(10, 'Decoración BBQ',        'Artículos decorativos y tablas temáticas de barbacoa')
ON CONFLICT (id_categoria) DO UPDATE 
SET nombre_categoria = EXCLUDED.nombre_categoria, descripcion = EXCLUDED.descripcion;

SELECT setval('categoria_id_categoria_seq', (SELECT MAX(id_categoria) FROM categoria));

-- 4. TABLA PROVEEDOR
CREATE TABLE IF NOT EXISTS proveedor (
    id_proveedor  SERIAL PRIMARY KEY,
    empresa       VARCHAR(150) NOT NULL,
    contacto      VARCHAR(100),
    telefono      VARCHAR(20)
);

INSERT INTO proveedor (id_proveedor, empresa, contacto, telefono) VALUES
(1,  'Metalúrgica del Valle',  'Jorge Méndez',    '3101234567'),
(2,  'Distribuidora El Fogón', 'Patricia Ruiz',   '3209876543'),
(3,  'Aceros Colombia SAS',    'Ricardo Vargas',  '3157894561'),
(4,  'Carbones del Pacífico',  'Claudia Herrera', '3182345678'),
(5,  'BBQ Importaciones',      'Andrés Caicedo',  '3223456789'),
(6,  'Industrias Parrilleras', 'Mónica Salazar',  '3164567890'),
(7,  'Ferretería Nacional',    'Hugo Castañeda',  '3195678901'),
(8,  'Salsas La Brasa',        'Camila Ortega',   '3136789012'),
(9,  'Maderas y Carbón SA',    'Felipe Arango',   '3207890123'),
(10, 'Accesorios BBQ Pro',     'Valentina Ríos',  '3118901234')
ON CONFLICT (id_proveedor) DO UPDATE 
SET empresa = EXCLUDED.empresa, contacto = EXCLUDED.contacto, telefono = EXCLUDED.telefono;

SELECT setval('proveedor_id_proveedor_seq', (SELECT MAX(id_proveedor) FROM proveedor));

-- 5. TABLA PRODUCTO (ASADORES Y COMPLEMENTOS)
CREATE TABLE IF NOT EXISTS producto (
    id_producto      SERIAL PRIMARY KEY,
    codigo_sku       VARCHAR(50)    NOT NULL UNIQUE,
    nombre_producto  VARCHAR(150)   NOT NULL,
    dimensiones      VARCHAR(150)   DEFAULT '',
    costo_estandar   NUMERIC(10,2)  NOT NULL DEFAULT 0 CHECK (costo_estandar >= 0),
    precio           NUMERIC(10,2)  NOT NULL CHECK (precio >= 0),
    stock_actual     INT            NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    stock_minimo     INT            NOT NULL DEFAULT 5 CHECK (stock_minimo >= 0),
    id_categoria     INT            NOT NULL REFERENCES categoria(id_categoria) ON UPDATE CASCADE ON DELETE RESTRICT,
    id_proveedor     INT            NOT NULL REFERENCES proveedor(id_proveedor) ON UPDATE CASCADE ON DELETE RESTRICT,
    img_url          VARCHAR(255)   DEFAULT NULL,
    activo           BOOLEAN        DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_producto_sku       ON producto(codigo_sku);
CREATE INDEX IF NOT EXISTS idx_producto_nombre    ON producto(nombre_producto);
CREATE INDEX IF NOT EXISTS idx_producto_categoria ON producto(id_categoria);
CREATE INDEX IF NOT EXISTS idx_producto_proveedor ON producto(id_proveedor);

INSERT INTO producto (id_producto, codigo_sku, nombre_producto, dimensiones, costo_estandar, precio, stock_actual, stock_minimo, id_categoria, id_proveedor, img_url, activo) VALUES
(1,  'ASAD-CLA-200L',  'Barril Asador Clásico 200L',      'Alto 95cm, Diámetro 60cm, Calibre 18 AISI 430', 210000.00, 350000.00,  43, 5,  1, 1, NULL, TRUE),
(2,  'ASAD-PREM-200L', 'Barril Asador Premium 200L',      'Alto 100cm, Diámetro 60cm, Calibre 16 AISI 304',310000.00, 520000.00,  15, 3,  1, 1, NULL, TRUE),
(3,  'ASAD-MINI-100L', 'Barril Asador Mini 100L',         'Alto 75cm, Diámetro 45cm, Calibre 18 AISI 430', 160000.00, 280000.00,  30, 5,  1, 3, NULL, TRUE),
(4,  'ASAD-AHUM-PROF', 'Barril Ahumador Profesional',     'Alto 115cm, Diámetro 65cm, Calibre 16 AISI 304',420000.00, 680000.00,  10, 2,  1, 1, NULL, TRUE),
(5,  'ACC-PINZ-INOX',  'Set Pinzas Acero Inoxidable',      'Longitud 45cm, Acero Inoxidable AISI 304',       22000.00,  45000.00,  50, 10, 2, 6, NULL, TRUE),
(6,  'ACC-GUAN-TERM',  'Guantes Térmicos para Parrilla',   'Talla L, Protección Térmica 500°C',               17000.00,  35000.00,  40, 8,  6, 5, NULL, TRUE),
(7,  'COMB-CARB-5KG',  'Carbón Vegetal Premium 5kg',       'Bulto 5kg Carbón Vegetal Roble',                  9000.00,  18000.00, 100, 20, 3, 4, NULL, TRUE),
(8,  'COMB-BRIQ-4KG',  'Briquetas de Carbón 4kg',          'Bolsa 4kg Briquetas Alta Duración',              11000.00,  22000.00,  80, 15, 3, 9, NULL, TRUE),
(9,  'ADOB-BBQ-500ML', 'Salsa BBQ Artesanal 500ml',        'Botella 500ml Salsa BBQ Ahumada',                 6000.00,  12000.00,  60, 10, 4, 8, NULL, TRUE),
(10, 'ADOB-ESP-350G',  'Adobo Especial Volcano 350g',      'Frasco 350g Especias Volcano',                    7500.00,  15000.00,  45, 10, 4, 8, NULL, TRUE),
(11, 'ACC-MESA-PLEG',  'Mesa Lateral Plegable para Barril','Madera Teca y Soporte Inox 60x40cm',             52000.00,  95000.00,  20, 5,  5, 7, NULL, TRUE),
(12, 'ACC-CEPI-LIMP',  'Cepillo Limpiador de Parrilla',     'Cerda Acero Inoxidable Mango Madera',             14000.00,  28000.00,  35, 8,  6, 6, NULL, TRUE),
(13, 'KIT-ASAD-12PZ',  'Kit Asador Completo 12 Piezas',   'Estuche Maletín Aluminio con 12 Piezas',          68000.00, 120000.00,  18, 4,  9, 5, NULL, TRUE),
(14, 'ACC-LAMP-LED',   'Lámpara LED para Parrilla',        'Pinza Magnética Cuello Flexible LED',             23000.00,  42000.00,  25, 5,  7, 7, NULL, TRUE),
(15, 'REP-REJI-UNIV',  'Rejilla de Repuesto Universal',     'Acero AISI 304 Diámetro 55cm',                    30000.00,  55000.00,  30, 5,  8, 3, NULL, TRUE)
ON CONFLICT (id_producto) DO UPDATE 
SET codigo_sku = EXCLUDED.codigo_sku,
    nombre_producto = EXCLUDED.nombre_producto,
    dimensiones = EXCLUDED.dimensiones,
    costo_estandar = EXCLUDED.costo_estandar,
    precio = EXCLUDED.precio,
    stock_actual = EXCLUDED.stock_actual,
    stock_minimo = EXCLUDED.stock_minimo,
    id_categoria = EXCLUDED.id_categoria,
    id_proveedor = EXCLUDED.id_proveedor,
    activo = EXCLUDED.activo;

SELECT setval('producto_id_producto_seq', (SELECT MAX(id_producto) FROM producto));

-- 6. TABLA CLIENTE
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente      SERIAL PRIMARY KEY,
    nombre_cliente  VARCHAR(150) NOT NULL,
    dni             VARCHAR(20)  UNIQUE,
    telefono        VARCHAR(20)
);

CREATE INDEX IF NOT EXISTS idx_cliente_nombre ON cliente(nombre_cliente);
CREATE INDEX IF NOT EXISTS idx_cliente_dni    ON cliente(dni);

INSERT INTO cliente (id_cliente, nombre_cliente, dni, telefono) VALUES
(1,  'Restaurante El Fogón Caleño',  '900123456', '3201234567'),
(2,  'Asados Don Julio',             '900234567', '3152345678'),
(3,  'Carlos Andrés Mejía',          '1144567890','3183456789'),
(4,  'María Fernanda Ospina',        '1144678901','3114567890'),
(5,  'Parrilla Express SAS',         '900345678', '3225678901'),
(6,  'Diego Alejandro Parra',        '1144789012','3166789012'),
(7,  'Eventos & Parrilladas CO',     '900456789', '3197890123'),
(8,  'Valentina Castaño Ruiz',       '1144890123','3138901234'),
(9,  'BBQ House Colombia',           '900567890', '3209012345'),
(10, 'Andrés Felipe Quintero',       '1144901234','3170123456'),
(11, 'Sabores al Carbón',            '900678901', '3141234567'),
(12, 'Juliana Restrepo Gómez',       '1145012345','3212345678')
ON CONFLICT (id_cliente) DO UPDATE 
SET nombre_cliente = EXCLUDED.nombre_cliente, dni = EXCLUDED.dni, telefono = EXCLUDED.telefono;

SELECT setval('cliente_id_cliente_seq', (SELECT MAX(id_cliente) FROM cliente));

-- 7. TABLA VENTA
CREATE TABLE IF NOT EXISTS venta (
    id_venta    SERIAL PRIMARY KEY,
    fecha       TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_cliente  INT            NOT NULL REFERENCES cliente(id_cliente) ON UPDATE CASCADE ON DELETE RESTRICT,
    id_usuario  INT            NOT NULL REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    total       NUMERIC(10,2)  NOT NULL DEFAULT 0 CHECK (total >= 0)
);

CREATE INDEX IF NOT EXISTS idx_venta_fecha   ON venta(fecha);
CREATE INDEX IF NOT EXISTS idx_venta_cliente ON venta(id_cliente);

INSERT INTO venta (id_venta, fecha, id_cliente, id_usuario, total) VALUES
(1,  '2026-03-01 10:30:00-05', 1,  1, 745000.00),
(2,  '2026-03-03 14:15:00-05', 2,  4, 350000.00),
(3,  '2026-03-05 09:00:00-05', 3,  5,  63000.00),
(4,  '2026-03-08 16:45:00-05', 4,  4, 520000.00),
(5,  '2026-03-10 11:20:00-05', 5,  1, 1060000.00),
(6,  '2026-03-12 13:00:00-05', 6,  6,  95000.00),
(7,  '2026-03-15 10:00:00-05', 7,  5, 680000.00),
(8,  '2026-03-18 15:30:00-05', 8,  4,  57000.00),
(9,  '2026-03-20 09:45:00-05', 9,  7, 280000.00),
(10, '2026-03-22 12:00:00-05', 10, 1, 120000.00),
(11, '2026-03-25 14:30:00-05', 11, 6, 396000.00),
(12, '2026-03-28 16:00:00-05', 12, 5,  45000.00)
ON CONFLICT (id_venta) DO UPDATE 
SET total = EXCLUDED.total, fecha = EXCLUDED.fecha;

SELECT setval('venta_id_venta_seq', (SELECT MAX(id_venta) FROM venta));

-- 8. TABLA DETALLE VENTA
CREATE TABLE IF NOT EXISTS detalle_venta (
    id_detalle      SERIAL PRIMARY KEY,
    id_venta        INT            NOT NULL REFERENCES venta(id_venta) ON UPDATE CASCADE ON DELETE CASCADE,
    id_producto     INT            NOT NULL REFERENCES producto(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT,
    cantidad        INT            NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2)  NOT NULL CHECK (precio_unitario >= 0),
    subtotal        NUMERIC(10,2)  NOT NULL CHECK (subtotal >= 0)
);

CREATE INDEX IF NOT EXISTS idx_detalle_venta_venta    ON detalle_venta(id_venta);
CREATE INDEX IF NOT EXISTS idx_detalle_venta_producto ON detalle_venta(id_producto);

INSERT INTO detalle_venta (id_detalle, id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1,  1,  1,  1, 350000.00, 350000.00),
(2,  1,  5,  2,  45000.00,  90000.00),
(3,  1,  7,  3,  18000.00,  54000.00),
(4,  1,  9,  1,  12000.00,  12000.00),
(5,  2,  1,  1, 350000.00, 350000.00),
(6,  3,  5,  1,  45000.00,  45000.00),
(7,  3,  7,  1,  18000.00,  18000.00),
(8,  4,  2,  1, 520000.00, 520000.00),
(9,  5,  2,  2, 520000.00, 1040000.00),
(10, 5, 12,  1,  28000.00,  28000.00),
(11, 6, 11,  1,  95000.00,  95000.00),
(12, 7,  4,  1, 680000.00, 680000.00),
(13, 8,  9,  2,  12000.00,  24000.00),
(14, 8, 10,  1,  15000.00,  15000.00),
(15, 8,  7,  1,  18000.00,  18000.00),
(16, 9,  3,  1, 280000.00, 280000.00),
(17, 10, 13, 1, 120000.00, 120000.00),
(18, 11,  1, 1, 350000.00, 350000.00),
(19, 11,  6, 1,  35000.00,  35000.00),
(20, 12,  5, 1,  45000.00,  45000.00)
ON CONFLICT (id_detalle) DO NOTHING;

SELECT setval('detalle_venta_id_detalle_seq', (SELECT MAX(id_detalle) FROM detalle_venta));

-- 9. TABLA COMPRA
CREATE TABLE IF NOT EXISTS compra (
    id_compra     SERIAL PRIMARY KEY,
    fecha         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total         NUMERIC(10,2)  NOT NULL DEFAULT 0 CHECK (total >= 0),
    id_proveedor  INT            NOT NULL REFERENCES proveedor(id_proveedor) ON UPDATE CASCADE ON DELETE RESTRICT,
    id_usuario    INT            NOT NULL REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_compra_fecha     ON compra(fecha);
CREATE INDEX IF NOT EXISTS idx_compra_proveedor ON compra(id_proveedor);

INSERT INTO compra (id_compra, fecha, total, id_proveedor, id_usuario) VALUES
(1, '2026-02-15 09:00:00-05', 3500000.00, 1, 1),
(2, '2026-02-20 14:30:00-05', 1800000.00, 4, 1),
(3, '2026-03-01 10:00:00-05',  450000.00, 6, 2),
(4, '2026-03-10 11:15:00-05', 2200000.00, 3, 1),
(5, '2026-03-20 16:00:00-05',  960000.00, 8, 2)
ON CONFLICT (id_compra) DO UPDATE SET total = EXCLUDED.total;

SELECT setval('compra_id_compra_seq', (SELECT MAX(id_compra) FROM compra));

-- 10. TABLA DETALLE COMPRA
CREATE TABLE IF NOT EXISTS detalle_compra (
    id_detalle_compra  SERIAL PRIMARY KEY,
    id_compra          INT            NOT NULL REFERENCES compra(id_compra) ON UPDATE CASCADE ON DELETE CASCADE,
    id_producto        INT            NOT NULL REFERENCES producto(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT,
    cantidad           INT            NOT NULL CHECK (cantidad > 0),
    precio_unitario    NUMERIC(10,2)  NOT NULL CHECK (precio_unitario >= 0)
);

CREATE INDEX IF NOT EXISTS idx_detalle_compra_compra   ON detalle_compra(id_compra);
CREATE INDEX IF NOT EXISTS idx_detalle_compra_producto ON detalle_compra(id_producto);

INSERT INTO detalle_compra (id_detalle_compra, id_compra, id_producto, cantidad, precio_unitario) VALUES
(1, 1, 1,  10, 350000.00),
(2, 2, 7, 100,  18000.00),
(3, 3, 5,  10,  45000.00),
(4, 4, 3,  10, 220000.00),
(5, 5, 9,  80,  12000.00)
ON CONFLICT (id_detalle_compra) DO NOTHING;

SELECT setval('detalle_compra_id_detalle_compra_seq', (SELECT MAX(id_detalle_compra) FROM detalle_compra));

-- ==============================================================================
-- FASE II: SUBSISTEMA DE MOVIMIENTOS DE ALMACEN, KARDEX Y AUTORIZACIONES
-- ==============================================================================

-- 11. TABLA TIPO MOVIMIENTO
CREATE TABLE IF NOT EXISTS tipo_movimiento (
    id_tipo_movimiento        SERIAL PRIMARY KEY,
    codigo_movimiento         VARCHAR(20)  NOT NULL UNIQUE,
    descripcion               VARCHAR(150) NOT NULL,
    naturaleza_afectacion     VARCHAR(20)  NOT NULL, -- ENTRADA, SALIDA, AJUSTE
    rol_autorizador_requerido VARCHAR(50)  NOT NULL
);

INSERT INTO tipo_movimiento (id_tipo_movimiento, codigo_movimiento, descripcion, naturaleza_afectacion, rol_autorizador_requerido) VALUES
(1, 'ENT_PROD', 'Entrada por Lote de Producción', 'ENTRADA', 'Jefe de Producción'),
(2, 'SAL_VENT', 'Salida por Despacho de Venta', 'SALIDA', 'Asesor Comercial'),
(3, 'DEV_CLIE', 'Devolución de Cliente con Dictamen', 'ENTRADA', 'Jefe de Producción'),
(4, 'AJU_FISI', 'Ajuste por Inventario Físico', 'AJUSTE', 'Administrador'),
(5, 'BAJ_MERM', 'Baja por Merma Técnica / Avería', 'SALIDA', 'Administrador')
ON CONFLICT (id_tipo_movimiento) DO UPDATE 
SET codigo_movimiento = EXCLUDED.codigo_movimiento,
    descripcion = EXCLUDED.descripcion,
    naturaleza_afectacion = EXCLUDED.naturaleza_afectacion,
    rol_autorizador_requerido = EXCLUDED.rol_autorizador_requerido;

SELECT setval('tipo_movimiento_id_tipo_movimiento_seq', (SELECT MAX(id_tipo_movimiento) FROM tipo_movimiento));

-- 12. TABLA MOVIMIENTO INVENTARIO (DOCUMENTO SOPORTE)
CREATE TABLE IF NOT EXISTS movimiento_inventario (
    id_movimiento            SERIAL PRIMARY KEY,
    numero_documento_soporte VARCHAR(50)  NOT NULL UNIQUE,
    fecha_hora               TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_tipo_movimiento       INT          NOT NULL REFERENCES tipo_movimiento(id_tipo_movimiento) ON UPDATE CASCADE ON DELETE RESTRICT,
    id_usuario_creador       INT          NOT NULL REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    id_usuario_autorizador   INT          DEFAULT NULL REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    estado_autorizacion      VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE, APROBADO, RECHAZADO
    fecha_autorizacion       TIMESTAMPTZ  DEFAULT NULL,
    observaciones            TEXT
);

CREATE INDEX IF NOT EXISTS idx_mov_estado ON movimiento_inventario(estado_autorizacion);
CREATE INDEX IF NOT EXISTS idx_mov_doc    ON movimiento_inventario(numero_documento_soporte);
CREATE INDEX IF NOT EXISTS idx_mov_tipo   ON movimiento_inventario(id_tipo_movimiento);

-- 13. TABLA DETALLE MOVIMIENTO
CREATE TABLE IF NOT EXISTS detalle_movimiento (
    id_detalle_movimiento SERIAL PRIMARY KEY,
    id_movimiento         INT            NOT NULL REFERENCES movimiento_inventario(id_movimiento) ON UPDATE CASCADE ON DELETE CASCADE,
    id_producto           INT            NOT NULL REFERENCES producto(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT,
    cantidad              INT            NOT NULL CHECK (cantidad > 0),
    costo_unitario        NUMERIC(10,2)  NOT NULL DEFAULT 0,
    saldo_resultante      INT            DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_detmov_mov  ON detalle_movimiento(id_movimiento);
CREATE INDEX IF NOT EXISTS idx_detmov_prod ON detalle_movimiento(id_producto);

-- 14. TABLA KARDEX (ASIENTOS INMUTABLES)
CREATE TABLE IF NOT EXISTS kardex (
    id_kardex          SERIAL PRIMARY KEY,
    id_producto        INT            NOT NULL REFERENCES producto(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT,
    fecha_hora         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_movimiento      INT            NOT NULL REFERENCES movimiento_inventario(id_movimiento) ON UPDATE CASCADE ON DELETE RESTRICT,
    tipo_evento        VARCHAR(50)    NOT NULL,
    numero_soporte     VARCHAR(50)    NOT NULL,
    cantidad_entrada   INT            NOT NULL DEFAULT 0,
    cantidad_salida    INT            NOT NULL DEFAULT 0,
    saldo_stock        INT            NOT NULL,
    costo_unitario     NUMERIC(10,2)  NOT NULL DEFAULT 0,
    costo_total_saldo  NUMERIC(12,2)  NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_kardex_prod  ON kardex(id_producto);
CREATE INDEX IF NOT EXISTS idx_kardex_fecha ON kardex(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_kardex_mov   ON kardex(id_movimiento);

-- 15. TABLA AUDITORIA FORENSE
CREATE TABLE IF NOT EXISTS auditoria (
    id_auditoria          SERIAL PRIMARY KEY,
    fecha_hora            TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario            INT          NOT NULL REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    accion                VARCHAR(50)  NOT NULL, -- AUTORIZACION, RECHAZO, CREACION, MODIFICACION
    tabla_afectada        VARCHAR(50)  NOT NULL,
    id_registro_afectado  INT          NOT NULL,
    detalle               TEXT
);

CREATE INDEX IF NOT EXISTS idx_auditoria_usuario ON auditoria(id_usuario);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha   ON auditoria(fecha_hora);

-- ==============================================================================
-- SEMILLAS DE MOVIMIENTOS PENDIENTES PARA PRUEBAS DE LA BANDEJA DE AUTORIZACIONES
-- ==============================================================================

INSERT INTO movimiento_inventario (id_movimiento, numero_documento_soporte, fecha_hora, id_tipo_movimiento, id_usuario_creador, estado_autorizacion, observaciones) VALUES
(1, 'OP-2026-088',  '2026-11-20 10:15:00-05', 1, 5, 'PENDIENTE', 'Entrega de lote de fabricación finalizado en taller metalmecánico. Soldadura TIG y pulido AISI 430 aprobados por Almacén.'),
(2, 'REM-2026-0142','2026-11-20 11:30:00-05', 2, 5, 'PENDIENTE', 'Remisión de despacho por venta corporativa Restaurante Asados Don Julio. Factura F-554.'),
(3, 'AJU-2026-0005','2026-11-20 14:00:00-05', 4, 5, 'PENDIENTE', 'Acta de conteo físico mensual en bodega producto terminado. Discrepancia detectada en modelo Ahumador.'),
(4, 'BAJ-2026-0012','2026-11-20 15:45:00-05', 5, 4, 'PENDIENTE', 'Acta de baja por golpe severo y fisura irreparable durante flete de despacho nacional. Requiere aprobación de Gerencia.'),
(5, 'DEV-2026-0021','2026-11-20 16:20:00-05', 3, 6, 'PENDIENTE', 'Devolución por garantía de acabado térmico en tapa superior. Dictamen técnico solicita reingreso a inventario tras revisión.')
ON CONFLICT (id_movimiento) DO NOTHING;

SELECT setval('movimiento_inventario_id_movimiento_seq', (SELECT MAX(id_movimiento) FROM movimiento_inventario));

INSERT INTO detalle_movimiento (id_detalle_movimiento, id_movimiento, id_producto, cantidad, costo_unitario, saldo_resultante) VALUES
(1, 1, 1, 10, 210000.00, 43),
(2, 1, 3,  5, 160000.00, 35),
(3, 2, 2,  2, 310000.00, 13),
(4, 2, 5,  4,  22000.00, 46),
(5, 3, 4,  1, 420000.00,  9),
(6, 4, 1,  1, 210000.00, 24),
(7, 5, 2,  1, 310000.00, 16)
ON CONFLICT (id_detalle_movimiento) DO NOTHING;

SELECT setval('detalle_movimiento_id_detalle_movimiento_seq', (SELECT MAX(id_detalle_movimiento) FROM detalle_movimiento));

-- ==============================================================================
-- VISTAS ANALITICAS Y DE CONTROL (SCE UNIAJC)
-- ==============================================================================

-- Vista 1: Historial consolidado del Kardex con nombres y soportes
CREATE OR REPLACE VIEW vw_kardex_general AS
SELECT 
    k.id_kardex,
    k.fecha_hora,
    p.codigo_sku,
    p.nombre_producto,
    c.nombre_categoria,
    k.tipo_evento,
    k.numero_soporte,
    k.cantidad_entrada,
    k.cantidad_salida,
    k.saldo_stock,
    k.costo_unitario,
    k.costo_total_saldo
FROM kardex k
JOIN producto p ON k.id_producto = p.id_producto
JOIN categoria c ON p.id_categoria = c.id_categoria
ORDER BY k.fecha_hora DESC;

-- Vista 2: Tablero de alertas de stock crítico y preventivo
CREATE OR REPLACE VIEW vw_alertas_stock_critico AS
SELECT 
    p.id_producto,
    p.codigo_sku,
    p.nombre_producto,
    c.nombre_categoria,
    p.stock_actual,
    p.stock_minimo,
    CASE 
        WHEN p.stock_actual = 0 THEN 'AGOTADO'
        WHEN p.stock_actual <= p.stock_minimo THEN 'CRITICO'
        WHEN p.stock_actual <= (p.stock_minimo * 1.5) THEN 'PREVENTIVO'
        ELSE 'OPTIMO'
    END AS estado_semaforo,
    CASE 
        WHEN p.stock_actual <= p.stock_minimo THEN (p.stock_minimo * 2) - p.stock_actual
        ELSE 0
    END AS cantidad_sugerida_produccion
FROM producto p
JOIN categoria c ON p.id_categoria = c.id_categoria
WHERE p.activo = TRUE
ORDER BY 
    CASE 
        WHEN p.stock_actual = 0 THEN 1
        WHEN p.stock_actual <= p.stock_minimo THEN 2
        WHEN p.stock_actual <= (p.stock_minimo * 1.5) THEN 3
        ELSE 4
    END,
    p.stock_actual ASC;

-- Vista 3: Valorización económica del inventario por categoría
CREATE OR REPLACE VIEW vw_valorizacion_inventario AS
SELECT 
    c.id_categoria,
    c.nombre_categoria,
    COUNT(p.id_producto) AS total_referencias,
    SUM(p.stock_actual) AS total_unidades_stock,
    SUM(p.stock_actual * p.costo_estandar) AS valor_total_costo_fabricacion,
    SUM(p.stock_actual * p.precio) AS valor_total_precio_comercial,
    SUM(p.stock_actual * p.precio) - SUM(p.stock_actual * p.costo_estandar) AS margen_bruto_potencial
FROM categoria c
LEFT JOIN producto p ON c.id_categoria = p.id_categoria AND p.activo = TRUE
GROUP BY c.id_categoria, c.nombre_categoria
ORDER BY valor_total_precio_comercial DESC;
