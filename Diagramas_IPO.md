# SCE2.1 – Diagramas de Entrada, Proceso y Salida (IPO)

A continuación, se presentan los diagramas IPO (Input - Process - Output) de los métodos más críticos y representativos del sistema **SIGEV**, identificando claramente el flujo de datos.

---

## 1. Método: Autenticación de Usuario (Login)
**Clase / Método:** `UsuarioDAO.buscarPorUsername(String username)` y validación en Controlador.
**Descripción:** Verifica las credenciales de un usuario para permitir o denegar el acceso al sistema.

```mermaid
flowchart LR
    subgraph Entrada
    E1[username]
    E2[contrasena]
    end

    subgraph Proceso
    P1(Buscar usuario en BD por username)
    P2{¿Existe y está Activo?}
    P3{¿Coincide la contraseña?}
    P1 --> P2
    P2 -- Sí --> P3
    end

    subgraph Salida
    S1[Objeto Usuario cargado en sesión]
    S2[Mensaje de Error / Denegado]
    P3 -- Sí --> S1
    P2 -- No --> S2
    P3 -- No --> S2
    end
    
    Entrada --> Proceso
```

### Tabla Resumen
| Componente | Detalles |
| :--- | :--- |
| **Entradas** | `String username`, `String contrasena` |
| **Proceso** | 1. Recibir credenciales.<br>2. Ejecutar `SELECT * FROM usuario WHERE username = ? AND activo = TRUE`.<br>3. Comparar contraseña ingresada con la almacenada. |
| **Salidas** | **Éxito:** Objeto `Usuario` con sus datos y Rol.<br>**Fallo:** Retorna `null` o falso (Error de credenciales). |

---

## 2. Método: Registrar Nuevo Producto
**Clase / Método:** `ProductoDAO.insertar(Producto p)`
**Descripción:** Añade un nuevo ítem de inventario a la base de datos maestra.

```mermaid
flowchart LR
    subgraph Entrada
    E1[Datos del Producto:]
    E2(nombre, precio, stock_actual, <br>stock_minimo, id_categoria, <br>id_proveedor)
    E1 --> E2
    end

    subgraph Proceso
    P1(Validar integridad de datos)
    P2(Ejecutar INSERT en tabla 'producto')
    P3(Recuperar ID generado)
    P1 --> P2 --> P3
    end

    subgraph Salida
    S1[Confirmación Booleana: TRUE]
    S2[ID del nuevo Producto asignado]
    S3[Confirmación Booleana: FALSE]
    P3 --> S1
    P3 --> S2
    P2 -- Falla SQL --> S3
    end

    Entrada --> Proceso
```

### Tabla Resumen
| Componente | Detalles |
| :--- | :--- |
| **Entradas** | Objeto `Producto` instanciado desde el formulario (CompraPanel / ProductoPanel). |
| **Proceso** | 1. Preparar sentencia SQL `INSERT`.<br>2. Inyectar parámetros en el `PreparedStatement`.<br>3. Ejecutar actualización y solicitar `Statement.RETURN_GENERATED_KEYS`. |
| **Salidas** | `boolean true` si se registró correctamente, asignando el nuevo ID al objeto original. `boolean false` si hubo error SQL. |

---

## 3. Método: Procesar Venta y Actualizar Inventario
**Clase / Método:** `VentaDAO.insertar(Venta v)` y `ProductoDAO.actualizarStock(...)`
**Descripción:** Es el proceso más importante de salida de dinero/mercancía. Registra la transacción y descuenta el stock de manera transaccional.

```mermaid
flowchart TD
    subgraph Entrada
    E1[Objeto Venta: fecha, cliente, cajero]
    E2[Lista DetalleVenta: producto, cantidad, subtotal]
    end

    subgraph Proceso
    P1(Generar registro en tabla 'venta')
    P2(Obtener id_venta)
    P3(Iterar sobre Lista DetalleVenta)
    P4(Insertar en 'detalle_venta')
    P5(Descontar 'stock_actual' en 'producto')
    
    P1 --> P2 --> P3
    P3 --> P4
    P4 --> P5
    P5 -. Ciclo .- P3
    end

    subgraph Salida
    S1[Venta Registrada Exitosamente]
    S2[Ticket / Factura de Venta]
    P3 -- Termina lista --> S1
    S1 --> S2
    end

    Entrada --> Proceso
```

