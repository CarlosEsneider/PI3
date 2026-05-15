package com.sigeiv.modelo;

import java.util.Date;

/**
 * Clase que representa una Compra (abastecimiento) en SIGEIV-Volcano.
 * Corresponde a la tabla 'compra' de la base de datos.
 * 
 * Entrada: id_compra, fecha, total, id_proveedor, id_usuario
 * Proceso: Encapsula una transaccion de compra a proveedor
 * Salida: Objeto Compra con total calculado
 */
public class Compra {

    // --- Atributos ---
    private int idCompra;
    private Date fecha;
    private double total;
    private int idProveedor;
    private int idUsuario;

    // --- Constructores ---

    /** Constructor vacio */
    public Compra() {}

    /** Constructor con todos los atributos */
    public Compra(int idCompra, Date fecha, double total, int idProveedor, int idUsuario) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.total = total;
        this.idProveedor = idProveedor;
        this.idUsuario = idUsuario;
    }

    // --- Getters ---

    public int getIdCompra() {
        return idCompra;
    }

    public Date getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    // --- Setters ---

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Compra #" + idCompra + " - $" + String.format("%.2f", total);
    }
}





