package com.sigeiv.modelo;

/**
 * Representa cada renglon de producto dentro de un documento soporte de inventario.
 * Corresponde a la tabla 'detalle_movimiento'.
 */
public class DetalleMovimiento {

    private int idDetalleMovimiento;
    private int idMovimiento;
    private int idProducto;
    private String nombreProducto;
    private String codigoSku;
    private int cantidad;
    private double costoUnitario;
    private int saldoResultante;
    private int stockActualBodega; // Existencias fisicas actuales en bodega para apoyo visual

    public DetalleMovimiento() {}

    public DetalleMovimiento(int idDetalleMovimiento, int idMovimiento, int idProducto,
                             int cantidad, double costoUnitario, int saldoResultante) {
        this.idDetalleMovimiento = idDetalleMovimiento;
        this.idMovimiento = idMovimiento;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.saldoResultante = saldoResultante;
    }

    public int getIdDetalleMovimiento() {
        return idDetalleMovimiento;
    }

    public void setIdDetalleMovimiento(int idDetalleMovimiento) {
        this.idDetalleMovimiento = idDetalleMovimiento;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCodigoSku() {
        return codigoSku;
    }

    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public int getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(int saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    public int getStockActualBodega() {
        return stockActualBodega;
    }

    public void setStockActualBodega(int stockActualBodega) {
        this.stockActualBodega = stockActualBodega;
    }

    public double getSubtotal() {
        return cantidad * costoUnitario;
    }
}
