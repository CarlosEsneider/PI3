package com.sigeiv.dao;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase Singleton para gestionar la conexion a la base de datos SQLite.
 * Utiliza el patron Singleton para asegurar una unica instancia de conexion.
 * 
 * Entrada: Parametros de conexion SQLite
 * Proceso: Establece y gestiona la conexion JDBC con SQLite y asegura la creacion de tablas
 * Salida: Objeto Connection reutilizable
 */
public class ConexionDB {

    // --- Parametros de conexion SQLite ---
    private static final String URL = "jdbc:sqlite:sigeiv_volcano.db";

    // --- Instancia Singleton ---
    private static ConexionDB instancia;
    private Connection conexion;

    /**
     * Constructor privado (Singleton).
     * Carga el driver JDBC de SQLite e inicializa la BD si es necesario.
     */
    private ConexionDB() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontro el driver SQLite JDBC.");
            System.err.println("Asegurate de que sqlite-jdbc-*.jar este en el classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la instancia unica de ConexionDB.
     * @return Instancia de ConexionDB
     */
    public static synchronized ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
            instancia.inicializarBaseDeDatos();
        }
        return instancia;
    }

    /**
     * Obtiene la conexion a la base de datos.
     * Si la conexion esta cerrada o es nula, crea una nueva.
     * Siempre habilita PRAGMA foreign_keys = ON; al abrir.
     * @return Objeto Connection activo
     * @throws SQLException si ocurre un error de conexion
     */
    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL);
            try (Statement stmt = conexion.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return conexion;
    }

    /**
     * Inicializa la base de datos si las tablas no existen.
     */
    private void inicializarBaseDeDatos() {
        try (Connection conn = getConexion();
             Statement stmt = conn.createStatement()) {
            
            // Verificar si la tabla 'usuario' existe
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='usuario'");
            if (!rs.next()) {
                System.out.println("Inicializando base de datos SQLite por primera vez...");
                rs.close();
                
                // Leer el archivo sql/sqlite_schema.sql
                File file = new File("sql/sqlite_schema.sql");
                if (file.exists()) {
                    String script = new String(Files.readAllBytes(Paths.get("sql/sqlite_schema.sql")), StandardCharsets.UTF_8);
                    // Separar por punto y coma y ejecutar cada sentencia
                    String[] sentencias = script.split(";");
                    for (String sentencia : sentencias) {
                        if (!sentencia.trim().isEmpty()) {
                            stmt.execute(sentencia.trim());
                        }
                    }
                    System.out.println("Base de datos inicializada correctamente.");
                } else {
                    System.err.println("Advertencia: No se encontro el archivo sql/sqlite_schema.sql");
                }
            } else {
                rs.close();
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la conexion a la base de datos.
     */
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
