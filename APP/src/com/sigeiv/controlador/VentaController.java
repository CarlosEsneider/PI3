package com.sigeiv.controlador;

import com.sigeiv.dao.DetalleVentaDAO;
import com.sigeiv.dao.ProductoDAO;
import com.sigeiv.dao.VentaDAO;
import com.sigeiv.modelo.DetalleVenta;
import com.sigeiv.modelo.Producto;
import com.sigeiv.modelo.Venta;
import java.util.Date;
import java.util.List;

/**
 * Controlador de logica de negocio para el proceso de Ventas y Facturacion.
 * Coordina la transaccionalidad ACID y valida disponibilidad de stock.
 */
public class VentaController {

    private final VentaDAO ventaDAO;
    private final DetalleVentaDAO detalleVentaDAO;
    private final ProductoDAO productoDAO;

    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.detalleVentaDAO = new DetalleVentaDAO();
        this.productoDAO = new ProductoDAO();
    }

    public VentaController(VentaDAO ventaDAO, DetalleVentaDAO detalleVentaDAO, ProductoDAO productoDAO) {
        this.ventaDAO = ventaDAO;
        this.detalleVentaDAO = detalleVentaDAO;
        this.productoDAO = productoDAO;
    }

    /**
     * Procesa una venta validando stock previo y ejecutando la transaccion atomica.
     * @param venta Cabecera de la venta
     * @param detalles Renglones de detalle con producto, cantidad y precio
     * @return true si la venta se registro y el stock se desconto exitosamente
     */
    public boolean procesarVenta(Venta venta, List<DetalleVenta> detalles) {
        if (venta == null || detalles == null || detalles.isEmpty()) {
            return false;
        }
        if (venta.getIdCliente() <= 0 || venta.getIdUsuario() <= 0 || venta.getTotal() < 0) {
            return false;
        }

        // Verificacion preventiva de stock antes de iniciar la transaccion
        for (DetalleVenta d : detalles) {
            if (d.getCantidad() <= 0) return false;
            Producto p = productoDAO.buscarPorId(d.getIdProducto());
            if (p == null || p.getStockActual() < d.getCantidad()) {
                return false; // Stock insuficiente detectado
            }
        }

        return ventaDAO.registrarVentaTransaccional(venta, detalles);
    }

    public List<Venta> listarTodas() {
        return ventaDAO.listarTodos();
    }

    public Venta buscarPorId(int id) {
        if (id <= 0) return null;
        return ventaDAO.buscarPorId(id);
    }

    public List<Venta> buscarPorFechas(Date inicio, Date fin) {
        if (inicio == null || fin == null) return listarTodas();
        return ventaDAO.buscarPorFechas(inicio, fin);
    }

    public List<DetalleVenta> listarDetallesPorVenta(int idVenta) {
        if (idVenta <= 0) return java.util.Collections.emptyList();
        return detalleVentaDAO.listarPorVenta(idVenta);
    }

    public double totalVentasPorFechas(Date inicio, Date fin) {
        if (inicio == null || fin == null) return 0;
        return ventaDAO.totalVentasPorFechas(inicio, fin);
    }

    public int contarVentasPorFechas(Date inicio, Date fin) {
        if (inicio == null || fin == null) return 0;
        return ventaDAO.contarVentasPorFechas(inicio, fin);
    }
}
