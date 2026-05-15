package com.sigeiv.modelo;

/**
 * Clase que representa un Producto en el inventario de SIGEIV-Volcano.
 * Corresponde a la tabla 'producto' de la base de datos.
 * 
 * Entrada: id_producto, nombre_producto, precio, stock_actual, stock_minimo,
 *          id_categoria, id_proveedor, img_url
 * Proceso: Encapsula datos del producto y control de inventario
 * Salida: Objeto Producto con capacidad de alertar stock bajo
 */
public class Producto {

    // --- Atributos ---
    private int idProducto;
    private String nombreProducto;
    private double precio;
    private int stockActual;
    private int stockMinimo;
    private int idCategoria;
    private int idProveedor;
    private String imgUrl;

    // --- Constructores ---

    /** Constructor vacio */
    public Producto() {}

    /** Constructor con todos los atributos */
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
    }

    // --- Getters ---

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
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

    // --- Setters ---

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
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

    /**
     * Verifica si el producto tiene stock por debajo del minimo.
     * @return true si stock_actual <= stock_minimo
     */
    public boolean isStockBajo() {
        return stockActual <= stockMinimo;
    }

    @Override
    public String toString() {
        return nombreProducto + " ($" + String.format("%.2f", precio) + ")";
    }
}





