package com.sigeiv.dao;

import com.sigeiv.modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Producto.
 * Implementa operaciones CRUD y busquedas avanzadas sobre la tabla 'producto'.
 */
public class ProductoDAO {

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    public boolean insertar(Producto p) {
        String sql = "INSERT INTO producto (nombre_producto, precio, stock_actual, stock_minimo, id_categoria, id_proveedor, img_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombreProducto());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStockActual());
            ps.setInt(4, p.getStockMinimo());
            ps.setInt(5, p.getIdCategoria());
            ps.setInt(6, p.getIdProveedor());
            ps.setString(7, p.getImgUrl());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) p.setIdProducto(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean actualizar(Producto p) {
        String sql = "UPDATE producto SET nombre_producto=?, precio=?, stock_actual=?, stock_minimo=?, id_categoria=?, id_proveedor=?, img_url=? WHERE id_producto=?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombreProducto());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStockActual());
            ps.setInt(4, p.getStockMinimo());
            ps.setInt(5, p.getIdCategoria());
            ps.setInt(6, p.getIdProveedor());
            ps.setString(7, p.getImgUrl());
            ps.setInt(8, p.getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM producto WHERE id_producto = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Producto buscarPorId(int id) {
        String sql = "SELECT * FROM producto WHERE id_producto = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto ORDER BY nombre_producto";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Busca productos por nombre (parcial) */
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE nombre_producto LIKE ? ORDER BY nombre_producto";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Filtra productos por categoria */
    public List<Producto> buscarPorCategoria(int idCategoria) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE id_categoria = ? ORDER BY nombre_producto";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Filtra productos por proveedor */
    public List<Producto> buscarPorProveedor(int idProveedor) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE id_proveedor = ? ORDER BY nombre_producto";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Lista productos con stock bajo (stock_actual <= stock_minimo) */
    public List<Producto> productosStockBajo() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE stock_actual <= stock_minimo ORDER BY stock_actual ASC";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Actualiza el stock de un producto (descuenta cantidad vendida) */
    public boolean actualizarStock(int idProducto, int cantidadVendida) {
        String sql = "UPDATE producto SET stock_actual = stock_actual - ? WHERE id_producto = ? AND stock_actual >= ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidadVendida);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidadVendida);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Incrementa el stock de un producto (agrega cantidad comprada) */
    public boolean actualizarStockCompra(int idProducto, int cantidadComprada) {
        String sql = "UPDATE producto SET stock_actual = stock_actual + ? WHERE id_producto = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidadComprada);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id_producto"),
            rs.getString("nombre_producto"),
            rs.getDouble("precio"),
            rs.getInt("stock_actual"),
            rs.getInt("stock_minimo"),
            rs.getInt("id_categoria"),
            rs.getInt("id_proveedor"),
            rs.getString("img_url")
        );
    }
}





