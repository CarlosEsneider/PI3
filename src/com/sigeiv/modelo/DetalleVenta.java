package com.sigeiv.modelo;

/**
 * Clase que representa un Detalle de Venta en SIGEIV-Volcano.
 * Corresponde a la tabla 'detalle_venta' de la base de datos.
 * 
 * Entrada: id_detalle, id_venta, id_producto, cantidad, precio_unitario, subtotal
 * Proceso: Encapsula una linea de producto dentro de una venta
 * Salida: Objeto DetalleVenta con subtotal calculado
 */
public class DetalleVenta {

    // --- Atributos ---
    private int idDetalle;
    private int idVenta;
    private int idProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    // --- Constructores ---

    /** Constructor vacio */
    public DetalleVenta() {}

    /** Constructor con todos los atributos */
    public DetalleVenta(int idDetalle, int idVenta, int idProducto,
                        int cantidad, double precioUnitario, double subtotal) {
        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // --- Getters ---

    public int getIdDetalle() {
        return idDetalle;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // --- Setters ---

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Calcula el subtotal multiplicando cantidad por precio unitario.
     */
    public void calcularSubtotal() {
        this.subtotal = this.cantidad * this.precioUnitario;
    }

    @Override
    public String toString() {
        return "Detalle #" + idDetalle + " - Cant: " + cantidad + " - $" + String.format("%.2f", subtotal);
    }
}





