package com.sigeiv.controlador;

import com.sigeiv.dao.UsuarioDAO;
import com.sigeiv.modelo.Usuario;
import com.sigeiv.util.HashUtil;

/**
 * Controlador de logica de negocio para Usuarios.
 * Gestiona autenticacion y operaciones sobre usuarios.
 *
 * Fase 2 - RF-01, RF-02:
 * Entrada: Credenciales de usuario (username, contrasena)
 * Proceso: Valida credenciales contra la BD usando hash SHA-256;
 *          reconoce los 5 roles jerarquicos oficiales:
 *            1 = Administrador / Gerente General
 *            2 = Jefe de Produccion y Calidad
 *            3 = Encargado de Almacen y Bodega
 *            4 = Asesor Comercial / Ventas
 *            5 = Auditor / Consultor Externo
 * Salida: Objeto Usuario autenticado o null si falla
 */
public class UsuarioController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuarioLogueado;

    /**
     * Autentica un usuario con username y contrasena.
     * @param username Nombre de usuario
     * @param password Contrasena en texto plano
     * @return Usuario autenticado, o null si las credenciales son invalidas
     */
    public Usuario autenticar(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return null;
        }
        Usuario usuario = usuarioDAO.buscarPorUsername(username.trim());
        if (usuario != null) {
            String hashPassword = HashUtil.sha256(password);
            if (usuario.getContrasena().equals(hashPassword)) {
                this.usuarioLogueado = usuario;
                return usuario;
            }
        }
        return null;
    }

    /**
     * Registra un nuevo usuario con contrasena hasheada.
     * Fase 2: acepta email y puedeAutorizar.
     */
    public boolean registrar(String nombre, String username, String password,
                             String email, int idRol, boolean puedeAutorizar) {
        Usuario u = new Usuario();
        u.setNombreUsuario(nombre);
        u.setUsername(username);
        u.setContrasena(HashUtil.sha256(password));
        u.setEmail(email != null ? email : "");
        u.setIdRol(idRol);
        u.setPuedeAutorizar(puedeAutorizar);
        u.setActivo(true);
        return usuarioDAO.insertar(u);
    }

    /** Mantiene compatibilidad con llamadas anteriores sin email ni puedeAutorizar. */
    public boolean registrar(String nombre, String username, String password, int idRol) {
        return registrar(nombre, username, password, "", idRol, false);
    }

    // -----------------------------------------------------------------------
    // Verificacion de roles — 5 roles jerarquicos (Fase 2)
    // -----------------------------------------------------------------------

    /** Verifica si el usuario logueado tiene un rol especifico. */
    public boolean tieneRol(int idRol) {
        return usuarioLogueado != null && usuarioLogueado.getIdRol() == idRol;
    }

    /** Rol 1 - Administrador / Gerente General: control total. */
    public boolean esAdmin() {
        return tieneRol(1);
    }

    /** Rol 2 - Jefe de Produccion y Calidad: autoriza lotes y dictamenes. */
    public boolean esJefeProduccion() {
        return tieneRol(2);
    }

    /** Rol 3 - Encargado de Almacen y Bodega: registra movimientos. */
    public boolean esAlmacen() {
        return tieneRol(3);
    }

    /** Rol 4 - Asesor Comercial / Ventas: autoriza despachos de venta. */
    public boolean esAsesorComercial() {
        return tieneRol(4);
    }

    /** Rol 5 - Auditor / Consultor Externo: solo lectura y auditoria. */
    public boolean esAuditor() {
        return tieneRol(5);
    }

    /**
     * Verifica si el usuario puede autorizar documentos soporte.
     * Combina el flag 'puede_autorizar' de la BD con el rol jerarquico.
     */
    public boolean puedeAutorizar() {
        if (usuarioLogueado == null) return false;
        return usuarioLogueado.isPuedeAutorizar() || esAdmin();
    }

    /**
     * Verifica si el usuario puede registrar o editar datos operativos
     * (todos los roles excepto Auditor).
     */
    public boolean puedeEscribir() {
        return usuarioLogueado != null && !esAuditor();
    }

    /**
     * @deprecated Usar esAsesorComercial() o esAlmacen() segun contexto.
     * Mantenido por compatibilidad con VentaPanel y otros paneles anteriores.
     */
    @Deprecated
    public boolean esVendedor() {
        return esAsesorComercial();
    }

    /**
     * @deprecated Usar esAuditor() segun contexto.
     * Mantenido por compatibilidad con paneles anteriores.
     */
    @Deprecated
    public boolean esConsultor() {
        return esAuditor();
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }

    public java.util.List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(int id) {
        if (id <= 0) return null;
        return usuarioDAO.buscarPorId(id);
    }

    public boolean actualizar(Usuario u) {
        if (u == null || u.getIdUsuario() <= 0) return false;
        return usuarioDAO.actualizar(u);
    }

    public boolean eliminar(int id) {
        if (id <= 0) return false;
        return usuarioDAO.eliminar(id);
    }
}