### Tabla Resumen
| Componente | Detalles |
| :--- | :--- |
| **Entradas** | Objeto principal `Venta` y una colección `List<DetalleVenta>`. |
| **Proceso** | 1. Insertar Venta maestra.<br>2. Por cada detalle: Insertar el detalle asociado a la venta.<br>3. Ejecutar `UPDATE producto SET stock_actual = stock_actual - cantidad`. |
| **Salidas** | Registro persistido, Inventario disminuido y posibilidad de generar un reporte de impresión (Ticket). |

---

## 4. Método: Alerta de Stock Bajo
**Clase / Método:** `ProductoDAO.productosStockBajo()`
**Descripción:** Consulta para identificar los productos que requieren reabastecimiento (stock actual <= stock mínimo).

```mermaid
flowchart LR
    subgraph Entrada
    E1[Solicitud de Reporte o Dashboard]
    end

    subgraph Proceso
    P1(Conexión a BD)
    P2(Ejecutar consulta de filtrado)
    P3(Comparar stock_actual <= stock_minimo)
    P4(Mapear a Lista de Productos)
    
    P1 --> P2 --> P3 --> P4
    end

    subgraph Salida
    S1[List<Producto>]
    S2[Vista de Tabla de Alertas]
    P4 --> S1 --> S2
    end

    Entrada --> Proceso
```

### Tabla Resumen
| Componente | Detalles |
| :--- | :--- |
| **Entradas** | Invocación del método sin parámetros (se basa en la regla de negocio interna). |
| **Proceso** | 1. Ejecutar `SELECT * FROM producto WHERE stock_actual <= stock_minimo`.<br>2. Recorrer el `ResultSet`.<br>3. Instanciar objetos `Producto` por cada fila coincidente. |
| **Salidas** | Retorna un `List<Producto>` conteniendo únicamente los ítems críticos. |

---

## 5. Método: Procesar Compra a Proveedor
**Clase / Método:** `CompraDAO.insertar(Compra c)` y `ProductoDAO.actualizarStockCompra(...)`
**Descripción:** Registra el abastecimiento de nueva mercancía y aumenta el inventario disponible.

```mermaid
flowchart LR
    subgraph Entrada
    E1[Objeto Compra: fecha, proveedor]
    E2[Lista DetalleCompra: producto, cant]
    end

    subgraph Proceso
    P1(Insertar en tabla 'compra')
    P2(Iterar Detalles)
    P3(Aumentar stock: <br> stock_actual = stock_actual + cant)
    
    P1 --> P2 --> P3
    end

    subgraph Salida
    S1[Stock Aumentado]
    S2[Registro Financiero de Compra]
    P3 --> S1
    P1 --> S2
    end
    
    Entrada --> Proceso
```

### Tabla Resumen
| Componente | Detalles |
| :--- | :--- |
| **Entradas** | Objeto `Compra` y `List<DetalleCompra>`. |
| **Proceso** | 1. Registrar Compra.<br>2. Iterar e insertar los detalles.<br>3. Ejecutar `UPDATE producto SET stock_actual = stock_actual + ?` por cada detalle. |
| **Salidas** | Actualización positiva del stock de inventario e historial de compras actualizado. |
---

## 6. Método: Generación de Reportes Estadísticos
**Clase / Método:** `VentaDAO.buscarPorFechas(Date inicio, Date fin)` y lógica en `ReportePanel`
**Descripción:** Filtra las transacciones del sistema por un periodo específico y calcula indicadores clave de rendimiento (KPIs).

```mermaid
flowchart LR
    subgraph Entrada
    E1[Fecha Inicio]
    E2[Fecha Fin]
    end

    subgraph Proceso
    P1(Consultar Ventas en rango)
    P2(Iterar sobre resultados)
    P3(Acumular Suma de Totales)
    P4(Contar registros)
    P5(Calcular Promedio: Total / Cantidad)
    
    P1 --> P2 --> P3 --> P4 --> P5
    end

    subgraph Salida
    S1[Total Ingresos $]
    S2[Cantidad de Transacciones]
    S3[Ticket Promedio $]
    S4[Lista de Ventas Filtradas]
    P5 --> S1
    P5 --> S2
    P5 --> S3
    P1 --> S4
    end
    
    Entrada --> Proceso
```

### Tabla Resumen
| Componente | Detalles |
| :--- | :--- |
| **Entradas** | Objetos `java.util.Date` capturados desde los filtros del Dashboard. |
| **Proceso** | 1. Ejecutar `SELECT * FROM venta WHERE fecha BETWEEN ? AND ?`.<br>2. Recorrer la lista obtenida para sumar el atributo `total`.<br>3. Aplicar lógica aritmética para promedios. |
| **Salidas** | Resumen financiero (Tarjetas de Resumen) y carga de la tabla detallada de ventas. |
