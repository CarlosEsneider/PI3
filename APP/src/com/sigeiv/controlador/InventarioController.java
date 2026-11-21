package com.sigeiv.controlador;

import com.sigeiv.dao.ProductoDAO;
import com.sigeiv.modelo.Producto;
import java.util.List;

/**
 * Controlador de logica de negocio para el Control y Auditoria de Inventario.
 * Calcula valorizaciones, estados criticos y metricas de stock.
 */
public class InventarioController {

    private final ProductoDAO productoDAO;

    public InventarioController() {
        this.productoDAO = new ProductoDAO();
    }

    public InventarioController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public List<Producto> listarInventario(boolean soloStockBajo) {
        if (soloStockBajo) {
            return productoDAO.productosStockBajo();
        }
        return productoDAO.listarTodos();
    }

    public int obtenerTotalProductos() {
        return productoDAO.listarTodos().size();
    }

    public int obtenerTotalStockBajo() {
        return productoDAO.productosStockBajo().size();
    }

    /**
     * Calcula la valorizacion total del inventario fisico en moneda local:
     * Sumatoria de (precio_unitario * stock_actual).
     */
    public double calcularValorTotalInventario() {
        double total = 0.0;
        List<Producto> productos = productoDAO.listarTodos();
        for (Producto p : productos) {
            total += (p.getPrecio() * p.getStockActual());
        }
        return total;
    }
}
