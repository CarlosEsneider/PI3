package com.sigeiv.controlador;

import com.sigeiv.dao.ClienteDAO;
import com.sigeiv.dao.DetalleVentaDAO;
import com.sigeiv.dao.ProductoDAO;
import com.sigeiv.dao.VentaDAO;
import com.sigeiv.modelo.DetalleVenta;
import com.sigeiv.modelo.Producto;
import com.sigeiv.modelo.Venta;
import java.util.*;

/**
 * Controlador de logica de negocio para Reportes Comerciales y KPIs.
 */
public class ReporteController {

    private final VentaDAO ventaDAO;
    private final DetalleVentaDAO detalleVentaDAO;
    private final ProductoDAO productoDAO;
    private final ClienteDAO clienteDAO;

    public ReporteController() {
        this.ventaDAO = new VentaDAO();
        this.detalleVentaDAO = new DetalleVentaDAO();
        this.productoDAO = new ProductoDAO();
        this.clienteDAO = new ClienteDAO();
    }

    public List<Venta> obtenerVentasPorRango(Date inicio, Date fin) {
        if (inicio == null || fin == null) return ventaDAO.listarTodos();
        return ventaDAO.buscarPorFechas(inicio, fin);
    }

    public double calcularTotalIngresos(Date inicio, Date fin) {
        return ventaDAO.totalVentasPorFechas(inicio, fin);
    }

    public int contarTotalVentas(Date inicio, Date fin) {
        return ventaDAO.contarVentasPorFechas(inicio, fin);
    }

    public double calcularTicketPromedio(Date inicio, Date fin) {
        int cantidad = contarTotalVentas(inicio, fin);
        if (cantidad <= 0) return 0.0;
        double total = calcularTotalIngresos(inicio, fin);
        return total / cantidad;
    }

    /**
     * Devuelve los productos mas vendidos a traves de la consulta agregada optimizada en base de datos.
     */
    public List<Object[]> obtenerTopProductos(int limite) {
        if (limite <= 0) limite = 10;
        return detalleVentaDAO.productosMasVendidos(limite);
    }
}
