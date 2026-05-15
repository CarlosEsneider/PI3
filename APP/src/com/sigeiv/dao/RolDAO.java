package com.sigeiv.dao;

import com.sigeiv.modelo.Rol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Rol.
 * Implementa operaciones CRUD sobre la tabla 'rol'.
 * Usa PreparedStatement para prevenir inyeccion SQL.
 */
public class RolDAO {

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    /** Inserta un nuevo rol */
    public boolean insertar(Rol rol) {
        String sql = "INSERT INTO rol (nombre_rol) VALUES (?)";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rol.getNombreRol());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) rol.setIdRol(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Actualiza un rol existente */
    public boolean actualizar(Rol rol) {
        String sql = "UPDATE rol SET nombre_rol = ? WHERE id_rol = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rol.getNombreRol());
            ps.setInt(2, rol.getIdRol());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Elimina un rol por ID */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM rol WHERE id_rol = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Busca un rol por ID */
    public Rol buscarPorId(int id) {
        String sql = "SELECT * FROM rol WHERE id_rol = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Rol(rs.getInt("id_rol"), rs.getString("nombre_rol"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Lista todos los roles */
    public List<Rol> listarTodos() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM rol ORDER BY id_rol";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Rol(rs.getInt("id_rol"), rs.getString("nombre_rol")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}





