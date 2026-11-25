package com.sigeiv.controlador;

import com.sigeiv.dao.KardexDAO;
import com.sigeiv.dao.ProductoDAO;
import com.sigeiv.modelo.Kardex;
import com.sigeiv.modelo.Producto;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de lógica de negocio para el Visor de Kardex y Trazabilidad (Fase 5 - RF-10, RF-11).
 * Implementa el patrón MVC desacoplando las vistas Swing de las consultas SQL inmutables.
 */
public class KardexController {

    private final KardexDAO kardexDAO;
    private final ProductoDAO productoDAO;

    public KardexController() {
        this.kardexDAO = new KardexDAO();
        this.productoDAO = new ProductoDAO();
    }

    public KardexController(KardexDAO kardexDAO, ProductoDAO productoDAO) {
        this.kardexDAO = kardexDAO;
        this.productoDAO = productoDAO;
    }

    /**
     * Consulta el historial cronológico oficial de asientos en Kardex para un producto dado (RF-10).
     */
    public List<Kardex> listarKardexPorProducto(int idProducto) {
        if (idProducto <= 0) {
            return new ArrayList<>();
        }
        return kardexDAO.listarKardexPorProducto(idProducto);
    }

    /**
     * Lista todos los registros del libro mayor de Kardex en orden descendente.
     */
    public List<Kardex> listarTodoKardex() {
        return kardexDAO.listarTodoKardex();
    }

    /**
     * Busca asientos de Kardex por coincidencia de SKU o nombre de producto.
     */
    public List<Kardex> buscarPorSkuONombre(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return kardexDAO.listarTodoKardex();
        }
        return kardexDAO.buscarPorSkuONombre(filtro.trim());
    }

    /**
     * Obtiene el acumulado total de unidades que han ingresado para un producto (Producción, Devoluciones, Ajustes +).
     */
    public int obtenerTotalEntradas(int idProducto) {
        if (idProducto <= 0) return 0;
        return kardexDAO.obtenerTotalEntradas(idProducto);
    }

    /**
     * Obtiene el acumulado total de unidades que han salido para un producto (Ventas, Bajas, Ajustes -).
     */
    public int obtenerTotalSalidas(int idProducto) {
        if (idProducto <= 0) return 0;
        return kardexDAO.obtenerTotalSalidas(idProducto);
    }

    /**
     * Obtiene el saldo de stock actual registrado en la última línea del Kardex o en el catálogo del producto.
     */
    public int obtenerSaldoActual(int idProducto) {
        List<Kardex> movimientos = kardexDAO.listarKardexPorProducto(idProducto);
        if (!movimientos.isEmpty()) {
            return movimientos.get(movimientos.size() - 1).getSaldoStock();
        }
        Producto p = productoDAO.buscarPorId(idProducto);
        return p != null ? p.getStockActual() : 0;
    }

    /**
     * Calcula la valorización contable del saldo en inventario para el producto en el Kardex.
     */
    public double calcularValorizacionSaldo(int idProducto) {
        List<Kardex> movimientos = kardexDAO.listarKardexPorProducto(idProducto);
        if (!movimientos.isEmpty()) {
            return movimientos.get(movimientos.size() - 1).getCostoTotalSaldo();
        }
        Producto p = productoDAO.buscarPorId(idProducto);
        return p != null ? (p.getStockActual() * p.getPrecio()) : 0.0;
    }

    /**
     * Retorna la lista de todos los productos disponibles para poblar el selector de Kardex.
     */
    public List<Producto> listarProductosDisponibles() {
        return productoDAO.listarTodos();
    }

    /**
     * Retorna el conteo total de asientos inmutables en el Kardex.
     */
    public int contarAsientosTotales() {
        return kardexDAO.contarAsientosTotales();
    }

    /**
     * Calcula el índice de rotación del producto: Unidades Salientes / (Saldo Actual + 1).
     */
    public double calcularIndiceRotacion(int idProducto) {
        int salidas = obtenerTotalSalidas(idProducto);
        int saldo = obtenerSaldoActual(idProducto);
        if (saldo == 0 && salidas == 0) return 0.0;
        return (double) salidas / (Math.max(saldo, 1));
    }
}
