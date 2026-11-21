# CAPÍTULO 4. METODOLOGÍA DE DESARROLLO DE SOFTWARE

Para garantizar la entrega puntual, la calidad del código fuente y la satisfacción de las necesidades del negocio de Volcano Barriles, se adoptó una metodología de desarrollo de software basada en **Scrum adaptado al entorno académico y de microempresa**, combinando ciclos iterativos e incrementales con las fases de control de la ingeniería de software tradicional.

## 4.1. Justificación de la Metodología
A diferencia del modelo tradicional en Cascada estricto, Scrum permite inspeccionar y adaptar el software de forma continua a medida que se completan las funcionalidades. Dado que los procesos de Volcano Barriles estaban sujetos a validaciones directas con los administradores y docentes de la UNIAJC, un enfoque ágil por Sprints facilitó la detección temprana de inconsistencias en el modelado de datos y en la usabilidad de las interfaces gráficas.

## 4.2. Fases del Proceso de Desarrollo
El proyecto se estructuró en cuatro fases secuenciales e iterativas:

1.  **Fase de Diagnóstico e Inicio (Semanas 1 a 4):**
    *   Comprensión del contexto operacional de Volcano Barriles mediante entrevistas y visitas al taller.
    *   Construcción del *Product Backlog* inicial con los requerimientos funcionales y no funcionales.
    *   Formulación de la arquitectura tecnológica base (Java 17, Swing, SQLite/MySQL).

2.  **Fase de Análisis y Modelado Arquitectónico (Semanas 5 a 7):**
    *   Diseño del Diagrama de Clases inicial y Modelo Entidad-Relación (DER) normalizado.
    *   Especificación de requerimientos y casos de uso detallados según la plantilla institucional UNIAJC.
    *   Diseño conceptual de la interfaz gráfica (mockups y esquema de colores).

3.  **Fase de Construcción e Implementación Incremental (Semanas 8 a 13):**
    *   Implementación de la base de datos relacional con sentencias DDL, índices y datos iniciales de prueba.
    *   Desarrollo de la capa de persistencia mediante el patrón Data Access Object (DAO) y conexión JDBC Singleton.
    *   Desarrollo de los controladores de lógica de negocio y seguridad (encriptación SHA-256).
    *   Construcción de las vistas Java Swing (`JFrame`, `JPanel`, `JTable`, `JComboBox`) con control de roles.

4.  **Fase de Pruebas, Refinamiento y Cierre (Semanas 14 a 16):**
    *   Pruebas de integración entre la interfaz gráfica y la persistencia en base de datos.
    *   Pruebas de integridad transaccional (registro de venta con descuento atómico de stock y compras con adición de stock).
    *   Empaquetado del software ejecutable (.jar y .exe portable).
    *   Sustentación final ante el comité evaluador de la UNIAJC.

## 4.3. Cronograma de Sprints y Entregables
El plan de trabajo se sincronizó con las ponderaciones evaluativas del Proyecto Integrador:

| Sprint / Hito | Duración | Actividades Principales | Entregables Clave | Ponderación |
| :--- | :---: | :--- | :--- | :---: |
| **Sprint 1: Diagnóstico y Fundamentos** | Semanas 1 a 5 | Diagnóstico en taller, planteamiento del problema, objetivos SMART, justificación, DER inicial y diagrama de clases básico. | Documento PDF Primera Entrega (Fase 1), DER y Clases UML. | **20%** |
| **Sprint 2: Diseño Detallado y Capa de Datos** | Semanas 6 a 9 | Especificación formal de RF/RNF, Casos de uso, scripts SQL DDL, configuración JDBC Singleton y pruebas unitarias de DAOs. | Script SQL probado, modelos UML refinados y fichas de requerimientos. | En progreso |
| **Sprint 3: Desarrollo de Módulos Core** | Semanas 10 a 13 | Construcción de GUI Swing (Login, Productos, Clientes, Proveedores, Ventas, Compras, Inventario), integración MVC y control de roles. | Documento Final PDF y Aplicativo 100% operativo con ejecutable. | **40%** |
| **Sprint 4: Validación y Sustentación** | Semanas 14 a 16 | Pruebas de caja negra, validación con usuarios finales, elaboración de diapositivas y preparación de la sustentación oral. | Software en vivo, diapositivas y sustentación ante jurados. | **40%** |
