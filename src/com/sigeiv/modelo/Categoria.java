package com.sigeiv.modelo;

/**
 * Clase que representa una Categoria de productos en SIGEIV-Volcano.
 * Corresponde a la tabla 'categoria' de la base de datos.
 * 
 * Entrada: id_categoria, nombre_categoria, descripcion
 * Proceso: Encapsula la clasificacion de productos
 * Salida: Objeto Categoria para agrupar productos
 */
public class Categoria {

    // --- Atributos ---
    private int idCategoria;
    private String nombreCategoria;
    private String descripcion;

    // --- Constructores ---

    /** Constructor vacio */
    public Categoria() {}

    /** Constructor con todos los atributos */
    public Categoria(int idCategoria, String nombreCategoria, String descripcion) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
    }

    // --- Getters ---

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // --- Setters ---

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombreCategoria;
    }
}





