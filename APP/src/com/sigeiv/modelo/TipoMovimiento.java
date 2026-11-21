package com.sigeiv.modelo;

/**
 * Representa la clasificacion y parametrizacion de los movimientos de inventario.
 * Corresponde a la tabla 'tipo_movimiento'.
 * 
 * Codigos estandar:
 *  - ENT_PROD: Entrada por Lote de Produccion (Naturaleza: ENTRADA)
 *  - SAL_VENT: Salida por Despacho de Venta (Naturaleza: SALIDA)
 *  - DEV_CLIE: Devolucion de Cliente con Dictamen (Naturaleza: ENTRADA)
 *  - AJU_FISI: Ajuste por Conteo Fisico (Naturaleza: AJUSTE)
 *  - BAJ_MERM: Baja por Merma Tecnica / Averia (Naturaleza: SALIDA)
 */
public class TipoMovimiento {

    private int idTipoMovimiento;
    private String codigoMovimiento;
    private String descripcion;
    private String naturalezaAfectacion; // ENTRADA, SALIDA, AJUSTE
    private String rolAutorizadorRequerido; // "Jefe de Produccion", "Administrador", "Asesor Comercial"

    public TipoMovimiento() {}

    public TipoMovimiento(int idTipoMovimiento, String codigoMovimiento, String descripcion,
                          String naturalezaAfectacion, String rolAutorizadorRequerido) {
        this.idTipoMovimiento = idTipoMovimiento;
        this.codigoMovimiento = codigoMovimiento;
        this.descripcion = descripcion;
        this.naturalezaAfectacion = naturalezaAfectacion;
        this.rolAutorizadorRequerido = rolAutorizadorRequerido;
    }

    public int getIdTipoMovimiento() {
        return idTipoMovimiento;
    }

    public void setIdTipoMovimiento(int idTipoMovimiento) {
        this.idTipoMovimiento = idTipoMovimiento;
    }

    public String getCodigoMovimiento() {
        return codigoMovimiento;
    }

    public void setCodigoMovimiento(String codigoMovimiento) {
        this.codigoMovimiento = codigoMovimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNaturalezaAfectacion() {
        return naturalezaAfectacion;
    }

    public void setNaturalezaAfectacion(String naturalezaAfectacion) {
        this.naturalezaAfectacion = naturalezaAfectacion;
    }

    public String getRolAutorizadorRequerido() {
        return rolAutorizadorRequerido;
    }

    public void setRolAutorizadorRequerido(String rolAutorizadorRequerido) {
        this.rolAutorizadorRequerido = rolAutorizadorRequerido;
    }

    @Override
    public String toString() {
        return descripcion + " (" + codigoMovimiento + ")";
    }
}
