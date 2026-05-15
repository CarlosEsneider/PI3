package com.sigeiv.dao;

import com.sigeiv.modelo.Compra;
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
}





