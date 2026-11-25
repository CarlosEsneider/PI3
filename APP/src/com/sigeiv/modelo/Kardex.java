package com.sigeiv.modelo;

/**
 * Representa un asiento inmutable en el libro Kardex de producto terminado.
 * Corresponde a la tabla 'kardex'.
 */
public class Kardex {

    private int idKardex;
    private int idProducto;
    private String nombreProducto;
    private String codigoSku;
    private String fechaHora;
    private int idMovimiento;
    private String tipoEvento;
    private String numeroSoporte;
    private int cantidadEntrada;
    private int cantidadSalida;
    private int saldoStock;
    private double costoUnitario;
    private double costoTotalSaldo;
    private String registradoPor;
    private String autorizadoPor;

    public Kardex() {}

    public Kardex(int idKardex, int idProducto, String fechaHora, int idMovimiento,
                  String tipoEvento, String numeroSoporte, int cantidadEntrada,
                  int cantidadSalida, int saldoStock, double costoUnitario, double costoTotalSaldo) {
        this.idKardex = idKardex;
        this.idProducto = idProducto;
        this.fechaHora = fechaHora;
        this.idMovimiento = idMovimiento;
        this.tipoEvento = tipoEvento;
        this.numeroSoporte = numeroSoporte;
        this.cantidadEntrada = cantidadEntrada;
        this.cantidadSalida = cantidadSalida;
        this.saldoStock = saldoStock;
        this.costoUnitario = costoUnitario;
        this.costoTotalSaldo = costoTotalSaldo;
    }

    public Kardex(int idKardex, int idProducto, String nombreProducto, String codigoSku,
                  String fechaHora, int idMovimiento, String tipoEvento, String numeroSoporte,
                  int cantidadEntrada, int cantidadSalida, int saldoStock,
                  double costoUnitario, double costoTotalSaldo,
                  String registradoPor, String autorizadoPor) {
        this.idKardex = idKardex;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.codigoSku = codigoSku;
        this.fechaHora = fechaHora;
        this.idMovimiento = idMovimiento;
        this.tipoEvento = tipoEvento;
        this.numeroSoporte = numeroSoporte;
        this.cantidadEntrada = cantidadEntrada;
        this.cantidadSalida = cantidadSalida;
        this.saldoStock = saldoStock;
        this.costoUnitario = costoUnitario;
        this.costoTotalSaldo = costoTotalSaldo;
        this.registradoPor = registradoPor;
        this.autorizadoPor = autorizadoPor;
    }

    public int getIdKardex() {
        return idKardex;
    }

    public void setIdKardex(int idKardex) {
        this.idKardex = idKardex;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCodigoSku() {
        return codigoSku;
    }

    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getNumeroSoporte() {
        return numeroSoporte;
    }

    public void setNumeroSoporte(String numeroSoporte) {
        this.numeroSoporte = numeroSoporte;
    }

    public int getCantidadEntrada() {
        return cantidadEntrada;
    }

    public void setCantidadEntrada(int cantidadEntrada) {
        this.cantidadEntrada = cantidadEntrada;
    }

    public int getCantidadSalida() {
        return cantidadSalida;
    }

    public void setCantidadSalida(int cantidadSalida) {
        this.cantidadSalida = cantidadSalida;
    }

    public int getSaldoStock() {
        return saldoStock;
    }

    public void setSaldoStock(int saldoStock) {
        this.saldoStock = saldoStock;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public double getCostoTotalSaldo() {
        return costoTotalSaldo;
    }

    public void setCostoTotalSaldo(double costoTotalSaldo) {
        this.costoTotalSaldo = costoTotalSaldo;
    }

    public String getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(String registradoPor) {
        this.registradoPor = registradoPor;
    }

    public String getAutorizadoPor() {
        return autorizadoPor;
    }

    public void setAutorizadoPor(String autorizadoPor) {
        this.autorizadoPor = autorizadoPor;
    }
}
