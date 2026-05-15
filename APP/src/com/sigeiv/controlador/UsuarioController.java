package com.sigeiv.controlador;

import com.sigeiv.dao.UsuarioDAO;
import com.sigeiv.modelo.Usuario;
import com.sigeiv.util.HashUtil;

/**
 * Controlador de logica de negocio para Usuarios.
 * Gestiona autenticacion y operaciones sobre usuarios.
 * 
 * Entrada: Credenciales de usuario (username, contrasena)
 * Proceso: Valida credenciales contra la BD usando hash SHA-256
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
     */
    public boolean registrar(String nombre, String username, String password, int idRol) {
        Usuario u = new Usuario();
        u.setNombreUsuario(nombre);
        u.setUsername(username);
        u.setContrasena(HashUtil.sha256(password));
        u.setIdRol(idRol);
        u.setActivo(true);
        return usuarioDAO.insertar(u);
    }

    /**
     * Verifica si el usuario logueado tiene un rol especifico.
     * Roles: 1=Administrador, 2=Vendedor, 3=Consultor
     */
    public boolean tieneRol(int idRol) {
        return usuarioLogueado != null && usuarioLogueado.getIdRol() == idRol;
    }

    /** Verifica si es administrador */
    public boolean esAdmin() {
        return tieneRol(1);
    }

    /** Verifica si es vendedor */
    public boolean esVendedor() {
        return tieneRol(2);
    }

    /** Verifica si es consultor (solo lectura) */
    public boolean esConsultor() {
        return tieneRol(3);
    }

    /** Verifica si puede escribir (admin o vendedor) */
    public boolean puedeEscribir() {
        return esAdmin() || esVendedor();
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }
}





