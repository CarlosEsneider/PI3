# SIGEIV-Volcano 
## Sistema de Gestion de Ventas Administrativo - Tienda Volcano Barriles

### Descripcion
Aplicacion de escritorio desarrollada en **Java** con interfaz grafica **Swing** y base de datos **MySQL**, disenada para gestionar el inventario, las ventas, los clientes y los proveedores de la tienda Volcano Barriles.

### Autores
- Carlos Esneider Espitia Paz
- Esteban Munoz Ordonez
- Leandro Caldon Fernandez
- Mauricio Alberto Ledesma

### Tecnologias
| Componente | Tecnologia |
|---|---|
| Lenguaje | Java 17+ |
| Interfaz Grafica | Java Swing |
| Base de Datos | MySQL 8.0+ |
| Conector | MySQL Connector/J 8.3.0 |

### Estructura del Proyecto
```
APP/
├── sql/
│   └── sigeiv_volcano.sql      # Script completo de la base de datos
├── src/
│   └── com/sigeiv/
│       ├── modelo/             # Clases POO (Producto, Cliente, etc.)
│       ├── dao/                # Acceso a datos (CRUD con JDBC)
│       ├── controlador/        # Logica de negocio
│       ├── vista/              # Interfaces graficas Swing
│       └── util/               # Utilidades (HashUtil)
├── lib/
│   └── mysql-connector-j-8.3.0.jar
├── bin/                        # Archivos compilados
├── package.bat                 # Script de empaquetado
└── README.md
```

### Requisitos Previos
1. **Java JDK 17+** instalado
2. **MySQL 8.0+** instalado y ejecutandose
3. Crear la base de datos ejecutando: `mysql -u root -p < sql/sigeiv_volcano.sql`

### Instalacion y Ejecucion

#### 1. Configurar la Base de Datos
```bash
mysql -u root -p < sql/sigeiv_volcano.sql
```

#### 2. Configurar la Conexion
Si tu MySQL tiene contrasena, edita el archivo:
`src/com/sigeiv/dao/ConexionDB.java`
Cambia la constante `PASSWORD` por tu contrasena de MySQL.

#### 3. Compilar y Ejecutar
Puedes usar el archivo `package.bat` para empaquetar o desde la terminal:
```bash
javac -encoding UTF-8 -d bin -cp "lib/mysql-connector-j-8.3.0.jar" src/com/sigeiv/modelo/*.java src/com/sigeiv/util/*.java src/com/sigeiv/dao/*.java src/com/sigeiv/controlador/*.java src/com/sigeiv/vista/*.java
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.sigeiv.vista.LoginFrame
```

### Credenciales de Prueba
| Rol | Usuario | Contrasena |
|---|---|---|
| Administrador | admin | admin123 |
| Vendedor | mauricio | vendedor123 |
| Consultor | juan.r | consultor123 |

### Modulos del Sistema
1. **Autenticacion**: Login con validacion SHA-256
2. **Gestion de Productos**: CRUD completo con busqueda y filtrado
3. **Gestion de Clientes**: CRUD con busqueda por nombre y DNI
4. **Gestion de Proveedores**: CRUD con busqueda
5. **Gestion de Categorias**: CRUD para clasificar productos
6. **Registro de Ventas**: Carrito de compras con actualizacion automatica del inventario
7. **Control de Inventario**: Vista del stock, alertas de stock bajo, metricas
8. **Reportes**: Ventas por periodo, top productos, estadisticas

### Roles y Permisos
| Accion | Administrador | Vendedor | Consultor |
|---|:---:|:---:|:---:|
| CRUD Productos | OK | OK | Lectura |
| CRUD Clientes | OK | OK | No |
| CRUD Proveedores | OK | No | No |
| CRUD Categorias | OK | No | No |
| Registrar Ventas | OK | OK | No |
| Ver Inventario | OK | OK | OK |
| Ver Reportes | OK | OK | OK |
