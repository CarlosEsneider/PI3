# CAPÍTULO 2. OBJETIVOS DEL PROYECTO (METODOLOGÍA SMART)

Los objetivos han sido formulados bajo el estándar **SMART** (*Specific, Measurable, Achievable, Relevant, Time-bound*), garantizando que sean específicos, medibles, alcanzables, relevantes y acotados al cronograma de ejecución del Proyecto Integrador.

## 2.1. Objetivo General
Desarrollar una solución informática de escritorio orientada a objetos bajo el patrón arquitectónico Modelo-Vista-Controlador (MVC), lenguaje Java y un sistema de gestión de base de datos relacional normalizado para la microempresa Volcano Barriles, con el propósito de sistematizar, controlar y optimizar los procesos de administración de inventarios, compras a proveedores y registro de ventas durante el primer periodo académico de 2026.

## 2.2. Objetivos Específicos
En coherencia secuencial con el ciclo de vida de desarrollo de software y las competencias integradas de Ingeniería de Software 1, Programación 2 y Bases de Datos 2, se definen los siguientes cuatro objetivos específicos:

1.  **Diagnosticar y especificar los requerimientos del sistema:**  
    Levantar y documentar formalmente las necesidades funcionales (RF) y no funcionales (RNF) de Volcano Barriles mediante técnicas de ingeniería de requisitos (entrevistas semiestructuradas, revisión documental y matrices IPO), estructurando las historias de usuario y casos de uso detallados según el estándar institucional UNIAJC.
    *   *Criterio de éxito / Medición:* Documento de especificación de requerimientos aprobado con un mínimo de 15 RF y 5 RNF completamente parametrizados y validados.

2.  **Diseñar los modelos conceptuales, lógicos y arquitectónicos:**  
    Modelar la arquitectura técnica del sistema mediante diagramas estándar de la notación UML 2.5 (diagramas de clases estructurales, casos de uso e interacción de actividades) y el diseño de la base de datos mediante el Modelo Entidad-Relación (DER) normalizado en Tercera Forma Normal (3FN), garantizando la integridad de llaves primarias y foráneas.
    *   *Criterio de éxito / Medición:* Modelos UML (clases y casos de uso) y esquema relacional de 10 tablas interconectadas con trazabilidad del 100% frente a los requerimientos del negocio.

3.  **Construir el aplicativo de escritorio y la capa de persistencia:**  
    Programar los módulos de software en lenguaje Java 17 aplicando el patrón arquitectónico MVC, el patrón Data Access Object (DAO), componentes gráficos interactivos con Java Swing (`JTable`, `JComboBox`, interfaces reactivas con `SwingWorker`) y la capa de acceso a datos relacional mediante JDBC, sentencias SQL parametrizadas y algoritmos criptográficos (SHA-256) para la seguridad de credenciales.
    *   *Criterio de éxito / Medición:* Software 100% ejecutable y operativo, con módulos funcionales de Autenticación por roles, CRUD de Productos, Clientes, Proveedores, Categorías, Registro transaccional de Ventas y Compras con actualización atómica de inventario, y Reportes estadísticos.

4.  **Validar y evaluar la operatividad del sistema:**  
    Ejecutar planes de pruebas funcionales de caja negra y pruebas de consistencia transaccional sobre la base de datos (descuento e incremento automático de stock, control de stock mínimo y prevención de inyecciones SQL), verificando la usabilidad y satisfacción operativa con los usuarios finales de Volcano Barriles.
    *   *Criterio de éxito / Medición:* Matriz de casos de prueba ejecutada al 100% con cero defectos críticos de consistencia de datos, y cumplimiento integral de las rúbricas institucionales UNIAJC (SCE).
