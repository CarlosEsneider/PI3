package com.sigeiv.modelo;

/**
 * Clase que representa un Cliente de la tienda Volcano Barriles.
 * Corresponde a la tabla 'cliente' de la base de datos.
 * 
 * Entrada: id_cliente, nombre_cliente, dni, telefono
 * Proceso: Encapsula los datos del cliente con getters/setters
 * Salida: Objeto Cliente para asociar a ventas
 */
public class Cliente {

    // --- Atributos ---
    private int idCliente;
    private String nombreCliente;
    private String dni;
    private String telefono;

    // --- Constructores ---

    /** Constructor vacio */
    public Cliente() {}

    /** Constructor con todos los atributos */
    public Cliente(int idCliente, String nombreCliente, String dni, String telefono) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.dni = dni;
        this.telefono = telefono;
    }

    // --- Getters ---

    public int getIdCliente() {
        return idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getDni() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }

    // --- Setters ---

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombreCliente + " (DNI: " + dni + ")";
    }
}





