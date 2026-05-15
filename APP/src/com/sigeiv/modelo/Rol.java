package com.sigeiv.modelo;

/**
 * Clase que representa un Rol de usuario en el sistema SIGEIV-Volcano.
 * Corresponde a la tabla 'rol' de la base de datos.
 * 
 * Entrada: id_rol (int), nombre_rol (String)
 * Proceso: Encapsula los datos del rol con getters y setters
 * Salida: Objeto Rol con sus atributos accesibles
 */
public class Rol {

    // --- Atributos ---
    private int idRol;
    private String nombreRol;

    // --- Constructores ---

    /** Constructor vacio */
    public Rol() {}

    /** Constructor con todos los atributos */
    public Rol(int idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }

    // --- Getters ---

    public int getIdRol() {
        return idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    // --- Setters ---

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    @Override
    public String toString() {
        return nombreRol;
    }
}





