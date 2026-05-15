package com.sigeiv.dao;

import com.sigeiv.modelo.DetalleCompra;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad DetalleCompra.
 * Implementa operaciones CRUD sobre la tabla 'detalle_compra'.
 */
public class DetalleCompraDAO {

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    public boolean insertar(DetalleCompra d) {
        String sql = "INSERT INTO detalle_compra (id_compra, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdCompra());
            ps.setInt(2, d.getIdProducto());
            ps.setInt(3, d.getCantidad());
            ps.setDouble(4, d.getPrecioUnitario());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) d.setIdDetalleCompra(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM detalle_compra WHERE id_detalle_compra = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Lista los detalles de una compra especifica */
    public List<DetalleCompra> listarPorCompra(int idCompra) {
        List<DetalleCompra> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_compra WHERE id_compra = ? ORDER BY id_detalle_compra";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Elimina todos los detalles de una compra */
    public boolean eliminarPorCompra(int idCompra) {
        String sql = "DELETE FROM detalle_compra WHERE id_compra = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private DetalleCompra mapear(ResultSet rs) throws SQLException {
        return new DetalleCompra(
            rs.getInt("id_detalle_compra"),
            rs.getInt("id_compra"),
            rs.getInt("id_producto"),
            rs.getInt("cantidad"),
            rs.getDouble("precio_unitario")
        );
    }
}





