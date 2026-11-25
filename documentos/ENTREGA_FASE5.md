# INSTITUCIÓN UNIVERSITARIA ANTONIO JOSÉ CAMACHO (UNIAJC)
## FACULTAD DE INGENIERÍAS — INGENIERÍA DE SISTEMAS
### PROYECTO INTEGRADOR CUARTO SEMESTRE (PI 411/412/413)
#### SIGEIV-VOLCANO: SISTEMA INTEGRADO DE GESTIÓN DE INVENTARIOS Y VENTAS
---
**DOCUMENTO FORMAL DE ENTREGA: FASE 5**  
**MÓDULO: VISOR INTERACTIVO DE KARDEX Y SEMAFORIZACIÓN VISUAL TRICOLOR (RF-10, RF-11)**

* **Autor / Responsable:** Kevin García
* **Correo Institucional:** kevingarcia@estudiante.uniajc.edu.co
* **Organización Beneficiaria:** Volcano Barriles S.A.S.
* **Fecha de Entrega:** Noviembre de 2026
* **Versión del Entregable:** 1.0 — Rama `kevin`

---

## 1. INTRODUCCIÓN Y ALCANCE DE LA FASE 5

El presente documento formaliza la entrega de la **Fase 5** del proyecto integrador **SIGEIV-Volcano**, desarrollada e implementada en la rama `kevin` bajo la arquitectura Modelo-Vista-Controlador (MVC), los estándares académicos de la cátedra de Ingeniería de Software de la UNIAJC y las normativas contables colombianas sobre administración de inventarios permanentes.

La Fase 5 comprende dos pilares fundamentales para la operatividad y auditoría de **Volcano Barriles S.A.S.**:
1. **Visor Interactivo de Kardex y Trazabilidad (RF-10):** Construcción del libro mayor inmutable que consolida cronológicamente cada uno de los movimientos autorizados (entradas de producción, salidas comerciales, devoluciones, bajas y ajustes físicos), detallando cantidades, saldos, costos unitarios, valorizaciones y segregación de funciones (usuario registrador y usuario autorizador).
2. **Semaforización Visual Tricolor del Inventario (RF-11):** Implementación de un modelo analítico preventivo en el catálogo de productos que clasifica el stock en tiempo real según umbrales matemáticos rigurosos (**Verde / Óptimo**, **Amarillo / Preventivo**, **Rojo / Crítico**), alertando de inmediato al taller de fabricación ante riesgos de desabastecimiento.

---

## 2. ESPECIFICACIÓN FORMAL DE REQUERIMIENTOS Y CASOS DE USO (ESTÁNDAR UNIAJC)

---

### 2.1 Requerimiento Funcional RF-10: Visor de Kardex y Trazabilidad

| Campo | Especificación |
| :--- | :--- |
| **Nombre del Requerimiento** | RF10. Visor interactivo y trazabilidad cronológica del Kardex inmutable |
| **Funcionalidad** | El sistema debe permitir consultar y auditar en tiempo real el libro mayor de Kardex por producto o de manera consolidada, visualizando cronológicamente cada asiento generado por documentos soporte autorizados con sus cantidades, saldos, costos y actores responsables. |
| <div align="center">**ESPECIFICACIÓN**</div> | |
| **Resumen** | • El usuario con rol facultado (Administrador, Jefe de Producción, Almacén o Auditor) selecciona el módulo "Kardex" desde la barra lateral.<br>• El sistema consulta la base de datos relacional y presenta las tarjetas métricas con el acumulado de Entradas (+), Salidas (-), Saldo en Stock, Valorización y Rotación.<br>• Si el usuario elige un asador del menú desplegable o digita su código SKU en el buscador rápido, el sistema filtra y ordena cronológicamente todos los asientos inmutables asociados.<br>• Si el producto no registra movimientos o el SKU no existe, el sistema presenta la grilla vacía e informa que no existen asientos contables asentados. |
| **Entrada** | Identificador de producto (`id_producto`), código SKU o texto de búsqueda en tiempo real. |
| **Salida** | • Grilla con 11 columnas oficiales: ID Kardex, Fecha/Hora, Tipo Evento, N.° Soporte, Registrado Por, Autorizado Por, Entrada (+), Salida (-), Saldo Stock, Costo Unitario, Saldo Valorizado.<br>• Tarjetas métricas superiores con totales acumulados.<br>• Mensaje de error/excepción ante fallos de conexión: *"Error al cargar los registros del libro mayor de Kardex"*. |

