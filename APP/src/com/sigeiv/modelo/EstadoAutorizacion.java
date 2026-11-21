package com.sigeiv.modelo;

/**
 * Estados del flujo jerarquico de autorizacion de documentos soporte.
 */
public enum EstadoAutorizacion {
    PENDIENTE("Pendiente de Autorizacion"),
    APROBADO("Aprobado / Asentado en Kardex"),
    RECHAZADO("Rechazado");

    private final String etiqueta;

    EstadoAutorizacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
