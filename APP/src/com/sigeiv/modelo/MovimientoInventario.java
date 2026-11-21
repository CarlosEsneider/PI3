package com.sigeiv.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa la cabecera de un movimiento o documento soporte de inventario.
 * Corresponde a la tabla 'movimiento_inventario'.
 */
public class MovimientoInventario {

    private int idMovimiento;
    private String numeroDocumentoSoporte;
    private String fechaHora;
    private int idTipoMovimiento;
    private String codigoTipoMovimiento;
    private String nombreTipoMovimiento;
    private String naturalezaAfectacion; // ENTRADA, SALIDA, AJUSTE
    private String rolAutorizadorRequerido;
    private int idUsuarioCreador;
    private String nombreUsuarioCreador;
    private Integer idUsuarioAutorizador;
    private String nombreUsuarioAutorizador;
    private String estadoAutorizacion; // PENDIENTE, APROBADO, RECHAZADO
    private String fechaAutorizacion;
    private String observaciones;
    private List<DetalleMovimiento> detalles = new ArrayList<>();

    public MovimientoInventario() {}

    public MovimientoInventario(int idMovimiento, String numeroDocumentoSoporte, String fechaHora,
                                int idTipoMovimiento, int idUsuarioCreador, Integer idUsuarioAutorizador,
                                String estadoAutorizacion, String fechaAutorizacion, String observaciones) {
        this.idMovimiento = idMovimiento;
        this.numeroDocumentoSoporte = numeroDocumentoSoporte;
        this.fechaHora = fechaHora;
        this.idTipoMovimiento = idTipoMovimiento;
        this.idUsuarioCreador = idUsuarioCreador;
        this.idUsuarioAutorizador = idUsuarioAutorizador;
        this.estadoAutorizacion = estadoAutorizacion;
        this.fechaAutorizacion = fechaAutorizacion;
        this.observaciones = observaciones;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getNumeroDocumentoSoporte() {
        return numeroDocumentoSoporte;
    }

    public void setNumeroDocumentoSoporte(String numeroDocumentoSoporte) {
        this.numeroDocumentoSoporte = numeroDocumentoSoporte;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getIdTipoMovimiento() {
        return idTipoMovimiento;
    }

    public void setIdTipoMovimiento(int idTipoMovimiento) {
        this.idTipoMovimiento = idTipoMovimiento;
    }

    public String getCodigoTipoMovimiento() {
        return codigoTipoMovimiento;
    }

    public void setCodigoTipoMovimiento(String codigoTipoMovimiento) {
        this.codigoTipoMovimiento = codigoTipoMovimiento;
    }

    public String getNombreTipoMovimiento() {
        return nombreTipoMovimiento;
    }

    public void setNombreTipoMovimiento(String nombreTipoMovimiento) {
        this.nombreTipoMovimiento = nombreTipoMovimiento;
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

    public int getIdUsuarioCreador() {
        return idUsuarioCreador;
    }

    public void setIdUsuarioCreador(int idUsuarioCreador) {
        this.idUsuarioCreador = idUsuarioCreador;
    }

    public String getNombreUsuarioCreador() {
        return nombreUsuarioCreador;
    }

    public void setNombreUsuarioCreador(String nombreUsuarioCreador) {
        this.nombreUsuarioCreador = nombreUsuarioCreador;
    }

    public Integer getIdUsuarioAutorizador() {
        return idUsuarioAutorizador;
    }

    public void setIdUsuarioAutorizador(Integer idUsuarioAutorizador) {
        this.idUsuarioAutorizador = idUsuarioAutorizador;
    }

    public String getNombreUsuarioAutorizador() {
        return nombreUsuarioAutorizador;
    }

    public void setNombreUsuarioAutorizador(String nombreUsuarioAutorizador) {
        this.nombreUsuarioAutorizador = nombreUsuarioAutorizador;
    }

    public String getEstadoAutorizacion() {
        return estadoAutorizacion;
    }

    public void setEstadoAutorizacion(String estadoAutorizacion) {
        this.estadoAutorizacion = estadoAutorizacion;
    }

    public String getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(String fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<DetalleMovimiento> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleMovimiento> detalles) {
        this.detalles = detalles;
    }

    public boolean isPendiente() {
        return "PENDIENTE".equalsIgnoreCase(estadoAutorizacion);
    }
}