---

#### Caso de Uso CU10: Consultar Libro Mayor de Kardex por Producto Terminado

| CASO DE USO: CU10 — CONSULTAR LIBRO MAYOR DE KARDEX POR PRODUCTO | |
| :--- | :--- |
| **Nombre del caso** | Consultar libro mayor de Kardex por producto terminado y trazabilidad contable |
| **Actores** | Administrador, Jefe de Producción, Encargado de Almacén, Auditor (Actores Primarios) |
| **Descripción** | Permite a los roles administrativos y de control auditar el historial de movimientos de inventario que han impactado las existencias de un asador de barril, verificando la inmutabilidad de los saldos, costos y responsables de radicación y autorización. |
| **Precondiciones** | El usuario debe haber iniciado sesión con un rol autorizado (no asesor comercial) y existir conexión activa a la base de datos `sigeiv_volcano.db`. |
| **Postcondiciones** | El sistema visualiza la cronología completa de asientos contables del Kardex sin alterar el estado inmutable de los datos. |
| **Reglas de negocio** | • **RN-KARD-01 (Inmutabilidad Contable):** Los asientos en Kardex son estrictamente de solo lectura y no permiten edición ni eliminación física (RNF-04).<br>• **RN-KARD-02 (Cruce de Segregación):** Cada asiento debe exhibir el usuario creador del documento soporte y el usuario que otorgó la autorización formal.<br>• **RN-KARD-03 (Consistencia de Saldos):** El saldo stock de la última fila del Kardex debe ser idéntico al campo `stock_actual` de la tabla `producto`. |
| **Referencias** | RF10 — Visor interactivo y trazabilidad cronológica del Kardex inmutable |
| <div align="center">**ESTRUCTURA DE FLUJOS DE EVENTOS**</div> | |
| **Flujo principal (Escenario exitoso)** | 1. El actor hace clic en la opción "Kardex" del menú lateral del sistema.<br>2. El sistema despliega el panel `KardexPanel` con las métricas consolidadas del catálogo.<br>3. El actor selecciona un producto específico en el selector desplegable (ej. *"ASAD-CLA-200L"*).<br>4. El sistema invoca `KardexController.listarKardexPorProducto(idProducto)`.<br>5. El sistema puebla la tabla con los asientos cronológicos, aplicando colores diferenciales a entradas (verde) y salidas (rojo).<br>6. El sistema recalcula y muestra las tarjetas de Total Entradas, Total Salidas, Saldo Actual, Valorización y Rotación.<br>7. El actor inspecciona los números de soporte y los usuarios responsables. |
| **Flujos alternativos** | • **3a. Filtrado rápido por código SKU o nombre:**<br>&nbsp;&nbsp;3a.1 El actor escribe texto en el campo *"Buscar SKU / Nombre"*.<br>&nbsp;&nbsp;3a.2 El sistema filtra en tiempo real la tabla mediante `KardexController.buscarPorSkuONombre()` actualizando las métricas. |
| **Flujos de excepción** | • **4a. Producto sin asientos en Kardex:**<br>&nbsp;&nbsp;4a.1 El sistema detecta que el producto aún no tiene movimientos autorizados.<br>&nbsp;&nbsp;4a.2 El sistema muestra la grilla vacía con contadores en cero y saldo según catálogo inicial.<br>• **4b. Pérdida de conexión con la base de datos:**<br>&nbsp;&nbsp;4b.1 El sistema captura la excepción `SQLException`.<br>&nbsp;&nbsp;4b.2 El sistema presenta diálogo modal: *"Error de comunicación con la base de datos al consultar el Kardex"*. |

---

### 2.2 Requerimiento Funcional RF-11: Semaforización Visual de Inventario

