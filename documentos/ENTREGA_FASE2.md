# Entrega Fase 2 — Seguridad, Roles y Navegación
**Sistema:** SIGEIV-Volcano  
**Proyecto Integrador — Semestre IV**  
**Rama:** `entrega-fase2`  
**Fecha:** 2026-11-21

---

## 1. Objetivo de la Fase 2

Implementar el módulo de **seguridad, autenticación y control de acceso basado en roles (RBAC)** para el sistema SIGEIV-Volcano. Se establecieron los 5 roles jerárquicos oficiales, se protegió el acceso al sistema con hash SHA-256 y bloqueo por intentos fallidos, y se condicionó la navegación del menú lateral a los permisos de cada rol.

---

## 2. Archivos Modificados

### 2.1 `Usuario.java` — Modelo
**Ruta:** `APP/src/com/sigeiv/modelo/Usuario.java`

**Qué se hizo:**
- Se agregaron dos nuevos atributos al modelo: `email` (String) y `puedeAutorizar` (boolean).
- Se creó un **constructor completo** de 8 parámetros que incluye los nuevos campos (`idUsuario`, `nombreCompleto`, `username`, `contrasena`, `email`, `idRol`, `puedeAutorizar`, `activo`).
- Se mantuvo un **constructor de compatibilidad** de 6 parámetros (sin `email` ni `puedeAutorizar`) para no romper el código anterior.
- Se agregaron los getters y setters correspondientes: `getEmail()`, `setEmail()`, `isPuedeAutorizar()`, `setPuedeAutorizar()`.
- El `toString()` devuelve `nombreUsuario (username)` para facilitar su uso en listas y combos.

---

### 2.2 `UsuarioDAO.java` — Capa de Acceso a Datos
**Ruta:** `APP/src/com/sigeiv/dao/UsuarioDAO.java`

**Qué se hizo:**
- **`insertar()`**: se amplió el INSERT para incluir los campos `email` y `puede_autorizar`. Usa `PreparedStatement` para prevenir inyección SQL. Recupera la clave generada automáticamente y la asigna al objeto.
- **`actualizar()`**: se amplió el UPDATE para actualizar también `email` y `puede_autorizar`.
- **`eliminar()`**: se reemplazó el `DELETE` físico por un **borrado lógico** (`UPDATE usuario SET activo = 0`), preservando la integridad referencial del historial.
- **`buscarPorUsername()`**: filtra por `activo = 1` para que solo usuarios activos puedan iniciar sesión.
- **`mapear()`** (privado): lee `email` y `puede_autorizar` con bloques `try/catch` independientes como **fallback seguro**, garantizando compatibilidad con bases de datos antiguas (SQLite del prototipo) que no tengan esas columnas.

---

### 2.3 `UsuarioController.java` — Controlador de Lógica de Negocio
**Ruta:** `APP/src/com/sigeiv/controlador/UsuarioController.java`

**Qué se hizo:**
- **`autenticar()`**: valida las credenciales comparando el hash SHA-256 de la contraseña ingresada contra el hash almacenado en BD. Retorna el objeto `Usuario` si es correcto, `null` si falla.
- **`registrar()`**: acepta los nuevos parámetros `email` y `puedeAutorizar`. Hashea la contraseña antes de persistir.
- **5 métodos de verificación de rol** (Fase 2 — RF-02):
  - `esAdmin()` → Rol 1
  - `esJefeProduccion()` → Rol 2
  - `esAlmacen()` → Rol 3
  - `esAsesorComercial()` → Rol 4
  - `esAuditor()` → Rol 5
- **`puedeAutorizar()`**: combina el flag `puede_autorizar` de BD con el rol (el Admin siempre puede autorizar).
- **`puedeEscribir()`**: retorna `true` para todos los roles excepto Auditor (solo lectura).
- Se marcaron `@Deprecated` los métodos `esVendedor()` y `esConsultor()` por compatibilidad con paneles anteriores.
- **`cerrarSesion()`**: limpia el usuario en sesión y regresa al `LoginFrame`.

---

### 2.4 `UsuarioPanel.java` — Vista de Gestión de Usuarios
**Ruta:** `APP/src/com/sigeiv/vista/UsuarioPanel.java`

**Qué se hizo:**
- Se define el **catálogo oficial de 5 roles** como arreglo estático `ROLES[]` que alimenta el `JComboBox` del formulario.
- **Control de acceso en construcción**: si el usuario no es Admin (`!ctrl.esAdmin()`), el panel muestra únicamente el mensaje *"Acceso Denegado"* sin renderizar el formulario ni la tabla.
- Se agregaron los campos de formulario: `txtEmail` y `chkPuedeAutorizar` (con tooltip explicativo).
- La **tabla** muestra columnas: ID, Nombre, Username, Email, Rol, Autoriza, Estado.
- **`guardarUsuario()`**: maneja creación y edición, incluyendo email y puedeAutorizar; en edición, solo actualiza la contraseña si se ingresó una nueva.
- **`eliminarUsuario()`** (borrado lógico): muestra confirmación, impide desactivar el propio usuario en sesión, y llama al DAO para marcar `activo = false`.
- **`limpiarFormulario()`**: resetea todos los campos incluyendo los nuevos de Fase 2.

---

### 2.5 `LoginFrame.java` — Ventana de Inicio de Sesión
**Ruta:** `APP/src/com/sigeiv/vista/LoginFrame.java`

**Qué se hizo:**
- **Control de intentos fallidos** (RF-01): contador `intentosFallidos` con constante `MAX_INTENTOS = 3`.
  - Cada credencial incorrecta incrementa el contador y muestra los intentos restantes en el label de error.
  - Al llegar a 3 intentos fallidos consecutivos: el botón queda deshabilitado con texto *"Acceso Bloqueado"* y aparece un `JOptionPane.ERROR_MESSAGE` que informa al usuario de contactar al administrador. El acceso solo se recupera reiniciando la aplicación.
