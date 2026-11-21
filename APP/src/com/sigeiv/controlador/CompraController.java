package com.sigeiv.controlador;

import com.sigeiv.dao.CompraDAO;
import com.sigeiv.dao.DetalleCompraDAO;
import com.sigeiv.dao.ProductoDAO;
import com.sigeiv.modelo.Compra;
import com.sigeiv.modelo.DetalleCompra;
import java.util.Date;
import java.util.List;

/**
 * Controlador de logica de negocio para el proceso de Compras y Abastecimiento.
 * Coordina la transaccionalidad ACID e incrementa existencias en bodega.
 */
public class CompraController {

    private final CompraDAO compraDAO;
    private final DetalleCompraDAO detalleCompraDAO;
    private final ProductoDAO productoDAO;

    public CompraController() {
        this.compraDAO = new CompraDAO();
        this.detalleCompraDAO = new DetalleCompraDAO();
        this.productoDAO = new ProductoDAO();
    }

    public CompraController(CompraDAO compraDAO, DetalleCompraDAO detalleCompraDAO, ProductoDAO productoDAO) {
        this.compraDAO = compraDAO;
        this.detalleCompraDAO = detalleCompraDAO;
        this.productoDAO = productoDAO;
    }

    /**
     * Procesa una compra a proveedor ejecutando la transaccion atomica e incrementando el stock.
     */
    public boolean procesarCompra(Compra compra, List<DetalleCompra> detalles) {
        if (compra == null || detalles == null || detalles.isEmpty()) {
            return false;
        }
        if (compra.getIdProveedor() <= 0 || compra.getIdUsuario() <= 0 || compra.getTotal() < 0) {
            return false;
        }
        for (DetalleCompra d : detalles) {
            if (d.getCantidad() <= 0 || d.getPrecioUnitario() < 0 || d.getIdProducto() <= 0) {
                return false;
            }
        }
        return compraDAO.registrarCompraTransaccional(compra, detalles);
    }

    public List<Compra> listarTodas() {
        return compraDAO.listarTodos();
    }

    public Compra buscarPorId(int id) {
        if (id <= 0) return null;
        return compraDAO.buscarPorId(id);
    }

    public List<Compra> buscarPorFechas(Date inicio, Date fin) {
        if (inicio == null || fin == null) return listarTodas();
        return compraDAO.buscarPorFechas(inicio, fin);
    }

    public List<DetalleCompra> listarDetallesPorCompra(int idCompra) {
        if (idCompra <= 0) return java.util.Collections.emptyList();
        return detalleCompraDAO.listarPorCompra(idCompra);
    }
}
