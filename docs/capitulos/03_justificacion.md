# CAPÍTULO 3. JUSTIFICACIÓN DEL PROYECTO

La materialización del sistema **SIGEIV-Volcano** se fundamenta en cuatro ejes estratégicos que demuestran la pertinencia, necesidad e impacto positivo del proyecto tanto a nivel tecnológico como empresarial y académico.

## 3.1. Beneficios Esperados
La automatización de las operaciones en Volcano Barriles a través de un aplicativo de escritorio dedicado reportará beneficios tangibles e inmediatos:
*   **Exactitud en el control de existencias:** El registro transaccional automático garantizará la sincronización entre el inventario físico y lógico, eliminando el margen de error humano en conteos y evitando la venta de productos sin existencias reales.
*   **Notificación proactiva de stock crítico:** Mediante consultas automáticas que evalúan si $\text{stock\_actual} \le \text{stock\_minimo}$, el sistema emite alertas visuales inmediatas para los productos en riesgo de agotamiento, permitiendo planificar las órdenes de compra con suficiente holgura.
*   **Optimización del tiempo transaccional:** La liquidación automatizada de ventas con cálculo instantáneo de subtotales y totales reducirá el tiempo de atención por cliente de 10 minutos a menos de 90 segundos.
*   **Trazabilidad histórica integral:** Disponibilidad centralizada de registros de compras a proveedores y ventas a clientes, facilitando auditorías de caja, consultas por rangos de fecha y cálculo de ingresos acumulados.

## 3.2. Impacto en el Negocio
El despliegue de esta solución informática transformará la dinámica comercial de Volcano Barriles:
*   **Competitividad comercial:** Al contar con tiempos de respuesta ágiles y certeza en la disponibilidad de sus productos, la empresa fortalecerá su posicionamiento frente a competidores del sector de parrilladas y artículos BBQ en el suroccidente colombiano.
*   **Fidelización de clientes:** Los clientes comerciales (restaurantes campestres y negocios parrilleros) recibirán cotizaciones y despachos inmediatos, generando relaciones de confianza a largo plazo y reduciendo cancelaciones de órdenes.
*   **Capacidad de escalabilidad:** El contar con una base de datos estandarizada y un modelo en capas permitirá a la empresa abrir nuevos canales de distribución o puntos de venta físicos a futuro sin requerir una reestructuración desde cero de sus sistemas.

## 3.3. Viabilidad y Sostenibilidad

### Viabilidad Técnica
El proyecto es plenamente viable puesto que no demanda infraestructura costosa ni equipos de cómputo de alta gama. Se desarrolla sobre la plataforma **Java 17 LTS**, una de las tecnologías más estables del mercado, garantizando independencia de plataforma y compatibilidad con los sistemas operativos existentes en el taller (Windows 10/11). La base de datos relacional (implementada con arquitectura JDBC desacoplada, operable en SQLite embebido o MySQL) ofrece alta fiabilidad transaccional sin consumo excesivo de memoria RAM ni exigencia de servidores dedicados de alto coste.

### Viabilidad Económica
El análisis de costo-beneficio resulta altamente favorable para Volcano Barriles. Al emplear tecnologías de código abierto (*open source*) sin licenciamiento comercial privativo (Java SE, SQLite, JDBC, NetBeans/VSCode), el costo de licenciamiento de software es de **\$0 COP**. La inversión requerida se concentra exclusivamente en el esfuerzo de desarrollo y capacitación del personal, la cual queda amortizada dentro del proyecto formativo institucional. La recuperación de los costos asociados a pérdidas de inventario (\$1.800.000 a \$2.500.000 COP mensuales) genera un retorno de inversión (ROI) acelerado a corto plazo.

### Viabilidad Organizativa y Operativa
La interfaz gráfica de usuario ha sido concebida bajo estrictos criterios de ergonomía visual y accesibilidad, utilizando una estética profesional de alto contraste (tema oscuro con acentos en naranja volcánico) con controles gráficos estándar (`JComboBox`, `JTable`, botones identificados por color y atajos de teclado como la tecla Enter para autenticación). El personal operativo actual posee competencias básicas de ofimática, por lo que la curva de capacitación para adoptar el sistema no supera las 4 horas por usuario.

## 3.4. Apoyo y Aceptación Institucional
El proyecto cuenta con el respaldo irrestricto de la administración de Volcano Barriles, quienes facilitaron el acceso a las listas de precios, fichas de catálogo de productos, perfiles de clientes y datos de proveedores para la construcción de los datos de prueba. Por su parte, en el plano académico de la **Institución Universitaria Antonio José Camacho (UNIAJC)**, este proyecto integrador consolida las directrices curriculares del cuarto semestre de Tecnología en Sistemas de Información, integrando de manera sinérgica los conocimientos de Ingeniería de Software 1, Programación 2 y Bases de Datos 2 bajo estándares de calidad profesional.
