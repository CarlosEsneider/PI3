import sqlite3
import os

db_path = os.path.join(os.path.dirname(__file__), '..', 'sigeiv_volcano.db')
conn = sqlite3.connect(db_path)
c = conn.cursor()

# 1. Update/insert roles (5 official roles)
c.execute("UPDATE rol SET nombre_rol = 'Administrador / Gerente General' WHERE id_rol = 1")
c.execute("UPDATE rol SET nombre_rol = 'Jefe de Producción y Calidad' WHERE id_rol = 2")
c.execute("UPDATE rol SET nombre_rol = 'Encargado de Almacén y Bodega' WHERE id_rol = 3")
c.execute("INSERT OR IGNORE INTO rol (id_rol, nombre_rol) VALUES (4, 'Asesor Comercial / Ventas')")
c.execute("INSERT OR IGNORE INTO rol (id_rol, nombre_rol) VALUES (5, 'Auditor / Consultor Externo')")

# 2. Add columns to usuario if not exists
c.execute("PRAGMA table_info(usuario)")
cols = [r[1] for r in c.fetchall()]
if 'email' not in cols:
    c.execute("ALTER TABLE usuario ADD COLUMN email VARCHAR(150) DEFAULT ''")
if 'puede_autorizar' not in cols:
    c.execute("ALTER TABLE usuario ADD COLUMN puede_autorizar BOOLEAN DEFAULT 0")

# Update users
c.execute("UPDATE usuario SET email='gerencia@volcanobarriles.com', puede_autorizar=1 WHERE id_usuario=1")
c.execute("UPDATE usuario SET id_rol=2, puede_autorizar=1, email='produccion@volcanobarriles.com' WHERE username='mauricio'")
c.execute("UPDATE usuario SET id_rol=3, puede_autorizar=0, email='almacen@volcanobarriles.com' WHERE username='ana.garcia'")
c.execute("UPDATE usuario SET id_rol=4, puede_autorizar=1, email='ventas@volcanobarriles.com' WHERE username='pedro.lopez'")
c.execute("UPDATE usuario SET id_rol=5, puede_autorizar=0, email='auditoria@volcanobarriles.com' WHERE username='juan.r'")

# 3. Add columns to producto if not exists
c.execute("PRAGMA table_info(producto)")
pcols = [r[1] for r in c.fetchall()]
if 'codigo_sku' not in pcols:
    c.execute("ALTER TABLE producto ADD COLUMN codigo_sku VARCHAR(50) DEFAULT ''")
if 'dimensiones' not in pcols:
    c.execute("ALTER TABLE producto ADD COLUMN dimensiones VARCHAR(150) DEFAULT ''")
if 'costo_estandar' not in pcols:
    c.execute("ALTER TABLE producto ADD COLUMN costo_estandar DECIMAL(10,2) DEFAULT 0")
if 'activo' not in pcols:
    c.execute("ALTER TABLE producto ADD COLUMN activo BOOLEAN DEFAULT 1")

# Seed SKUs and costs for standard products
skus = {
    1: ('ASAD-CLA-200L', 'Alto 95cm, Diametro 60cm, Calibre 18 AISI 430', 210000.0),
    2: ('ASAD-PREM-200L', 'Alto 100cm, Diametro 60cm, Calibre 16 AISI 304', 310000.0),
    3: ('ASAD-MINI-100L', 'Alto 75cm, Diametro 45cm, Calibre 18 AISI 430', 160000.0),
    4: ('ASAD-AHUM-PROF', 'Alto 115cm, Diametro 65cm, Calibre 16 AISI 304', 420000.0),
    5: ('ACC-PINZ-INOX', 'Longitud 45cm, Acero Inoxidable AISI 304', 22000.0),
    6: ('ACC-GUAN-TERM', 'Talla L, Proteccion Termica 500C', 17000.0),
    7: ('COMB-CARB-5KG', 'Bulto 5kg Carbon Vegetal Roble', 9000.0),
    8: ('COMB-BRIQ-4KG', 'Bolsa 4kg Briquetas Alta Duracion', 11000.0),
    9: ('ADOB-BBQ-500ML', 'Botella 500ml Salsa BBQ Ahumada', 6000.0),
    10: ('ADOB-ESP-350G', 'Frasco 350g Especias Volcano', 7500.0),
    11: ('ACC-MESA-PLEG', 'Madera Teca y Soporte Inox 60x40cm', 52000.0),
    12: ('ACC-CEPI-LIMP', 'Cerda Acero Inoxidable Mango Madera', 14000.0),
    13: ('KIT-ASAD-12PZ', 'Estuche Maletin Aluminio con 12 Piezas', 68000.0),
    14: ('ACC-LAMP-LED', 'Pinza Magnetica Cuello Flexible LED', 23000.0),
    15: ('REP-REJI-UNIV', 'Acero AISI 304 Diametro 55cm', 30000.0)
}
for pid, (sku, dim, costo) in skus.items():
    c.execute("UPDATE producto SET codigo_sku=?, dimensiones=?, costo_estandar=?, activo=1 WHERE id_producto=?", (sku, dim, costo, pid))

