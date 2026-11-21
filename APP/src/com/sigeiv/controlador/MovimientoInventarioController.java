package com.sigeiv.controlador;

import com.sigeiv.dao.MovimientoInventarioDAO;
import com.sigeiv.modelo.DetalleMovimiento;
import com.sigeiv.modelo.MovimientoInventario;
import com.sigeiv.modelo.Usuario;

import java.util.List;

/**
 * Controlador de Logica de Negocio para el Subsistema de Movimientos de Inventario,
 * Documentos Soporte y Bandeja de Autorizaciones (Fase 2 / Fase 3 - OE3, RNF-05).
 */
public class MovimientoInventarioController {

    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAO();

    /**
     * Clase auxiliar de resultado de operacion para desacoplar logica y vista.
     */
    public static class ResultadoOperacion {
        private final boolean exito;
        private final String mensaje;

        private ResultadoOperacion(boolean exito, String mensaje) {
            this.exito = exito;
            this.mensaje = mensaje;
        }

        public static ResultadoOperacion ok(String mensaje) {
            return new ResultadoOperacion(true, mensaje);
        }

        public static ResultadoOperacion error(String mensaje) {
            return new ResultadoOperacion(false, mensaje);
        }

        public boolean isExito() {
            return exito;
        }

        public String getMensaje() {
            return mensaje;
        }
    }

    /**
     * Obtiene el listado de todos los movimientos de inventario en estado PENDIENTE.
     */
    public List<MovimientoInventario> listarPendientes() {
        return movimientoDAO.listarPendientes();
    }

    /**
     * Obtiene el detalle de renglones de producto de un movimiento.
     */
    public List<DetalleMovimiento> obtenerDetalles(int idMovimiento) {
        return movimientoDAO.listarDetalles(idMovimiento);
    }

    /**
     * Valida permisos jerarquicos y autoriza formalmente un documento soporte.
     * 
     * Reglas de Negocio Oficiales (Tabla 2 y Seccion 8.3 del README):
     *  - Lote de Produccion (ENT_PROD) / Devolucion (DEV_CLIE) -> Requiere Jefe de Produccion (2) o Admin (1).
     *  - Ajuste Fisico (AJU_FISI) / Baja Tecnica (BAJ_MERM)     -> Requiere Administrador / Gerente General (1).
     *  - Despacho de Venta (SAL_VENT)                           -> Requiere Asesor Comercial (4) o Admin (1).
     */
    public ResultadoOperacion autorizarMovimiento(int idMovimiento, Usuario usuarioLogueado) {
        if (usuarioLogueado == null) {
            return ResultadoOperacion.error("No hay una sesion de usuario activa.");
        }

        MovimientoInventario mov = movimientoDAO.buscarPorId(idMovimiento);
        if (mov == null) {
            return ResultadoOperacion.error("El movimiento seleccionado no existe en la base de datos.");
        }

        if (!mov.isPendiente()) {
            return ResultadoOperacion.error("El documento ya se encuentra en estado: " + mov.getEstadoAutorizacion());
        }

        // Verificacion de rol jerarquico autorizado segun tipo de movimiento
        String codTipo = mov.getCodigoTipoMovimiento();
        int rolUsuario = usuarioLogueado.getIdRol();
        boolean esAdmin = (rolUsuario == 1);
        boolean puedeAutorizar = usuarioLogueado.isPuedeAutorizar() || esAdmin;

        if (!puedeAutorizar) {
            return ResultadoOperacion.error("Tu cuenta de usuario no tiene facultades de autorizacion (puede_autorizar = false).");
        }

        String rolRequeridoTexto;
        boolean rolValido;

        if ("ENT_PROD".equalsIgnoreCase(codTipo) || "DEV_CLIE".equalsIgnoreCase(codTipo)) {
            // Lote de Produccion o Devolucion con Dictamen -> Jefe de Produccion (2) o Admin (1)
            rolValido = esAdmin || (rolUsuario == 2);
            rolRequeridoTexto = "Jefe de Produccion y Calidad o Administrador";
        } else if ("AJU_FISI".equalsIgnoreCase(codTipo) || "BAJ_MERM".equalsIgnoreCase(codTipo)) {
            // Ajuste Fisico o Baja Tecnica -> Autorizacion indelegable de Gerencia General (1)
            rolValido = esAdmin;
            rolRequeridoTexto = "Administrador / Gerente General (Autorizacion Indelegable)";
        } else if ("SAL_VENT".equalsIgnoreCase(codTipo)) {
            // Despacho de Venta -> Asesor Comercial (4) o Admin (1)
            rolValido = esAdmin || (rolUsuario == 4);
            rolRequeridoTexto = "Asesor Comercial / Ventas o Administrador";
        } else {
            // Por defecto, solo Administrador
            rolValido = esAdmin;
            rolRequeridoTexto = "Administrador / Gerente General";
        }

        if (!rolValido) {
            return ResultadoOperacion.error(
                "ACCESO DENEGADO POR CONTROL INTERNO:\n\n" +
                "El documento soporte " + mov.getNumeroDocumentoSoporte() + " (" + mov.getNombreTipoMovimiento() + ")\n" +
                "requiere autorizacion exclusiva de: " + rolRequeridoTexto + ".\n\n" +
                "Tu rol actual es: " + obtenerNombreRol(rolUsuario) + "."
            );
        }

        try {
            boolean exito = movimientoDAO.autorizarMovimiento(idMovimiento, usuarioLogueado.getIdUsuario());
            if (exito) {
                return ResultadoOperacion.ok(
                    "¡DOCUMENTO AUTORIZADO EXITOSAMENTE!\n\n" +
                    "Documento Soporte: " + mov.getNumeroDocumentoSoporte() + "\n" +
                    "Tipo: " + mov.getNombreTipoMovimiento() + "\n" +
                    "Autorizado por: " + usuarioLogueado.getNombreUsuario() + " [" + obtenerNombreRol(rolUsuario) + "]\n\n" +
                    "El stock fisico en bodega ha sido actualizado y el asiento oficial en Kardex ha quedado registrado."
                );
            } else {
                return ResultadoOperacion.error("No fue posible completar la autorizacion del documento.");
            }
        } catch (Exception e) {
            return ResultadoOperacion.error("Error durante la autorizacion transaccional:\n" + e.getMessage());
        }
    }

