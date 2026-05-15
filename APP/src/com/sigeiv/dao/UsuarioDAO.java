package com.sigeiv.dao;

import com.sigeiv.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Usuario.
 * Implementa operaciones CRUD sobre la tabla 'usuario'.
 * Usa PreparedStatement para prevenir inyeccion SQL.
 */
public class UsuarioDAO {

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    /** Inserta un nuevo usuario */
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario (nombre_usuario, username, contrasena, id_rol, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getContrasena());
            ps.setInt(4, u.getIdRol());
            ps.setBoolean(5, u.isActivo());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) u.setIdUsuario(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Actualiza un usuario existente */
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET nombre_usuario=?, username=?, contrasena=?, id_rol=?, activo=? WHERE id_usuario=?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getContrasena());
            ps.setInt(4, u.getIdRol());
            ps.setBoolean(5, u.isActivo());
            ps.setInt(6, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Elimina un usuario por ID */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Busca un usuario por ID */
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Busca un usuario por username (para login) */
    public Usuario buscarPorUsername(String username) {
        String sql = "SELECT * FROM usuario WHERE username = ? AND activo = 1";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Lista todos los usuarios */
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY id_usuario";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /** Mapea un ResultSet a un objeto Usuario */
    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("nombre_usuario"),
            rs.getString("username"),
            rs.getString("contrasena"),
            rs.getInt("id_rol"),
            rs.getBoolean("activo")
        );
    }
}





