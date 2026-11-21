package com.sigeiv.controlador;

import com.sigeiv.dao.CategoriaDAO;
import com.sigeiv.modelo.Categoria;
import java.util.List;

/**
 * Controlador de logica de negocio para Gestion de Categorias de productos.
 */
public class CategoriaController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public CategoriaController(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public List<Categoria> listarTodos() {
        return categoriaDAO.listarTodos();
    }

    public Categoria buscarPorId(int id) {
        if (id <= 0) return null;
        return categoriaDAO.buscarPorId(id);
    }

    public boolean guardar(Categoria c) {
        if (c == null || c.getNombreCategoria() == null || c.getNombreCategoria().trim().isEmpty()) return false;
        return categoriaDAO.insertar(c);
    }

    public boolean actualizar(Categoria c) {
        if (c == null || c.getIdCategoria() <= 0) return false;
        if (c.getNombreCategoria() == null || c.getNombreCategoria().trim().isEmpty()) return false;
        return categoriaDAO.actualizar(c);
    }

    public boolean eliminar(int id) {
        if (id <= 0) return false;
        return categoriaDAO.eliminar(id);
    }
}
