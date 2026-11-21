package com.sigeiv.controlador;

import com.sigeiv.dao.ProveedorDAO;
import com.sigeiv.modelo.Proveedor;
import java.util.List;

/**
 * Controlador de logica de negocio para Gestion de Proveedores.
 */
public class ProveedorController {

    private final ProveedorDAO proveedorDAO;

    public ProveedorController() {
        this.proveedorDAO = new ProveedorDAO();
    }

    public ProveedorController(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    public List<Proveedor> listarTodos() {
        return proveedorDAO.listarTodos();
    }

    public Proveedor buscarPorId(int id) {
        if (id <= 0) return null;
        return proveedorDAO.buscarPorId(id);
    }

    public List<Proveedor> buscarPorEmpresa(String empresa) {
        if (empresa == null || empresa.trim().isEmpty()) {
            return listarTodos();
        }
        return proveedorDAO.buscarPorNombre(empresa.trim());
    }

    public boolean guardar(Proveedor p) {
        if (p == null || p.getEmpresa() == null || p.getEmpresa().trim().isEmpty()) return false;
        return proveedorDAO.insertar(p);
    }

    public boolean actualizar(Proveedor p) {
        if (p == null || p.getIdProveedor() <= 0) return false;
        if (p.getEmpresa() == null || p.getEmpresa().trim().isEmpty()) return false;
        return proveedorDAO.actualizar(p);
    }

    public boolean eliminar(int id) {
        if (id <= 0) return false;
        return proveedorDAO.eliminar(id);
    }
}
