package com.sigeiv.dao;

import com.sigeiv.modelo.Venta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Venta.
 * Implementa operaciones CRUD y consultas de reportes.
 */
public class VentaDAO {

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    /** Inserta una nueva venta y devuelve el ID generado */
    public boolean insertar(Venta v) {
        String sql = "INSERT INTO venta (fecha, id_cliente, id_usuario, total) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new Timestamp(v.getFecha().getTime()));
            ps.setInt(2, v.getIdCliente());
            ps.setInt(3, v.getIdUsuario());
            ps.setDouble(4, v.getTotal());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) v.setIdVenta(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean actualizar(Venta v) {
        String sql = "UPDATE venta SET fecha=?, id_cliente=?, id_usuario=?, total=? WHERE id_venta=?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(v.getFecha().getTime()));
            ps.setInt(2, v.getIdCliente());
            ps.setInt(3, v.getIdUsuario());
            ps.setDouble(4, v.getTotal());
            ps.setInt(5, v.getIdVenta());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM venta WHERE id_venta = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Venta buscarPorId(int id) {
        String sql = "SELECT * FROM venta WHERE id_venta = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Venta> listarTodos() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM venta ORDER BY fecha DESC";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Ventas por rango de fechas (para reportes) */
    public List<Venta> buscarPorFechas(java.util.Date inicio, java.util.Date fin) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM venta WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new Timestamp(fin.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Total de ventas en un rango de fechas */
    public double totalVentasPorFechas(java.util.Date inicio, java.util.Date fin) {
        String sql = "SELECT COALESCE(SUM(total), 0) AS total FROM venta WHERE fecha BETWEEN ? AND ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new Timestamp(fin.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Cuenta ventas por rango de fechas */
    public int contarVentasPorFechas(java.util.Date inicio, java.util.Date fin) {
        String sql = "SELECT COUNT(*) AS cantidad FROM venta WHERE fecha BETWEEN ? AND ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new Timestamp(fin.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cantidad");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Venta mapear(ResultSet rs) throws SQLException {
        return new Venta(
            rs.getInt("id_venta"),
            rs.getTimestamp("fecha"),
            rs.getInt("id_cliente"),
            rs.getInt("id_usuario"),
            rs.getDouble("total")
        );
    }
}





