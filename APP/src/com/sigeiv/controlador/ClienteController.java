package com.sigeiv.controlador;

import com.sigeiv.dao.ClienteDAO;
import com.sigeiv.modelo.Cliente;
import java.util.List;

/**
 * Controlador de logica de negocio para Gestion de Clientes.
 * Valida formatos, duplicados de DNI y gestiona la persistencia.
 */
public class ClienteController {

    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    public Cliente buscarPorId(int id) {
        if (id <= 0) return null;
        return clienteDAO.buscarPorId(id);
    }

    public Cliente buscarPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) return null;
        return clienteDAO.buscarPorDni(dni.trim());
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return listarTodos();
        }
        return clienteDAO.buscarPorNombre(nombre.trim());
    }

    public boolean guardar(Cliente c) {
        if (!validarCliente(c)) return false;
        // Validar que el DNI no exista previamente
        if (c.getDni() != null && !c.getDni().trim().isEmpty()) {
            Cliente existente = clienteDAO.buscarPorDni(c.getDni().trim());
            if (existente != null) {
                return false; // DNI ya registrado
            }
        }
        return clienteDAO.insertar(c);
    }

    public boolean actualizar(Cliente c) {
        if (c == null || c.getIdCliente() <= 0) return false;
        if (!validarCliente(c)) return false;
        return clienteDAO.actualizar(c);
    }

    public boolean eliminar(int id) {
        if (id <= 0) return false;
        return clienteDAO.eliminar(id);
    }

    private boolean validarCliente(Cliente c) {
        if (c == null) return false;
        if (c.getNombreCliente() == null || c.getNombreCliente().trim().isEmpty()) return false;
        return true;
    }
}