| Campo | Especificación |
| :--- | :--- |
| **Nombre del Requerimiento** | RF11. Control visual de existencias mediante semaforización tricolor de inventario |
| **Funcionalidad** | El sistema debe clasificar y colorear automáticamente cada producto del catálogo según una escala matemática tricolor basada en su stock actual respecto a su stock mínimo, alertando de forma inmediata sobre productos críticos o en riesgo de desabastecimiento. |
| <div align="center">**ESPECIFICACIÓN**</div> | |
| **Resumen** | • El usuario ingresa a la vista "Inventario" desde la navegación principal.<br>• El sistema evalúa para cada producto la regla: $\text{Stock} \le \text{Mínimo}$ (Rojo/Crítico), $\text{Mínimo} < \text{Stock} \le \text{Mínimo} \times 1.5$ (Amarillo/Preventivo) y $\text{Stock} > \text{Mínimo} \times 1.5$ (Verde/Óptimo).<br>• El sistema renderiza badges visuales tricolores en la columna "Semáforo (RF-11)" y actualiza las tarjetas contadoras de cada estado en el panel superior.<br>• Si el usuario activa el filtro por estado, el sistema lista únicamente las referencias que cumplen dicha condición operativa. |
| **Entrada** | Selección de filtro en el desplegable (`TODOS`, `OPTIMO`, `PREVENTIVO`, `CRITICO`). |
| **Salida** | • Tabla de inventario estilizada con insignias semafóricas (🟢 ÓPTIMO, 🟡 PREVENTIVO, 🔴 CRÍTICO).<br>• Tarjetas de conteo cuantitativo de referencias en cada estado.<br>• Alerta visual para el taller de producción ante referencias críticas. |

---

#### Caso de Uso CU11: Visualizar Catálogo con Semaforización Tricolor Preventiva

| CASO DE USO: CU11 — VISUALIZAR INVENTARIO CON SEMAFORIZACIÓN TRICOLOR | |
| :--- | :--- |
| **Nombre del caso** | Visualizar y filtrar catálogo de inventario con semaforización tricolor preventiva |
| **Actores** | Administrador, Jefe de Producción, Encargado de Almacén, Auditor (Actores Primarios) |
| **Descripción** | Permite evaluar la salud operativa de las existencias de Volcano Barriles para tomar decisiones preventivas de fabricación antes de que ocurra quiebre de stock. |
| **Precondiciones** | Existencia de productos configurados en el catálogo con `stock_actual` y `stock_minimo` definidos. |
| **Postcondiciones** | La vista refleja el estado actual de cada producto clasificado según la escala tricolor. |
| **Reglas de negocio** | • **RN-SEM-01 (Umbral Óptimo):** $\text{Stock Actual} > 1.5 \times \text{Stock Mínimo} \implies \text{Verde (Óptimo)}$.<br>• **RN-SEM-02 (Umbral Preventivo):** $\text{Stock Mínimo} < \text{Stock Actual} \le 1.5 \times \text{Stock Mínimo} \implies \text{Amarillo (Preventivo)}$.<br>• **RN-SEM-03 (Umbral Crítico):** $\text{Stock Actual} \le \text{Stock Mínimo} \lor \text{Stock Actual} = 0 \implies \text{Rojo (Crítico/Alerta Taller)}$. |
| **Referencias** | RF11 — Control visual de existencias mediante semaforización tricolor de inventario |
| <div align="center">**ESTRUCTURA DE FLUJOS DE EVENTOS**</div> | |
| **Flujo principal (Escenario exitoso)** | 1. El actor hace clic en "Inventario" en la barra lateral.<br>2. El sistema despliega el panel `InventarioPanel`.<br>3. El sistema evalúa `InventarioController.determinarEstadoSemaforo()` para cada registro.<br>4. El sistema presenta las 5 tarjetas de resumen: Catálogo Total, 🟢 Óptimo, 🟡 Preventivo, 🔴 Crítico / Taller y Valorización Total.<br>5. El sistema renderiza la tabla coloreando los estados en verde, amarillo y rojo.<br>6. El actor identifica las referencias críticas y coordina la orden de producción correspondiente. |
| **Flujos alternativos** | • **3a. Filtrar únicamente productos en estado Crítico:**<br>&nbsp;&nbsp;3a.1 El actor selecciona *"🔴 Solo Críticos / Agotados"* en el combo superior.<br>&nbsp;&nbsp;3a.2 El sistema invoca `InventarioController.filtrarPorSemaforo("CRITICO")` y muestra únicamente las filas en rojo. |
| **Flujos de excepción** | • **4a. Catálogo de productos vacío:**<br>&nbsp;&nbsp;4a.1 El sistema detecta cero registros en la tabla `producto`.<br>&nbsp;&nbsp;4a.2 El sistema muestra los contadores en 0 y la grilla vacía sin generar excepciones. |

---

