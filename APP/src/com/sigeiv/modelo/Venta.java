package com.sigeiv.modelo;

import java.util.Date;

/**
 * Clase que representa una Venta realizada en SIGEIV-Volcano.
 * Corresponde a la tabla 'venta' de la base de datos.
 * 
 * Entrada: id_venta, fecha, id_cliente, id_usuario, total
 * Proceso: Encapsula una transaccion de venta
 * Salida: Objeto Venta con total calculado
 */
public class Venta {

    // --- Atributos ---
    private int idVenta;
    private Date fecha;
    private int idCliente;
    private int idUsuario;
    private double total;

    // --- Constructores ---

    /** Constructor vacio */
    public Venta() {}

    /** Constructor con todos los atributos */
    public Venta(int idVenta, Date fecha, int idCliente, int idUsuario, double total) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.total = total;
    }

    // --- Getters ---

    public int getIdVenta() {
        return idVenta;
    }

    public Date getFecha() {
        return fecha;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public double getTotal() {
        return total;
    }

    // --- Setters ---

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Venta #" + idVenta + " - $" + String.format("%.2f", total);
    }
}