- **`SwingWorker`**: la autenticación se ejecuta en hilo de fondo para no bloquear el EDT (Event Dispatch Thread) de Swing durante la consulta a BD.
- **Teclas rápidas**: `Enter` en campo usuario pasa el foco a contraseña; `Enter` en contraseña dispara el login.
- Diseño oscuro temático con colores volcánicos (naranja `#FF6B35`).
- Al autenticar correctamente: reinicia el contador, cierra el `LoginFrame` y abre `MainFrame(usuarioController)`.

---

### 2.6 `MainFrame.java` — Ventana Principal y Navegación
**Ruta:** `APP/src/com/sigeiv/vista/MainFrame.java`

**Qué se hizo:**
- Recibe el `UsuarioController` con la sesión activa y lo propaga a todos los paneles hijos.
- El **título de la ventana** muestra nombre del usuario y su rol: `SIGEIV-Volcano - [Nombre] [Rol]`.
- El **sidebar** muestra el nombre y rol del usuario activo en el encabezado.
- **Matriz de visibilidad de módulos** (RF-02 — ver sección 4): cada botón del sidebar se muestra u oculta con `setVisible(boolean)` según el `idRol` del usuario.
- Implementa `CardLayout` para cambiar entre paneles sin abrir nuevas ventanas.
- El botón **"Cerrar Sesión"** llama a `usuarioCtrl.cerrarSesion()`, cierra el frame y abre un nuevo `LoginFrame`.
- **`obtenerNombreRol(int)`**: traduce el `id_rol` al nombre oficial del rol (5 casos).
- **`aplicarTema()`** (estático): configura el Look & Feel y los overrides globales de colores antes de iniciar la UI.

---

## 3. Los 5 Roles Jerárquicos

| ID | Nombre del Rol | Descripción |
|----|---------------|-------------|
| 1 | **Administrador / Gerente General** | Control total del sistema. Acceso completo a todos los módulos. Puede crear, editar y desactivar usuarios. |
| 2 | **Jefe de Producción y Calidad** | Gestiona categorías/líneas de productos e inventario. Puede autorizar lotes y dictámenes de calidad. |
| 3 | **Encargado de Almacén y Bodega** | Registra movimientos de inventario, ventas (despacho físico) y clientes. |
| 4 | **Asesor Comercial / Ventas** | Gestiona clientes y ventas/despachos. Sin acceso a inventario, reportes ni configuración. |
| 5 | **Auditor / Consultor Externo** | Solo lectura. Accede a inventario y reportes pero no puede modificar ningún dato. |

---

## 4. Matriz de Control de Acceso

La visibilidad de cada módulo en el sidebar se controla en `MainFrame.java` mediante flags booleanos derivados del `idRol`.

| Módulo / Panel      | Admin (1) | Jefe Prod. (2) | Almacén (3) | Asesor Com. (4) | Auditor (5) |
|---------------------|:---------:|:--------------:|:-----------:|:---------------:|:-----------:|
| **Productos**       | ✅        | ✅             | ✅          | ✅              | ✅          |
| **Clientes**        | ✅        | ❌             | ✅          | ✅              | ❌          |
| **Proveedores**     | ✅        | ❌             | ❌          | ❌              | ❌          |
| **Categorías**      | ✅        | ✅             | ❌          | ❌              | ❌          |
| **Usuarios**        | ✅        | ❌             | ❌          | ❌              | ❌          |
| **Ventas**          | ✅        | ❌             | ✅          | ✅              | ❌          |
| **Compras**         | ✅        | ❌             | ❌          | ❌              | ❌          |
| **Inventario**      | ✅        | ✅             | ✅          | ❌              | ✅          |
| **Reportes**        | ✅        | ✅             | ✅          | ❌              | ✅          |

> [!NOTE]
> La visibilidad controla **qué módulos aparecen en el menú**. La lógica interna de cada panel (botones de escritura, edición, eliminación) aplica validaciones adicionales usando los métodos del `UsuarioController` (`puedeEscribir()`, `esAdmin()`, etc.).

---

## 5. Mecanismo de Seguridad — Autenticación

```
Usuario ingresa credenciales
        │
        ▼
LoginFrame.iniciarSesion()   [SwingWorker — hilo de fondo]
        │
        ▼
UsuarioController.autenticar(username, password)
        │
        ▼
UsuarioDAO.buscarPorUsername(username)   → WHERE activo = 1
        │
        ├── No encontrado → null
        │
        └── Encontrado → comparar HashUtil.sha256(password) == hash_bd
                │
                ├── Coincide → Usuario autenticado → MainFrame
                │
                └── No coincide → intentosFallidos++
                        │
                        ├── < 3 → mostrar intentos restantes
                        └── >= 3 → BLOQUEO — botón deshabilitado + JOptionPane
```

---

## 6. Dependencia de Clases

```
LoginFrame
    └── UsuarioController
            ├── UsuarioDAO
            │       └── ConexionDB (Singleton)
            ├── Usuario (modelo)
            └── HashUtil (SHA-256)

MainFrame
    └── UsuarioController (sesión activa propagada a todos los paneles)
            ├── UsuarioPanel  ← solo visible para Rol 1
            ├── ProductoPanel
            ├── ClientePanel
            ├── ProveedorPanel
            ├── CategoriaPanel
            ├── VentaPanel
            ├── CompraPanel
            ├── InventarioPanel
            └── ReportePanel
```

---

*Documento generado para la entrega de la Fase 2 del Proyecto Integrador — UNIAJC, 2026.*
