package com.sigeiv.modelo;

/**
 * Clase que representa un Usuario del sistema SIGEIV-Volcano.
 * Corresponde a la tabla 'usuario' de la base de datos.
 * 
 * Fase 2 - RF-01, RF-02:
 * Entrada: id_usuario, nombre_completo, username, contrasena, email, id_rol, puede_autorizar, activo
 * Proceso: Encapsula credenciales y datos del usuario con getters/setters
 * Salida: Objeto Usuario con control de acceso segun los 5 roles jerarquicos
 */
public class Usuario {

    // --- Atributos ---
    private int idUsuario;
    private String nombreUsuario;
    private String username;
    private String contrasena;
    private String email;
    private int idRol;
    private boolean puedeAutorizar;
    private boolean activo;

    // --- Constructores ---

    /** Constructor vacio */
    public Usuario() {}

    /** Constructor con todos los atributos (Fase 2: incluye email y puedeAutorizar) */
    public Usuario(int idUsuario, String nombreUsuario, String username,
                   String contrasena, String email, int idRol,
                   boolean puedeAutorizar, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.username = username;
        this.contrasena = contrasena;
        this.email = email;
        this.idRol = idRol;
        this.puedeAutorizar = puedeAutorizar;
        this.activo = activo;
    }

    /** Constructor de compatibilidad con codigo anterior (sin email ni puedeAutorizar) */
    public Usuario(int idUsuario, String nombreUsuario, String username,
                   String contrasena, int idRol, boolean activo) {
        this(idUsuario, nombreUsuario, username, contrasena, "", idRol, false, activo);
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

    public String getEmail() {
        return email;
    }

    public int getIdRol() {
        return idRol;
    }

    public boolean isPuedeAutorizar() {
        return puedeAutorizar;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public void setPuedeAutorizar(boolean puedeAutorizar) {
        this.puedeAutorizar = puedeAutorizar;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + username + ")";
    }
}