## 3. ESPECIFICACIÓN DE REQUERIMIENTOS NO FUNCIONALES (RNF)

| Campo | Especificación |
| :--- | :--- |
| **Nombre del Requerimiento** | RNF04. Trazabilidad inmutable de asientos contables y no repudio |
| **Funcionalidad** | El sistema debe garantizar que los asientos del libro Kardex sean estrictamente inmutables (append-only), impidiendo cualquier modificación o eliminación física por parte de usuarios o interfaces. |
| <div align="center">**ESPECIFICACIÓN**</div> | |
| **Resumen** | • Al registrarse una transacción formal de inventario autorizada, el sistema inserta el asiento en la tabla `kardex`.<br>• La clase `KardexDAO` no provee métodos de actualización (`UPDATE`) ni borrado (`DELETE`).<br>• La base de datos garantiza llaves foráneas hacia `producto` y `movimiento_inventario` con restricción `ON DELETE RESTRICT`.<br>• Ante cualquier intento no autorizado de supresión de datos, el motor relacional bloquea la operación preservando la evidencia contable. |
| **Entrada** | Evento de inserción transaccional desde el módulo de autorizaciones. |
| **Salida** | • Asiento inmutable con sello de tiempo `CURRENT_TIMESTAMP` y correlativo único.<br>• Excepción `SQLException` ante intentos de eliminación foránea. |

---

| Campo | Especificación |
| :--- | :--- |
| **Nombre del Requerimiento** | RNF05. Rendimiento y tiempo de respuesta en visualización analítica |
| **Funcionalidad** | El sistema debe resolver las consultas de trazabilidad de Kardex y semaforización de inventario en tiempos menores a 200 milisegundos para catálogos de hasta 10.000 asientos. |
| <div align="center">**ESPECIFICACIÓN**</div> | |
| **Resumen** | • Al seleccionar un producto en el combo o digitar un filtro en la interfaz Swing, el sistema ejecuta la consulta SQL.<br>• El motor SQLite utiliza los índices `idx_kardex_prod` e `idx_kardex_fecha` definidos en la Fase 1.<br>• El controlador procesa la lista y entrega el modelo de tabla a la vista en memoria en menos de 200 ms.<br>• Si la base de datos se encuentra bajo concurrencia, el pool de conexiones Singleton previene bloqueos de lectura. |
| **Entrada** | Filtros de interfaz de usuario en Swing (ComboBox o DocumentListener). |
| **Salida** | • Renderizado instantáneo de la grilla sin congelamiento del hilo de eventos (`EDT`).<br>• Tiempos de respuesta sub-segundo garantizados. |

---

## 4. MATRIZ DE CONTROL DE ACCESO (RBAC) — FASE 5

| Módulo / Función | Administrador (Rol 1) | Jefe Producción (Rol 2) | Almacén (Rol 3) | Asesor Comercial (Rol 4) | Auditor (Rol 5) |
|---|:---:|:---:|:---:|:---:|:---:|
| **Ver Inventario General** | ✅ Total | ✅ Total | ✅ Total | ❌ Denegado | ✅ Lectura |
| **Ver Semáforo Tricolor (RF-11)** | ✅ Total | ✅ Total | ✅ Total | ❌ Denegado | ✅ Lectura |
| **Filtrar Críticos / Preventivos** | ✅ Total | ✅ Total | ✅ Total | ❌ Denegado | ✅ Lectura |
| **Ver Botón "Kardex" en Sidebar** | ✅ Visible | ✅ Visible | ✅ Visible | ❌ Oculto | ✅ Visible |
| **Consultar Historial Kardex (RF-10)** | ✅ Total | ✅ Total | ✅ Total | ❌ Denegado | ✅ Lectura |
| **Ver Costos Unitarios y Valorización** | ✅ Total | ✅ Total | ✅ Total | ❌ Denegado | ✅ Auditoría |
| **Exportar / Auditar Asientos** | ✅ Total | ✅ Lectura | ✅ Lectura | ❌ Denegado | ✅ Total |

---

## 5. ARQUITECTURA TÉCNICA E IMPLEMENTACIÓN MVC

