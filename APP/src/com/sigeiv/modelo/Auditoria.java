package com.sigeiv.modelo;

/**
 * Representa un registro de auditoria forense inmutable sobre operaciones sensibles.
 * Corresponde a la tabla 'auditoria'.
 */
public class Auditoria {

    private int idAuditoria;
    private String fechaHora;
    private int idUsuario;
    private String nombreUsuario;
    private String accion; // AUTORIZACION, RECHAZO, CREACION, MODIFICACION
    private String tablaAfectada;
    private int idRegistroAfectado;
    private String detalle;

    public Auditoria() {}

    public Auditoria(int idAuditoria, String fechaHora, int idUsuario,
                     String accion, String tablaAfectada, int idRegistroAfectado, String detalle) {
        this.idAuditoria = idAuditoria;
        this.fechaHora = fechaHora;
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.tablaAfectada = tablaAfectada;
        this.idRegistroAfectado = idRegistroAfectado;
        this.detalle = detalle;
    }

    public int getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getTablaAfectada() {
        return tablaAfectada;
    }

    public void setTablaAfectada(String tablaAfectada) {
        this.tablaAfectada = tablaAfectada;
    }

    public int getIdRegistroAfectado() {
        return idRegistroAfectado;
    }

    public void setIdRegistroAfectado(int idRegistroAfectado) {
        this.idRegistroAfectado = idRegistroAfectado;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