# 4. Create tipo_movimiento
c.execute("""
CREATE TABLE IF NOT EXISTS tipo_movimiento (
    id_tipo_movimiento INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo_movimiento VARCHAR(20) NOT NULL UNIQUE,
    descripcion VARCHAR(150) NOT NULL,
    naturaleza_afectacion VARCHAR(20) NOT NULL,
    rol_autorizador_requerido VARCHAR(50) NOT NULL
)
""")

tipos = [
    (1, 'ENT_PROD', 'Entrada por Lote de Producción', 'ENTRADA', 'Jefe de Producción'),
    (2, 'SAL_VENT', 'Salida por Despacho de Venta', 'SALIDA', 'Asesor Comercial'),
    (3, 'DEV_CLIE', 'Devolución de Cliente con Dictamen', 'ENTRADA', 'Jefe de Producción'),
    (4, 'AJU_FISI', 'Ajuste por Inventario Físico', 'AJUSTE', 'Administrador'),
    (5, 'BAJ_MERM', 'Baja por Merma Técnica / Avería', 'SALIDA', 'Administrador')
]
for t in tipos:
    c.execute("INSERT OR IGNORE INTO tipo_movimiento (id_tipo_movimiento, codigo_movimiento, descripcion, naturaleza_afectacion, rol_autorizador_requerido) VALUES (?, ?, ?, ?, ?)", t)

# 5. Create movimiento_inventario
c.execute("""
CREATE TABLE IF NOT EXISTS movimiento_inventario (
    id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT,
    numero_documento_soporte VARCHAR(50) NOT NULL UNIQUE,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_tipo_movimiento INT NOT NULL,
    id_usuario_creador INT NOT NULL,
    id_usuario_autorizador INT DEFAULT NULL,
    estado_autorizacion VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_autorizacion DATETIME DEFAULT NULL,
    observaciones TEXT,
    CONSTRAINT fk_mov_tipo FOREIGN KEY (id_tipo_movimiento) REFERENCES tipo_movimiento(id_tipo_movimiento),
    CONSTRAINT fk_mov_creador FOREIGN KEY (id_usuario_creador) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_mov_autorizador FOREIGN KEY (id_usuario_autorizador) REFERENCES usuario(id_usuario)
)
""")

# 6. Create detalle_movimiento
c.execute("""
CREATE TABLE IF NOT EXISTS detalle_movimiento (
    id_detalle_movimiento INTEGER PRIMARY KEY AUTOINCREMENT,
    id_movimiento INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    costo_unitario DECIMAL(10,2) NOT NULL DEFAULT 0,
    saldo_resultante INT DEFAULT 0,
    CONSTRAINT fk_detmov_mov FOREIGN KEY (id_movimiento) REFERENCES movimiento_inventario(id_movimiento) ON DELETE CASCADE,
    CONSTRAINT fk_detmov_prod FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
)
""")

# 7. Create kardex
c.execute("""
CREATE TABLE IF NOT EXISTS kardex (
    id_kardex INTEGER PRIMARY KEY AUTOINCREMENT,
    id_producto INT NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_movimiento INT NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    numero_soporte VARCHAR(50) NOT NULL,
    cantidad_entrada INT NOT NULL DEFAULT 0,
    cantidad_salida INT NOT NULL DEFAULT 0,
    saldo_stock INT NOT NULL,
    costo_unitario DECIMAL(10,2) NOT NULL DEFAULT 0,
    costo_total_saldo DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_kardex_prod FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    CONSTRAINT fk_kardex_mov FOREIGN KEY (id_movimiento) REFERENCES movimiento_inventario(id_movimiento)
)
""")