```
                 CAPA VISTA (Swing UI)
    +-----------------------------------------------+
    |  KardexPanel.java      InventarioPanel.java   |
    |  - JComboBox producto  - Semáforo tricolor    |
    |  - Tarjetas métricas   - Renderer colores     |
    |  - JTable cronológica  - Filtro por estado    |
    +-----------------------+-----------------------+
                            | Consume controladores
                            v
               CAPA CONTROLADOR (Business Logic)
    +-----------------------------------------------+
    |  KardexController.java    InventarioController|
    |  - listarKardexPorProd()  - determinarSemaforo|
    |  - calcularValorizacion() - obtenerTotalOptimo|
    |  - calcularRotacion()     - filtrarPorSemaforo|
    +-----------------------+-----------------------+
                            | Invoca DAOs
                            v
               CAPA DAO (Acceso a Datos - JDBC)
    +-----------------------------------------------+
    |  KardexDAO.java           ProductoDAO.java    |
    |  - BASE_SELECT con cruce  - listarTodos()     |
    |  - JOIN producto, usuario - buscarPorId()     |
    |  - Consultas inmutables   - productosBajos()  |
    +-----------------------+-----------------------+
                            | SQL / JDBC
                            v
               BASE DE DATOS RELACIONAL (SQLite)
    +-----------------------------------------------+
    |  kardex               movimiento_inventario   |
    |  producto             usuario                 |
    +-----------------------------------------------+
```

---

## 6. EVIDENCIA DE PRUEBAS AUTOMATIZADAS (SUITE COMPLETA)

La suite de pruebas automatizadas `TestVerificacionFase5.java` fue ejecutada de forma satisfactoria con un resultado de **8 pruebas superadas de 8 (100% de éxito)**:

```text
==================================================================
  SIGEIV-Volcano | Suite de Pruebas de Verificacion - Fase 5
  Libro Mayor de Kardex (RF-10) y Semaforizacion Visual (RF-11)
==================================================================
[PASS] Prueba 1: Conexion a BD exitosa. Asientos registrados en Kardex: 3
[PASS] Prueba 2: Consulta de Kardex por producto ejecutada correctamente (ID=10).
[PASS] Prueba 3: Filtrado y busqueda textual de asientos de Kardex verificado.
[PASS] Prueba 4: Metricas contables calculadas: Ent=18, Sal=0, Saldo=43, Val=$9030000,00, Rot=0,00x
[PASS] Prueba 5: Regla matematica oficial de semaforizacion tricolor validada al 100% (Verde/Amarillo/Rojo).
[PASS] Prueba 6: Contadores de semaforo integros (Total=15 = Opt:15 + Prev:0 + Crit:0).
[PASS] Prueba 7: Esquema relacional inmutable de Kardex verificado (RNF-04).
[PASS] Prueba 8: Cruce relacional de auditoria en Kardex (Registrador / Autorizador) verificado sin nulos.
==================================================================
  RESULTADO: 8 pruebas exitosas, 0 fallos.
  ESTADO: [PASS] FASE 5 VERIFICADA AL 100% CON EXITO
==================================================================
```

Adicionalmente, se ejecutaron las suites de regresión global sin registrar ningún fallo:
* **`AppTestSuite` (Regresión General):** 16/16 pruebas aprobadas (100.0%).
* **`TestVerificacionS4` (Regresión Fase 3/4):** 3/3 pruebas ejecutadas aprobadas sin fallos.

---

## 7. REFERENCIAS BIBLIOGRÁFICAS (NORMAS APA 7.ª EDICIÓN)

* Ballou, R. H. (2004). *Logística: Administración de la cadena de suministro* (5.ª ed.). Pearson Educación.
* Chase, R. B., Jacobs, F. R., & Aquilano, N. J. (2018). *Administración de operaciones: Producción y cadena de suministros* (15.ª ed.). McGraw-Hill Interamericana.
* Date, C. J. (2004). *Introducción a los sistemas de bases de datos* (8.ª ed.). Pearson Educación.
* Deitel, P., & Deitel, H. (2017). *Java: Cómo programar* (10.ª ed.). Pearson Educación.
* Elmasri, R., & Navathe, S. B. (2016). *Fundamentals of database systems* (7.ª ed.). Pearson.
* Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design patterns: Elements of reusable object-oriented software*. Addison-Wesley.
* Pressman, R. S., & Maxim, B. R. (2021). *Ingeniería del software: Un enfoque práctico* (9.ª ed.). McGraw-Hill.
* Sommerville, I. (2019). *Ingeniería de software* (10.ª ed.). Pearson Educación.
