package com.sigeiv.modelo;

/**
 * Clase que representa un Proveedor en el sistema SIGEIV-Volcano.
 * Corresponde a la tabla 'proveedor' de la base de datos.
 * 
 * Entrada: id_proveedor, empresa, contacto, telefono
 * Proceso: Encapsula los datos del proveedor con getters/setters
 * Salida: Objeto Proveedor con informacion de suministro
 */
public class Proveedor {

    // --- Atributos ---
    private int idProveedor;
    private String empresa;
    private String contacto;
    private String telefono;

    // --- Constructores ---

    /** Constructor vacio */
    public Proveedor() {}

    /** Constructor con todos los atributos */
    public Proveedor(int idProveedor, String empresa, String contacto, String telefono) {
        this.idProveedor = idProveedor;
        this.empresa = empresa;
        this.contacto = contacto;
        this.telefono = telefono;
    }

    // --- Getters ---

    public int getIdProveedor() {
        return idProveedor;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getContacto() {
        return contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    // --- Setters ---

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return empresa;
    }
}





