# SIGEIV-Volcano 
## Sistema de Gestion de Inventario y Ventas - Tienda Volcano Barriles
### Proyecto Integrador Cuarto Semestre (PI 411/412/413) — UNIAJC

### Descripcion
Aplicacion de escritorio orientada a objetos desarrollada en **Java** bajo el patron arquitectonico **Modelo-Vista-Controlador (MVC)**, interfaz grafica **Java Swing** y base de datos relacional **SQLite** (con script de creacion inicial portable y esquema alternativo MySQL), disenada para gestionar de manera sistematica el inventario, las compras a proveedores, los clientes y las ventas de la tienda *Volcano Barriles*.

### Autores
* **Kevin Garcia** (Desarrollo y Controladores MVC)
* **Carlos Esneider Espitia Paz**
* **Esteban Munoz Ordonez**
* **Leandro Caldon Fernandez**
* **Mauricio Alberto Ledesma**

### Tecnologias y Herramientas
| Componente | Tecnologia |
|---|---|
| Lenguaje | Java 17 LTS / 21 |
| Arquitectura | MVC (Modelo - Vista - Controlador) + DAO + Singleton |
| Interfaz Grafica | Java Swing (Tema oscuro alto contraste) |
| Base de Datos | SQLite 3 (embebido / portable sin servidor) |
| Conector JDBC | SQLite JDBC 3.41.2.1 |
| Seguridad | Hashing criptografico SHA-256 para credenciales |

### Estructura de Paquetes
```
APP/
├── sql/
│   ├── sqlite_schema.sql       # Script DDL SQLite con datos iniciales
│   └── sigeiv_volcano.sql      # Script DDL alternativo para MySQL
├── src/com/sigeiv/
│   ├── modelo/                 # Clases POJO de entidad (Producto, Cliente, etc.)
│   ├── dao/                    # Capa DAO con PreparedStatement y transacciones ACID
│   ├── controlador/            # Logica de negocio y Controladores MVC:
│   │   ├── UsuarioController.java
│   │   ├── ProductoController.java
│   │   ├── ClienteController.java
│   │   ├── ProveedorController.java
│   │   ├── CategoriaController.java
│   │   ├── VentaController.java (Transaccional)
│   │   ├── CompraController.java (Transaccional)
│   │   ├── InventarioController.java
│   │   └── ReporteController.java
│   ├── vista/                  # Paneles interactivos Swing desacoplados
│   ├── util/                   # Utilidades de seguridad (HashUtil SHA-256)
│   └── test/                   # Suite de pruebas automatizadas (AppTestSuite)
├── lib/
│   └── sqlite-jdbc-3.41.2.1.jar
├── sigeiv_volcano.db           # Base de datos relacional SQLite
├── package.bat                 # Script de empaquetado jpackage / standalone
└── README.md
```

### Compilacion y Ejecucion

#### 1. Compilar todo el codigo fuente
```bash
javac --release 17 -encoding UTF-8 -d bin -cp "lib/sqlite-jdbc-3.41.2.1.jar" src/com/sigeiv/modelo/*.java src/com/sigeiv/util/*.java src/com/sigeiv/dao/*.java src/com/sigeiv/controlador/*.java src/com/sigeiv/vista/*.java src/com/sigeiv/test/*.java
```

#### 2. Ejecutar la Suite de Pruebas Automatizadas
```bash
java -cp "bin;lib/sqlite-jdbc-3.41.2.1.jar" com.sigeiv.test.AppTestSuite
```

#### 3. Ejecutar la Aplicacion GUI
```bash
java -cp "bin;lib/sqlite-jdbc-3.41.2.1.jar" com.sigeiv.vista.LoginFrame
```

### Credenciales de Prueba por Rol
| Rol | Usuario | Contrasena | Privilegios |
|---|---|---|---|
| **Administrador** | `admin` | `admin123` | Control total del sistema y administracion de usuarios |
| **Vendedor** | `mauricio` | `vendedor123` | Gestion de clientes, ventas e inventario |
| **Consultor** | `juan.r` | `consultor123` | Solo lectura: inventario, stock bajo y reportes |

### Modulos del Sistema
1. **Autenticacion Segura:** Inicio de sesion validado contra base de datos con contrasenas encriptadas SHA-256.
2. **Gestion de Productos:** CRUD completo con validacion de negocio, filtros por categoria y busqueda interactiva.
3. **Gestion de Clientes:** CRUD con control de DNI duplicado y busqueda rapida.
4. **Gestion de Proveedores:** Mantenimiento de empresas aliadas y contactos.
5. **Gestion de Categorias:** Clasificacion modular del catalogo.
6. **Ventas y Facturacion:** Carrito de compras reactivo con descuento transaccional atomico (`conn.setAutoCommit(false)` / `commit` / `rollback`) y validacion de disponibilidad de stock.
7. **Compras y Abastecimiento:** Registro a proveedores con incremento atomico del stock.
8. **Control de Inventario:** Indicadores en tiempo real (total de productos, stock critico, valorizacion total en dinero).
9. **Reportes y Estadisticas:** KPIs por rangos de fechas (ventas totales, ingresos acumulados, ticket promedio y top productos mas vendidos).
