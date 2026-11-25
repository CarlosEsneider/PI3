package com.sigeiv.dao;

import com.sigeiv.modelo.Kardex;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para el Libro Mayor de Kardex (Fase 5 - RF-10, RNF-04).
 * Provee consultas inmutables de trazabilidad sobre la tabla 'kardex' con cruce
 * relacional hacia 'producto', 'movimiento_inventario' y 'usuario'.
 */
public class KardexDAO {

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    private static final String BASE_SELECT =
        "SELECT k.id_kardex, k.id_producto, p.nombre_producto, p.codigo_sku, " +
        "       k.fecha_hora, k.id_movimiento, k.tipo_evento, k.numero_soporte, " +
        "       k.cantidad_entrada, k.cantidad_salida, k.saldo_stock, " +
        "       k.costo_unitario, k.costo_total_saldo, " +
        "       COALESCE(u_rad.nombre_usuario, 'Sistema') AS registrado_por, " +
        "       COALESCE(u_aut.nombre_usuario, 'Automático / N/A') AS autorizado_por " +
        "FROM kardex k " +
        "INNER JOIN producto p ON k.id_producto = p.id_producto " +
        "LEFT JOIN movimiento_inventario m ON k.id_movimiento = m.id_movimiento " +
        "LEFT JOIN usuario u_rad ON m.id_usuario_creador = u_rad.id_usuario " +
        "LEFT JOIN usuario u_aut ON m.id_usuario_autorizador = u_aut.id_usuario ";

    /**
     * Mapea un ResultSet a un objeto Kardex con información relacional completa.
     */
    private Kardex mapearKardex(ResultSet rs) throws SQLException {
        Kardex k = new Kardex();
        k.setIdKardex(rs.getInt("id_kardex"));
        k.setIdProducto(rs.getInt("id_producto"));
        k.setNombreProducto(rs.getString("nombre_producto"));
        k.setCodigoSku(rs.getString("codigo_sku"));
        k.setFechaHora(rs.getString("fecha_hora"));
        k.setIdMovimiento(rs.getInt("id_movimiento"));
        k.setTipoEvento(rs.getString("tipo_evento"));
        k.setNumeroSoporte(rs.getString("numero_soporte"));
        k.setCantidadEntrada(rs.getInt("cantidad_entrada"));
        k.setCantidadSalida(rs.getInt("cantidad_salida"));
        k.setSaldoStock(rs.getInt("saldo_stock"));
        k.setCostoUnitario(rs.getDouble("costo_unitario"));
        k.setCostoTotalSaldo(rs.getDouble("costo_total_saldo"));
        k.setRegistradoPor(rs.getString("registrado_por"));
        k.setAutorizadoPor(rs.getString("autorizado_por"));
        return k;
    }

    /**
     * Obtiene el historial cronológico completo de asientos Kardex de un producto específico (RF-10).
     */
    public List<Kardex> listarKardexPorProducto(int idProducto) {
        List<Kardex> lista = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE k.id_producto = ? ORDER BY k.id_kardex ASC";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearKardex(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Lista todos los asientos de Kardex de todos los productos en orden descendente.
     */
    public List<Kardex> listarTodoKardex() {
        List<Kardex> lista = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY k.id_kardex DESC";
        try (Connection conn = conexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearKardex(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Filtra los asientos del Kardex por coincidencia en SKU o Nombre de Producto.
     */
    public List<Kardex> buscarPorSkuONombre(String filtro) {
        List<Kardex> lista = new ArrayList<>();
        String sql = BASE_SELECT +
                     "WHERE p.codigo_sku LIKE ? OR p.nombre_producto LIKE ? " +
                     "ORDER BY k.id_kardex DESC";
        String pattern = "%" + (filtro != null ? filtro.trim() : "") + "%";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearKardex(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Calcula la sumatoria acumulada de entradas físicas para un producto.
     */
    public int obtenerTotalEntradas(int idProducto) {
        String sql = "SELECT COALESCE(SUM(cantidad_entrada), 0) FROM kardex WHERE id_producto = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Calcula la sumatoria acumulada de salidas físicas para un producto.
     */
    public int obtenerTotalSalidas(int idProducto) {
        String sql = "SELECT COALESCE(SUM(cantidad_salida), 0) FROM kardex WHERE id_producto = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retorna la cantidad total de asientos inmutables en el Kardex.
     */
    public int contarAsientosTotales() {
        String sql = "SELECT COUNT(*) FROM kardex";
        try (Connection conn = conexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
