package com.sigeiv.modelo;

/**
 * Clase que representa un Detalle de Compra en SIGEIV-Volcano.
 * Corresponde a la tabla 'detalle_compra' de la base de datos.
 * 
 * Entrada: id_detalle_compra, id_compra, id_producto, cantidad, precio_unitario
 * Proceso: Encapsula una linea de producto dentro de una compra
 * Salida: Objeto DetalleCompra con subtotal calculado
 */
public class DetalleCompra {

    // --- Atributos ---
    private int idDetalleCompra;
    private int idCompra;
    private int idProducto;
    private int cantidad;
    private double precioUnitario;

    // --- Constructores ---

    /** Constructor vacio */
    public DetalleCompra() {}

    /** Constructor con todos los atributos */
    public DetalleCompra(int idDetalleCompra, int idCompra, int idProducto,
                         int cantidad, double precioUnitario) {
        this.idDetalleCompra = idDetalleCompra;
        this.idCompra = idCompra;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // --- Getters ---

    public int getIdDetalleCompra() {
        return idDetalleCompra;
    }

    public int getIdCompra() {
        return idCompra;
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

    // --- Setters ---

    public void setIdDetalleCompra(int idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
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

    /**
     * Calcula el subtotal multiplicando cantidad por precio unitario.
     * @return subtotal de esta linea de compra
     */
    public double getSubtotal() {
        return this.cantidad * this.precioUnitario;
    }

    @Override
    public String toString() {
        return "DetalleCompra #" + idDetalleCompra + " - Cant: " + cantidad
             + " - $" + String.format("%.2f", getSubtotal());
    }
}





