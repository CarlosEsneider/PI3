
DROP DATABASE IF EXISTS sigeiv_volcano;
CREATE DATABASE sigeiv_volcano CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sigeiv_volcano;

CREATE TABLE rol (
    id_rol      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol  VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE usuario (
    id_usuario      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario  VARCHAR(100) NOT NULL,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    contrasena      VARCHAR(255) NOT NULL,
    id_rol          INT          NOT NULL,
    activo          BOOLEAN      DEFAULT TRUE,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE categoria (
    id_categoria      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria  VARCHAR(80)  NOT NULL UNIQUE,
    descripcion       TEXT
) ENGINE=InnoDB;

CREATE TABLE proveedor (
    id_proveedor  INT AUTO_INCREMENT PRIMARY KEY,
    empresa       VARCHAR(150) NOT NULL,
    contacto      VARCHAR(100),
    telefono      VARCHAR(20)
) ENGINE=InnoDB;

CREATE TABLE producto (
    id_producto      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_producto  VARCHAR(150)   NOT NULL,
    precio           DECIMAL(10,2)  NOT NULL CHECK (precio >= 0),
    stock_actual     INT            NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    stock_minimo     INT            NOT NULL DEFAULT 5 CHECK (stock_minimo >= 0),
    id_categoria     INT            NOT NULL,
    id_proveedor     INT            NOT NULL,
    img_url          VARCHAR(255)   DEFAULT NULL,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria)
        REFERENCES categoria(id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_producto_proveedor FOREIGN KEY (id_proveedor)
        REFERENCES proveedor(id_proveedor) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_producto_nombre    ON producto(nombre_producto);
CREATE INDEX idx_producto_categoria ON producto(id_categoria);
CREATE INDEX idx_producto_proveedor ON producto(id_proveedor);

CREATE TABLE cliente (
    id_cliente      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_cliente  VARCHAR(150) NOT NULL,
    dni             VARCHAR(20)  UNIQUE,
    telefono        VARCHAR(20)
) ENGINE=InnoDB;

CREATE INDEX idx_cliente_nombre ON cliente(nombre_cliente);
CREATE INDEX idx_cliente_dni    ON cliente(dni);

CREATE TABLE venta (
    id_venta    INT AUTO_INCREMENT PRIMARY KEY,
    fecha       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_cliente  INT         NOT NULL,
    id_usuario  INT         NOT NULL,
    total       DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_venta_fecha   ON venta(fecha);
CREATE INDEX idx_venta_cliente ON venta(id_cliente);

CREATE TABLE detalle_venta (
    id_detalle      INT AUTO_INCREMENT PRIMARY KEY,
    id_venta        INT            NOT NULL,
    id_producto     INT            NOT NULL,
    cantidad        INT            NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2)  NOT NULL CHECK (precio_unitario >= 0),
    subtotal        DECIMAL(10,2)  NOT NULL CHECK (subtotal >= 0),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta)
        REFERENCES venta(id_venta) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto)
        REFERENCES producto(id_producto) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_detalle_venta    ON detalle_venta(id_venta);
CREATE INDEX idx_detalle_producto ON detalle_venta(id_producto);

CREATE TABLE compra (
    id_compra     INT AUTO_INCREMENT PRIMARY KEY,
    fecha         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total         DECIMAL(10,2)  NOT NULL DEFAULT 0 CHECK (total >= 0),
    id_proveedor  INT            NOT NULL,
    id_usuario    INT            NOT NULL,
    CONSTRAINT fk_compra_proveedor FOREIGN KEY (id_proveedor)
        REFERENCES proveedor(id_proveedor) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_compra_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_compra_fecha     ON compra(fecha);
CREATE INDEX idx_compra_proveedor ON compra(id_proveedor);

CREATE TABLE detalle_compra (
    id_detalle_compra  INT AUTO_INCREMENT PRIMARY KEY,
    id_compra          INT            NOT NULL,
    id_producto        INT            NOT NULL,
    cantidad           INT            NOT NULL CHECK (cantidad > 0),
    precio_unitario    DECIMAL(10,2)  NOT NULL CHECK (precio_unitario >= 0),
    CONSTRAINT fk_detalle_compra_compra FOREIGN KEY (id_compra)
        REFERENCES compra(id_compra) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_compra_producto FOREIGN KEY (id_producto)
        REFERENCES producto(id_producto) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_detalle_compra_compra   ON detalle_compra(id_compra);
CREATE INDEX idx_detalle_compra_producto ON detalle_compra(id_producto);

INSERT INTO rol (nombre_rol) VALUES
('Administrador'),
('Vendedor'),
('Consultor');

-- Usuarios (10 registros) - Contrasenas hasheadas con SHA-256
-- Contrasena por defecto: "admin123" = 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- Contrasena por defecto: "vendedor123" = 8b2c86e62b5e1e1ded8bcded37b72573a009e04bb54e53e36de3dcd63795de38
-- Contrasena por defecto: "consultor123" = 47b48fbe11b42f7e61cba37f47377d8ba13040df57abba1fe9fa4750a4bbc6f4
INSERT INTO usuario (nombre_usuario, username, contrasena, id_rol, activo) VALUES
('Carlos Espitia',   'admin',      '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1, TRUE),
('Esteban Munoz',    'esteban',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1, TRUE),
('Leandro Caldon',   'leandro',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1, TRUE),
('Mauricio Ledesma', 'mauricio',   '8b2c86e62b5e1e1ded8bcded37b72573a009e04bb54e53e36de3dcd63795de38', 2, TRUE),
('Ana Garcia',       'ana.garcia', '8b2c86e62b5e1e1ded8bcded37b72573a009e04bb54e53e36de3dcd63795de38', 2, TRUE),
('Pedro Lopez',      'pedro.lopez','8b2c86e62b5e1e1ded8bcded37b72573a009e04bb54e53e36de3dcd63795de38', 2, TRUE),
('Maria Torres',     'maria.t',    '8b2c86e62b5e1e1ded8bcded37b72573a009e04bb54e53e36de3dcd63795de38', 2, TRUE),
('Juan Ramirez',     'juan.r',     '47b48fbe11b42f7e61cba37f47377d8ba13040df57abba1fe9fa4750a4bbc6f4', 3, TRUE),
('Laura Diaz',       'laura.d',    '47b48fbe11b42f7e61cba37f47377d8ba13040df57abba1fe9fa4750a4bbc6f4', 3, TRUE),
('Roberto Sanchez',  'roberto.s',  '47b48fbe11b42f7e61cba37f47377d8ba13040df57abba1fe9fa4750a4bbc6f4', 3, TRUE);

INSERT INTO categoria (nombre_categoria, descripcion) VALUES
('Barriles de Asado',     'Barriles metalicos adaptados para asar carnes y alimentos'),
('Accesorios de Parrilla','Utensilios y herramientas complementarias para asados'),
('Carbon y Combustible',  'Carbon vegetal, briquetas y encendedores para parrillas'),
('Salsas y Adobos',       'Salsas, marinados y condimentos para asados'),
('Mesas y Soportes',      'Mesas laterales y soportes para barriles'),
('Proteccion y Limpieza', 'Guantes, delantales, cepillos y productos de limpieza'),
('Iluminacion Exterior',  'Luces y lamparas para zonas de asado al aire libre'),
('Repuestos',             'Partes y repuestos para barriles y parrillas'),
('Kits de Asado',         'Conjuntos completos de herramientas para asar'),
('Decoracion BBQ',        'Articulos decorativos tematicos de barbacoa');

INSERT INTO proveedor (empresa, contacto, telefono) VALUES
('Metalurgica del Valle',  'Jorge Mendez',    '3101234567'),
('Distribuidora El Fogon', 'Patricia Ruiz',   '3209876543'),
('Aceros Colombia SAS',    'Ricardo Vargas',  '3157894561'),
('Carbones del Pacifico',  'Claudia Herrera', '3182345678'),
('BBQ Importaciones',      'Andres Caicedo',  '3223456789'),
('Industrias Parrilleras', 'Monica Salazar',  '3164567890'),
('Ferreteria Nacional',    'Hugo Castaneda',  '3195678901'),
('Salsas La Brasa',        'Camila Ortega',   '3136789012'),
('Maderas y Carbon SA',    'Felipe Arango',   '3207890123'),
('Accesorios BBQ Pro',     'Valentina Rios',  '3118901234');

INSERT INTO producto (nombre_producto, precio, stock_actual, stock_minimo, id_categoria, id_proveedor, img_url) VALUES
('Barril Asador Clasico 200L',       350000.00,  25, 5,  1, 1, NULL),
('Barril Asador Premium 200L',       520000.00,  15, 3,  1, 1, NULL),
('Barril Asador Mini 100L',          280000.00,  30, 5,  1, 3, NULL),
('Barril Ahumador Profesional',      680000.00,  10, 2,  1, 1, NULL),
('Set Pinzas Acero Inoxidable',       45000.00,  50, 10, 2, 6, NULL),
('Guantes Termicos para Parrilla',    35000.00,  40, 8,  6, 5, NULL),
('Carbon Vegetal Premium 5kg',        18000.00, 100, 20, 3, 4, NULL),
('Briquetas de Carbon 4kg',           22000.00,  80, 15, 3, 9, NULL),
('Salsa BBQ Artesanal 500ml',         12000.00,  60, 10, 4, 8, NULL),
('Adobo Especial Volcano 350g',       15000.00,  45, 10, 4, 8, NULL),
('Mesa Lateral Plegable para Barril',  95000.00,  20, 5,  5, 7, NULL),
('Cepillo Limpiador de Parrilla',      28000.00,  35, 8,  6, 6, NULL),
('Kit Asador Completo 12 Piezas',    120000.00,  18, 4,  9, 5, NULL),
('Lampara LED para Parrilla',         42000.00,  25, 5,  7, 7, NULL),
('Rejilla de Repuesto Universal',      55000.00,  30, 5,  8, 3, NULL);

INSERT INTO cliente (nombre_cliente, dni, telefono) VALUES
('Restaurante El Fogon Caleno',  '900123456', '3201234567'),
('Asados Don Julio',             '900234567', '3152345678'),
('Carlos Andres Mejia',          '1144567890','3183456789'),
('Maria Fernanda Ospina',        '1144678901','3114567890'),
('Parrilla Express SAS',         '900345678', '3225678901'),
('Diego Alejandro Parra',        '1144789012','3166789012'),
('Eventos & Parrilladas CO',     '900456789', '3197890123'),
('Valentina Castano Ruiz',       '1144890123','3138901234'),
('BBQ House Colombia',           '900567890', '3209012345'),
('Andres Felipe Quintero',       '1144901234','3170123456'),
('Sabores al Carbon',            '900678901', '3141234567'),
('Juliana Restrepo Gomez',       '1145012345','3212345678');

INSERT INTO venta (fecha, id_cliente, id_usuario, total) VALUES
('2026-03-01 10:30:00', 1,  1, 745000.00),
('2026-03-03 14:15:00', 2,  4, 350000.00),
('2026-03-05 09:00:00', 3,  5, 63000.00),
('2026-03-08 16:45:00', 4,  4, 520000.00),
('2026-03-10 11:20:00', 5,  1, 1060000.00),
('2026-03-12 13:00:00', 6,  6, 95000.00),
('2026-03-15 10:00:00', 7,  5, 680000.00),
('2026-03-18 15:30:00', 8,  4, 57000.00),
('2026-03-20 09:45:00', 9,  7, 280000.00),
('2026-03-22 12:00:00', 10, 1, 120000.00),
('2026-03-25 14:30:00', 11, 6, 396000.00),
('2026-03-28 16:00:00', 12, 5, 45000.00);

INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1,  1,  1, 350000.00, 350000.00),
(1,  5,  2,  45000.00,  90000.00),
(1,  7,  3,  18000.00,  54000.00),
(1,  9,  1,  12000.00,  12000.00),
(2,  1,  1, 350000.00, 350000.00),
(3,  5,  1,  45000.00,  45000.00),
(3,  7,  1,  18000.00,  18000.00),
(4,  2,  1, 520000.00, 520000.00),
(5,  2,  2, 520000.00, 1040000.00),
(5, 12,  1,  28000.00,  28000.00),
(6, 11,  1,  95000.00,  95000.00),
(7,  4,  1, 680000.00, 680000.00),
(8,  9,  2,  12000.00,  24000.00),
(8, 10,  1,  15000.00,  15000.00),
(8,  7,  1,  18000.00,  18000.00),
(9,  3,  1, 280000.00, 280000.00),
(10, 13, 1, 120000.00, 120000.00),
(11,  1, 1, 350000.00, 350000.00),
(11,  6, 1,  35000.00,  35000.00),
(12,  5, 1,  45000.00,  45000.00);

INSERT INTO compra (fecha, total, id_proveedor, id_usuario) VALUES
('2026-02-15 09:00:00', 3500000.00, 1, 1),
('2026-02-20 14:30:00', 1800000.00, 4, 1),
('2026-03-01 10:00:00',  450000.00, 6, 2),
('2026-03-10 11:15:00', 2200000.00, 3, 1),
('2026-03-20 16:00:00',  960000.00, 8, 2);

INSERT INTO detalle_compra (id_compra, id_producto, cantidad, precio_unitario) VALUES
(1,  1,  10, 350000.00),
(2,  7,  100,  18000.00),
(3,  5,   10,  45000.00),
(4,  3,   10, 220000.00),
(5,  9,   80,  12000.00);





