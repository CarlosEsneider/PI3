package com.sigeiv.controlador;

import com.sigeiv.dao.ProductoDAO;
import com.sigeiv.modelo.Producto;
import java.util.List;

/**
 * Controlador de logica de negocio para Gestion de Productos e Inventario.
 * Intermedia entre las Vistas (ProductoPanel, InventarioPanel) y el modelo/DAO.
 */
public class ProductoController {

    private final ProductoDAO productoDAO;

    public ProductoController() {
        this.productoDAO = new ProductoDAO();
    }

    public ProductoController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public List<Producto> listarTodos() {
        return productoDAO.listarTodos();
    }

    public Producto buscarPorId(int id) {
        if (id <= 0) return null;
        return productoDAO.buscarPorId(id);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarTodos();
        }
        return productoDAO.buscarPorNombre(nombre.trim());
    }

    public List<Producto> buscarPorCategoria(int idCategoria) {
        if (idCategoria <= 0) return listarTodos();
        return productoDAO.buscarPorCategoria(idCategoria);
    }

    public List<Producto> buscarPorProveedor(int idProveedor) {
        if (idProveedor <= 0) return listarTodos();
        return productoDAO.buscarPorProveedor(idProveedor);
    }

    public List<Producto> listarStockBajo() {
        return productoDAO.productosStockBajo();
    }

    /**
     * Valida las reglas de negocio y registra un nuevo producto.
     */
    public boolean guardar(Producto p) {
        if (!validarProducto(p)) return false;
        return productoDAO.insertar(p);
    }

    /**
     * Valida las reglas de negocio y actualiza un producto existente.
     */
    public boolean actualizar(Producto p) {
        if (p == null || p.getIdProducto() <= 0) return false;
        if (!validarProducto(p)) return false;
        return productoDAO.actualizar(p);
    }

    public boolean eliminar(int id) {
        if (id <= 0) return false;
        return productoDAO.eliminar(id);
    }

    private boolean validarProducto(Producto p) {
        if (p == null) return false;
        if (p.getNombreProducto() == null || p.getNombreProducto().trim().isEmpty()) return false;
        if (p.getPrecio() < 0) return false;
        if (p.getStockActual() < 0) return false;
        if (p.getStockMinimo() < 0) return false;
        if (p.getIdCategoria() <= 0) return false;
        if (p.getIdProveedor() <= 0) return false;
        return true;
    }
}
