package com.sigeiv.modelo;

/**
 * Clase que representa un Producto en el inventario de SIGEIV-Volcano.
 * Corresponde a la tabla 'producto' de la base de datos.
 * 
 * Atributos Fase 2 (RF-04): codigoSku, dimensiones, costoEstandar, activo
 */
public class Producto {

    // --- Atributos ---
    private int idProducto;
    private String codigoSku;
    private String nombreProducto;
    private String dimensiones;
    private double costoEstandar;
    private double precio;
    private int stockActual;
    private int stockMinimo;
    private int idCategoria;
    private int idProveedor;
    private String imgUrl;
    private boolean activo = true;

    // --- Constructores ---

    /** Constructor vacio */
    public Producto() {}

    /** Constructor original con 8 atributos (compatibilidad Fase 1) */
    public Producto(int idProducto, String nombreProducto, double precio,
                    int stockActual, int stockMinimo, int idCategoria,
                    int idProveedor, String imgUrl) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precio = precio;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        this.imgUrl = imgUrl;
        this.codigoSku = "";
        this.dimensiones = "";
        this.costoEstandar = 0.0;
        this.activo = true;
    }

    /** Constructor completo con todos los atributos de manufactura (Fase 2) */
    public Producto(int idProducto, String codigoSku, String nombreProducto,
                    String dimensiones, double costoEstandar, double precio,
                    int stockActual, int stockMinimo, int idCategoria,
                    int idProveedor, String imgUrl, boolean activo) {
        this.idProducto = idProducto;
        this.codigoSku = codigoSku;
        this.nombreProducto = nombreProducto;
        this.dimensiones = dimensiones;
        this.costoEstandar = costoEstandar;
        this.precio = precio;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        this.imgUrl = imgUrl;
        this.activo = activo;
    }

    // --- Getters ---

    public int getIdProducto() {
        return idProducto;
    }

    public String getCodigoSku() {
        return codigoSku != null ? codigoSku : "";
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getDimensiones() {
        return dimensiones != null ? dimensiones : "";
    }

    public double getCostoEstandar() {
        return costoEstandar;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStockActual() {
        return stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public boolean isActivo() {
        return activo;
    }

    // --- Setters ---

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public void setCostoEstandar(double costoEstandar) {
        this.costoEstandar = costoEstandar;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Verifica si el producto tiene stock por debajo del minimo.
     * @return true si stock_actual <= stock_minimo
     */
    public boolean isStockBajo() {
        return stockActual <= stockMinimo;
    }

    @Override
    public String toString() {
        String sku = (codigoSku != null && !codigoSku.isEmpty()) ? "[" + codigoSku + "] " : "";
        return sku + nombreProducto + " ($" + String.format("%.2f", precio) + ")";
    }
}
