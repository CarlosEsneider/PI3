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

    /**
     * Determina el estado de semaforización tricolor oficial (Fase 5 - RF-11):
     * - VERDE (ÓPTIMO): Stock > Stock Mínimo * 1.5
     * - AMARILLO (PREVENTIVO): Stock Mínimo < Stock <= Stock Mínimo * 1.5
     * - ROJO (CRÍTICO / AGOTADO): Stock <= Stock Mínimo o Stock = 0
     */
    public String determinarEstadoSemaforo(Producto p) {
        if (p == null) return "DESCONOCIDO";
        int stock = p.getStockActual();
        int min = p.getStockMinimo();
        if (stock <= min || stock <= 0) {
            return "CRITICO";
        } else if (stock <= min * 1.5) {
            return "PREVENTIVO";
        } else {
            return "OPTIMO";
        }
    }

    /**
     * Cuenta el total de productos en estado Óptimo (Verde).
     */
    public int obtenerTotalOptimo() {
        int count = 0;
        for (Producto p : productoDAO.listarTodos()) {
            if ("OPTIMO".equals(determinarEstadoSemaforo(p))) count++;
        }
        return count;
    }

    /**
     * Cuenta el total de productos en estado Preventivo (Amarillo).
     */
    public int obtenerTotalPreventivo() {
        int count = 0;
        for (Producto p : productoDAO.listarTodos()) {
            if ("PREVENTIVO".equals(determinarEstadoSemaforo(p))) count++;
        }
        return count;
    }

    /**
     * Cuenta el total de productos en estado Crítico / Agotado (Rojo).
     */
    public int obtenerTotalCritico() {
        int count = 0;
        for (Producto p : productoDAO.listarTodos()) {
            if ("CRITICO".equals(determinarEstadoSemaforo(p))) count++;
        }
        return count;
    }

    /**
     * Filtra el inventario por estado del semáforo: "TODOS", "OPTIMO", "PREVENTIVO", "CRITICO".
     */
    public java.util.List<Producto> filtrarPorSemaforo(String estadoFiltro) {
        if (estadoFiltro == null || "TODOS".equalsIgnoreCase(estadoFiltro)) {
            return productoDAO.listarTodos();
        }
        java.util.List<Producto> filtrados = new java.util.ArrayList<>();
        for (Producto p : productoDAO.listarTodos()) {
            if (estadoFiltro.equalsIgnoreCase(determinarEstadoSemaforo(p))) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }
}