    /**
     * Valida permisos jerarquicos y rechaza un documento soporte con causal obligatoria.
     */
    public ResultadoOperacion rechazarMovimiento(int idMovimiento, Usuario usuarioLogueado, String motivoRechazo) {
        if (usuarioLogueado == null) {
            return ResultadoOperacion.error("No hay una sesion de usuario activa.");
        }

        if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
            return ResultadoOperacion.error("Debe proporcionar una justificacion tecnica obligatoria para el rechazo.");
        }

        MovimientoInventario mov = movimientoDAO.buscarPorId(idMovimiento);
        if (mov == null) {
            return ResultadoOperacion.error("El movimiento seleccionado no existe.");
        }

        if (!mov.isPendiente()) {
            return ResultadoOperacion.error("El documento ya no esta en estado PENDIENTE.");
        }

        // Verificacion de roles
        int rolUsuario = usuarioLogueado.getIdRol();
        boolean esAdmin = (rolUsuario == 1);
        boolean puedeAutorizar = usuarioLogueado.isPuedeAutorizar() || esAdmin;

        if (!puedeAutorizar) {
            return ResultadoOperacion.error("Tu cuenta no cuenta con permisos para rechazar o emitir dictamenes de documentos soporte.");
        }

        try {
            boolean exito = movimientoDAO.rechazarMovimiento(idMovimiento, usuarioLogueado.getIdUsuario(), motivoRechazo.trim());
            if (exito) {
                return ResultadoOperacion.ok(
                    "El documento soporte " + mov.getNumeroDocumentoSoporte() + " ha sido RECHAZADO.\n\n" +
                    "Causal tecnica registrada en auditoria: \"" + motivoRechazo.trim() + "\"\n" +
                    "No se alteraron las existencias del inventario."
                );
            } else {
                return ResultadoOperacion.error("No se pudo completar el rechazo del documento.");
            }
        } catch (Exception e) {
            return ResultadoOperacion.error("Error al procesar el rechazo:\n" + e.getMessage());
        }
    }

    private String obtenerNombreRol(int idRol) {
        switch (idRol) {
            case 1: return "Administrador / Gerente";
            case 2: return "Jefe de Produccion";
            case 3: return "Encargado de Almacen";
            case 4: return "Asesor Comercial";
            case 5: return "Auditor / Consultor";
            default: return "Rol " + idRol;
        }
    }
}
