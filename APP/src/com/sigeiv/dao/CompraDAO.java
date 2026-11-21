package com.sigeiv.dao;

import com.sigeiv.modelo.Compra;
import com.sigeiv.modelo.DetalleCompra;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Compra.
 * Implementa operaciones CRUD y consultas sobre la tabla 'compra'.
 */
public class CompraDAO {

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    /** Inserta una nueva compra y devuelve el ID generado */
    public boolean insertar(Compra c) {
        String sql = "INSERT INTO compra (fecha, total, id_proveedor, id_usuario) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new Timestamp(c.getFecha().getTime()));
            ps.setDouble(2, c.getTotal());
            ps.setInt(3, c.getIdProveedor());
            ps.setInt(4, c.getIdUsuario());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) c.setIdCompra(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean actualizar(Compra c) {
        String sql = "UPDATE compra SET fecha=?, total=?, id_proveedor=?, id_usuario=? WHERE id_compra=?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(c.getFecha().getTime()));
            ps.setDouble(2, c.getTotal());
            ps.setInt(3, c.getIdProveedor());
            ps.setInt(4, c.getIdUsuario());
            ps.setInt(5, c.getIdCompra());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM compra WHERE id_compra = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Compra buscarPorId(int id) {
        String sql = "SELECT * FROM compra WHERE id_compra = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Compra> listarTodos() {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT * FROM compra ORDER BY fecha DESC";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Compras por rango de fechas (para reportes) */
    public List<Compra> buscarPorFechas(java.util.Date inicio, java.util.Date fin) {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT * FROM compra WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new Timestamp(fin.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Total de compras en un rango de fechas */
    public double totalComprasPorFechas(java.util.Date inicio, java.util.Date fin) {
        String sql = "SELECT COALESCE(SUM(total), 0) AS total FROM compra WHERE fecha BETWEEN ? AND ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new Timestamp(fin.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Compra mapear(ResultSet rs) throws SQLException {
        return new Compra(
            rs.getInt("id_compra"),
            rs.getTimestamp("fecha"),
            rs.getDouble("total"),
            rs.getInt("id_proveedor"),
            rs.getInt("id_usuario")
        );
    }

    /**
     * Registra una compra completa con sus detalles e incrementa el stock de inventario
     * de forma atomica dentro de una transaccion ACID.
     * @param c Objeto Compra
     * @param detalles Lista de DetalleCompra
     * @return true si la transaccion se completo exitosamente
     */
    public boolean registrarCompraTransaccional(Compra c, List<DetalleCompra> detalles) {
        if (detalles == null || detalles.isEmpty()) return false;
        
        String sqlCompra = "INSERT INTO compra (fecha, total, id_proveedor, id_usuario) VALUES (?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_compra (id_compra, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        String sqlStock = "UPDATE producto SET stock_actual = stock_actual + ? WHERE id_producto = ?";
        
        Connection conn = null;
        boolean originalAutoCommit = true;
        try {
            conn = conexionDB.getConexion();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            
            // 1. Insertar cabecera de compra
            int idCompraGenerada;
            try (PreparedStatement psCompra = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
                psCompra.setTimestamp(1, new Timestamp(c.getFecha().getTime()));
                psCompra.setDouble(2, c.getTotal());
                psCompra.setInt(3, c.getIdProveedor());
                psCompra.setInt(4, c.getIdUsuario());
                int filas = psCompra.executeUpdate();
                if (filas == 0) {
                    conn.rollback();
                    return false;
                }
                try (ResultSet rs = psCompra.getGeneratedKeys()) {
                    if (rs.next()) {
                        idCompraGenerada = rs.getInt(1);
                        c.setIdCompra(idCompraGenerada);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            // 2. Insertar detalles y 3. Incrementar stock
            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                 PreparedStatement psStock = conn.prepareStatement(sqlStock)) {
                
                for (DetalleCompra det : detalles) {
                    det.setIdCompra(idCompraGenerada);
                    psDetalle.setInt(1, idCompraGenerada);
                    psDetalle.setInt(2, det.getIdProducto());
                    psDetalle.setInt(3, det.getCantidad());
                    psDetalle.setDouble(4, det.getPrecioUnitario());
                    psDetalle.executeUpdate();
                    
                    psStock.setInt(1, det.getCantidad());
                    psStock.setInt(2, det.getIdProducto());
                    psStock.executeUpdate();
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(originalAutoCommit); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}





