package com.sigeiv.dao;

import com.sigeiv.modelo.DetalleVenta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad DetalleVenta.
 * Implementa operaciones CRUD sobre la tabla 'detalle_venta'.
 */
public class DetalleVentaDAO {

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    public boolean insertar(DetalleVenta d) {
        String sql = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdVenta());
            ps.setInt(2, d.getIdProducto());
            ps.setInt(3, d.getCantidad());
            ps.setDouble(4, d.getPrecioUnitario());
            ps.setDouble(5, d.getSubtotal());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) d.setIdDetalle(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM detalle_venta WHERE id_detalle = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Lista los detalles de una venta especifica */
    public List<DetalleVenta> listarPorVenta(int idVenta) {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_venta WHERE id_venta = ? ORDER BY id_detalle";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Elimina todos los detalles de una venta */
    public boolean eliminarPorVenta(int idVenta) {
        String sql = "DELETE FROM detalle_venta WHERE id_venta = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Productos mas vendidos (para reportes) */
    public List<Object[]> productosMasVendidos(int limite) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT p.nombre_producto, SUM(dv.cantidad) AS total_vendido, SUM(dv.subtotal) AS total_ingresos " +
                     "FROM detalle_venta dv INNER JOIN producto p ON dv.id_producto = p.id_producto " +
                     "GROUP BY p.id_producto, p.nombre_producto ORDER BY total_vendido DESC LIMIT ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("nombre_producto"),
                    rs.getInt("total_vendido"),
                    rs.getDouble("total_ingresos")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private DetalleVenta mapear(ResultSet rs) throws SQLException {
        return new DetalleVenta(
            rs.getInt("id_detalle"),
            rs.getInt("id_venta"),
            rs.getInt("id_producto"),
            rs.getInt("cantidad"),
            rs.getDouble("precio_unitario"),
            rs.getDouble("subtotal")
        );
    }
}





