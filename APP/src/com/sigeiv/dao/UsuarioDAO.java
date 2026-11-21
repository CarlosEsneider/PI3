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

    /**
     * Inserta un nuevo usuario.
     * Fase 2 - RF-02: incluye email y puede_autorizar en el INSERT.
     */
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario (nombre_usuario, username, contrasena, email, id_rol, puede_autorizar, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getContrasena());
            ps.setString(4, u.getEmail() != null ? u.getEmail() : "");
            ps.setInt(5, u.getIdRol());
            ps.setBoolean(6, u.isPuedeAutorizar());
            ps.setBoolean(7, u.isActivo());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) u.setIdUsuario(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Actualiza un usuario existente.
     * Fase 2 - RF-02: incluye email y puede_autorizar en el UPDATE.
     */
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET nombre_usuario=?, username=?, contrasena=?, email=?, id_rol=?, puede_autorizar=?, activo=? WHERE id_usuario=?";
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getContrasena());
            ps.setString(4, u.getEmail() != null ? u.getEmail() : "");
            ps.setInt(5, u.getIdRol());
            ps.setBoolean(6, u.isPuedeAutorizar());
            ps.setBoolean(7, u.isActivo());
            ps.setInt(8, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Borrado logico de un usuario (activo = false).
     * Fase 2 - RF-02: se reemplaza el DELETE fisico por borrado logico.
     */
    public boolean eliminar(int id) {
        String sql = "UPDATE usuario SET activo = 0 WHERE id_usuario = ?";
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

    /**
     * Mapea un ResultSet a un objeto Usuario.
     * Fase 2 - RF-02: lee email y puede_autorizar con fallback seguro.
     */
    private Usuario mapear(ResultSet rs) throws SQLException {
        String email = "";
        boolean puedeAutorizar = false;
        // Columnas opcionales: pueden no existir en BDs antiguas (SQLite del prototipo)
        try { email = rs.getString("email"); if (email == null) email = ""; } catch (SQLException ignored) {}
        try { puedeAutorizar = rs.getBoolean("puede_autorizar"); } catch (SQLException ignored) {}
        return new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("nombre_usuario"),
            rs.getString("username"),
            rs.getString("contrasena"),
            email,
            rs.getInt("id_rol"),
            puedeAutorizar,
            rs.getBoolean("activo")
        );
    }
}





