package com.sigeiv.modelo;

/**
 * Clase que representa un Usuario del sistema SIGEIV-Volcano.
 * Corresponde a la tabla 'usuario' de la base de datos.
 * 
 * Entrada: id_usuario, nombre_usuario, username, contrasena, id_rol, activo
 * Proceso: Encapsula credenciales y datos del usuario con getters/setters
 * Salida: Objeto Usuario con control de acceso segun rol
 */
public class Usuario {

    // --- Atributos ---
    private int idUsuario;
    private String nombreUsuario;
    private String username;
    private String contrasena;
    private int idRol;
    private boolean activo;

    // --- Constructores ---

    /** Constructor vacio */
    public Usuario() {}

    /** Constructor con todos los atributos */
    public Usuario(int idUsuario, String nombreUsuario, String username,
                   String contrasena, int idRol, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.username = username;
        this.contrasena = contrasena;
        this.idRol = idRol;
        this.activo = activo;
    }

    // --- Getters ---

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getUsername() {
        return username;
    }

    public String getContrasena() {
        return contrasena;
    }

    public int getIdRol() {
        return idRol;
    }

    public boolean isActivo() {
        return activo;
    }

    // --- Setters ---

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + username + ")";
    }
}





