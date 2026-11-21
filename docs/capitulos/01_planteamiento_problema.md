# CAPÍTULO 1. PLANTEAMIENTO DEL PROBLEMA

## 1.1. Contextualización y Comprensión del Negocio
La empresa **Volcano Barriles** es una microempresa manufacturera y comercializadora radicada en la ciudad de Santiago de Cali (Valle del Cauca, Colombia), dedicada al diseño, fabricación artesanal e industrial, ensamble y distribución de barriles asadores y ahumadores de acero al carbón e inoxidable, así como de un variado ecosistema de accesorios parrilleros (pinzas, guantes térmicos, rejillas de repuesto, lámparas LED), combustibles (carbón vegetal gourmet, briquetas) y salsas o adobos especializados para barbacoa.

En el marco de la recolección de información inicial y el diagnóstico organizacional, se realizaron entrevistas directas y sesiones de observación presencial con los administradores y operarios del taller comercial. A través de este trabajo de campo, se constató que la organización gestiona sus actividades operativas cotidianas de forma empírica y fragmentada, empleando cuadernos manuscritos y hojas de cálculo no estandarizadas en computadores locales sin sincronización ni respaldo centralizado. 

Este modelo manual de operación genera una desconexión crítica entre el área de compras (abastecimiento de láminas de acero, herrajes y carbón a proveedores metalúrgicos y distribuidores) y el área comercial de ventas (mostrador físico, despachos a restaurantes y ventas directas). Las principales dificultades identificadas comprenden:
1. **Desactualización del stock físico en tiempo real:** Cuando se efectúa una venta en mostrador o un pedido mayorista para un restaurante campestre, la deducción de unidades no se registra de inmediato en el inventario maestro, originando compromisos de entrega de productos inexistentes (*quiebres de stock*).
2. **Ausencia de alertas de reaprovisionamiento:** No se calculan puntos de reorden ni se notifican existencias mínimas de repuestos o insumos críticos (como rejillas o carbón), lo que paraliza la capacidad de respuesta ante clientes y demora las compras a proveedores hasta que el material se encuentra completamente agotado.
3. **Pérdida de trazabilidad de compras y cuentas por pagar:** Las compras realizadas a proveedores locales no cuentan con una correlación sistematizada frente al incremento real de stock ni frente a los precios unitarios fluctuantes del acero.
4. **Cálculo manual propenso a errores en ventas:** El cálculo de totales, subtotales y aplicación de precios se elabora manualmente por el dependiente, propiciando discrepancias de caja al final de la jornada.

## 1.2. Datos y Cifras Cuantitativas del Negocio
Para dimensionar con rigor el impacto económico y operacional de las problemáticas expuestas, se consolidaron los siguientes indicadores recopilados durante la fase diagnóstica:

*   **Puntos de operación y equipo humano:** 1 sede central que combina taller de ensamble y sala de ventas en Cali, con una planta operativa de 5 colaboradores (1 administrador general, 2 operarios de taller y 2 encargados de atención y ventas).
*   **Volumen de inventario administrado:** Catálogo activo superior a 15 referencias fijas de productos agrupadas en 10 categorías (barriles de 100L, 200L, profesionales, accesorios, carbón, salsas y repuestos), con un promedio de 400 unidades físicas rotando mensualmente.
*   **Red de proveedores y clientes:** Cartera de 10 proveedores metalúrgicos y de suministros clave, y más de 150 clientes recurrentes registrados de manera dispersa entre restaurantes, eventos y compradores particulares.
*   **Volumen transaccional diario:** Promedio de 10 a 15 operaciones de venta diarias, con valores de transacción oscilantes entre \$12.000 COP (consumibles de adobo y salsas) hasta más de \$1.000.000 COP (combos de barriles industriales con accesorios).
*   **Tiempo improductivo en conciliación:** Se estiman entre **12 y 15 horas semanales** dedicadas exclusivamente a conteos físicos repetitivos, cuadres manuales de caja y resolución de inconsistencias entre notas de papel y hojas electrónicas.
*   **Impacto financiero estimado:** Las roturas imprevistas de inventario y la imposibilidad de atender órdenes de compra de barriles por falta de accesorios representan pérdidas de venta estimadas entre **\$1.800.000 y \$2.500.000 COP mensuales**, sumadas a mermas por descontrol de consumibles de hasta un 9% sobre el valor del stock circulante.

## 1.3. Respaldo Bibliográfico y Teórico
La literatura especializada en ingeniería de software y gestión logística ratifica que las empresas manufactureras y comerciales que carecen de sistemas transaccionales integrados sufren pérdidas sistemáticas de eficiencia operativa. Al respecto, Ballou (2004) subraya que la gestión del inventario constituye uno de los tres componentes vitales de la cadena de suministro, y que la falta de visibilidad en los niveles de existencias genera el denominado *efecto látigo*, desestabilizando los pedidos hacia los proveedores e incrementando los costos de almacenamiento.

Asimismo, Laudon y Laudon (2020) señalan que los sistemas de información transaccionales (TPS) estructurados bajo bases de datos relacionales normalizadas permiten mitigar la redundancia de datos, garantizar la integridad referencial y proveer a la gerencia indicadores fiables para la toma de decisiones oportunas. En el ámbito del diseño de software, autores canónicos como Sommerville (2019) y Pressman y Maxim (2021) enfatizan que la adopción de arquitecturas desacopladas como el patrón Modelo-Vista-Controlador (MVC), combinada con lenguajes fuertemente tipados y orientados a objetos como Java, confiere a las soluciones informáticas la robustez, mantenibilidad y modularidad indispensables para garantizar un ciclo de vida prolongado sin incurrir en deuda técnica temprana.

## 1.4. Formulación de la Pregunta de Investigación
A partir de la necesidad diagnosticada y del marco conceptual de referencia, se formula la siguiente pregunta que orienta el desarrollo del proyecto:

> **¿Cómo el desarrollo e implementación de un sistema de información de escritorio basado en el patrón arquitectónico Modelo-Vista-Controlador (MVC), lenguaje de programación Java y una base de datos relacional normalizada, puede optimizar el control de inventario, la gestión de compras a proveedores y la trazabilidad del proceso de facturación de ventas en la microempresa Volcano Barriles?**