# 8. Create auditoria
c.execute("""
CREATE TABLE IF NOT EXISTS auditoria (
    id_auditoria INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,
    accion VARCHAR(50) NOT NULL,
    tabla_afectada VARCHAR(50) NOT NULL,
    id_registro_afectado INT NOT NULL,
    detalle TEXT,
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
)
""")

# Seed sample pending movements if empty
c.execute("SELECT COUNT(*) FROM movimiento_inventario WHERE estado_autorizacion='PENDIENTE'")
count_pending = c.fetchone()[0]
if count_pending == 0:
    # 1. Lote produccion
    c.execute("INSERT INTO movimiento_inventario (numero_documento_soporte, fecha_hora, id_tipo_movimiento, id_usuario_creador, estado_autorizacion, observaciones) VALUES ('OP-2026-088', '2026-11-20 10:15:00', 1, 3, 'PENDIENTE', 'Entrega de lote de fabricacion finalizado en taller metalmecanico. Soldadura TIG y pulido AISI 430 aprobados por Almacen.')")
    m1 = c.lastrowid
    c.execute("INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad, costo_unitario, saldo_resultante) VALUES (?, 1, 10, 210000.0, 35)", (m1,))
    c.execute("INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad, costo_unitario, saldo_resultante) VALUES (?, 3, 5, 160000.0, 35)", (m1,))

    # 2. Salida venta despacho
    c.execute("INSERT INTO movimiento_inventario (numero_documento_soporte, fecha_hora, id_tipo_movimiento, id_usuario_creador, estado_autorizacion, observaciones) VALUES ('REM-2026-0142', '2026-11-20 11:30:00', 2, 3, 'PENDIENTE', 'Remision de despacho por venta corporativa Restaurante Asados Don Julio. Factura F-554.')")
    m2 = c.lastrowid
    c.execute("INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad, costo_unitario, saldo_resultante) VALUES (?, 2, 2, 310000.0, 13)", (m2,))
    c.execute("INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad, costo_unitario, saldo_resultante) VALUES (?, 5, 4, 22000.0, 46)", (m2,))

    # 3. Ajuste fisico
    c.execute("INSERT INTO movimiento_inventario (numero_documento_soporte, fecha_hora, id_tipo_movimiento, id_usuario_creador, estado_autorizacion, observaciones) VALUES ('AJU-2026-0005', '2026-11-20 14:00:00', 4, 3, 'PENDIENTE', 'Acta de conteo fisico mensual en bodega producto terminado. Discrepancia detectada en modelo Ahumador.')")
    m3 = c.lastrowid
    c.execute("INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad, costo_unitario, saldo_resultante) VALUES (?, 4, 1, 420000.0, 9)", (m3,))

    # 4. Baja por merma tecnica
    c.execute("INSERT INTO movimiento_inventario (numero_documento_soporte, fecha_hora, id_tipo_movimiento, id_usuario_creador, estado_autorizacion, observaciones) VALUES ('BAJ-2026-0012', '2026-11-20 15:45:00', 5, 2, 'PENDIENTE', 'Acta de baja por golpe severo y fisura irreparable durante flete de despacho nacional. Requiere aprobacion de Gerencia.')")
    m4 = c.lastrowid
    c.execute("INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad, costo_unitario, saldo_resultante) VALUES (?, 1, 1, 210000.0, 24)", (m4,))

    # 5. Devolucion de cliente
    c.execute("INSERT INTO movimiento_inventario (numero_documento_soporte, fecha_hora, id_tipo_movimiento, id_usuario_creador, estado_autorizacion, observaciones) VALUES ('DEV-2026-0021', '2026-11-20 16:20:00', 3, 4, 'PENDIENTE', 'Devolucion por garantia de acabado termico en tapa superior. Dictamen tecnico solicita reingreso a inventario tras revision.')")
    m5 = c.lastrowid
    c.execute("INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad, costo_unitario, saldo_resultante) VALUES (?, 2, 1, 310000.0, 16)", (m5,))

conn.commit()
conn.close()
print("Migration and seed completed successfully!")
