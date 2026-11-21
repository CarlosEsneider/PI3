package com.sigeiv.test;

import com.sigeiv.controlador.MovimientoInventarioController;
import com.sigeiv.controlador.MovimientoInventarioController.ResultadoOperacion;
import com.sigeiv.dao.ConexionDB;
import com.sigeiv.dao.ProductoDAO;
import com.sigeiv.modelo.DetalleMovimiento;
import com.sigeiv.modelo.MovimientoInventario;
import com.sigeiv.modelo.Producto;
import com.sigeiv.modelo.Usuario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Suite de Pruebas Unitarias y de Integracion para Fase 3 (OE3, RNF-04, RNF-05, RNF-06).
 * Verifica el flujo jerarquico de autorizaciones, kardex, auditoria y consistencia transaccional.
 */
public class TestVerificacionS4 {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("  INICIANDO SUITE DE PRUEBAS DE INTEGRACION - FASE 3 (OE3)");
        System.out.println("  Bandeja de Autorizaciones, Kardex Transaccional y Roles Jerarquicos");
        System.out.println("==================================================================");

        int exitos = 0;
        int fallos = 0;

        MovimientoInventarioController ctrl = new MovimientoInventarioController();
        ProductoDAO prodDAO = new ProductoDAO();

        // Usuarios simulados para cada rol
        Usuario admin = new Usuario(1, "Carlos Espitia", "admin", "pass", "admin@volcano.com", 1, true, true);
        Usuario jefeProd = new Usuario(2, "Mauricio Ledesma", "mauricio", "pass", "prod@volcano.com", 2, true, true);
        Usuario almacen = new Usuario(3, "Ana Garcia", "ana.garcia", "pass", "almacen@volcano.com", 3, false, true);
        Usuario asesor = new Usuario(4, "Pedro Lopez", "pedro.lopez", "pass", "ventas@volcano.com", 4, true, true);
        Usuario auditor = new Usuario(5, "Juan Ramirez", "juan.r", "pass", "auditor@volcano.com", 5, false, true);

        // PRUEBA 1: Listar pendientes
        try {
            List<MovimientoInventario> pendientes = ctrl.listarPendientes();
            if (!pendientes.isEmpty()) {
                System.out.println("[PASS] Prueba 1: listarPendientes() retorno " + pendientes.size() + " documentos pendientes.");
                exitos++;
            } else {
                System.err.println("[FAIL] Prueba 1: listarPendientes() no retorno documentos.");
                fallos++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 1 con excepcion: " + e.getMessage());
            fallos++;
        }

        // PRUEBA 2: Cargar detalles de un movimiento
        try {
            List<MovimientoInventario> pendientes = ctrl.listarPendientes();
            if (!pendientes.isEmpty()) {
                MovimientoInventario primerMov = pendientes.get(0);
                List<DetalleMovimiento> detalles = ctrl.obtenerDetalles(primerMov.getIdMovimiento());
                if (!detalles.isEmpty() && detalles.get(0).getCodigoSku() != null) {
                    System.out.println("[PASS] Prueba 2: obtenerDetalles() retorno " + detalles.size() +
                                       " renglones. Primer SKU: " + detalles.get(0).getCodigoSku());
                    exitos++;
                } else {
                    System.err.println("[FAIL] Prueba 2: Detalles vacios o sin SKU.");
                    fallos++;
                }
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 2 con excepcion: " + e.getMessage());
            fallos++;
        }

        // PRUEBA 3: Control de acceso - Asesor Comercial NO puede autorizar Lote de Produccion (ENT_PROD)
        try {
            List<MovimientoInventario> pendientes = ctrl.listarPendientes();
            MovimientoInventario movLote = pendientes.stream()
                .filter(m -> "ENT_PROD".equalsIgnoreCase(m.getCodigoTipoMovimiento()))
                .findFirst().orElse(null);

            if (movLote != null) {
                ResultadoOperacion res = ctrl.autorizarMovimiento(movLote.getIdMovimiento(), asesor);
                if (!res.isExito() && res.getMensaje().contains("DENEGADO")) {
                    System.out.println("[PASS] Prueba 3: Segregacion de funciones exitosa. Asesor Comercial fue bloqueado al intentar autorizar Lote de Produccion.");
                    exitos++;
                } else {
                    System.err.println("[FAIL] Prueba 3: Se permitio al asesor comercial autorizar lote de produccion.");
                    fallos++;
                }
            } else {
                System.out.println("[SKIP] Prueba 3: No se encontro movimiento ENT_PROD pendiente.");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 3 con excepcion: " + e.getMessage());
            fallos++;
        }

        // PRUEBA 4: Control de acceso - Jefe de Produccion NO puede autorizar Ajuste Fisico (AJU_FISI)
        try {
            List<MovimientoInventario> pendientes = ctrl.listarPendientes();
            MovimientoInventario movAjuste = pendientes.stream()
                .filter(m -> "AJU_FISI".equalsIgnoreCase(m.getCodigoTipoMovimiento()))
                .findFirst().orElse(null);

            if (movAjuste != null) {
                ResultadoOperacion res = ctrl.autorizarMovimiento(movAjuste.getIdMovimiento(), jefeProd);
                if (!res.isExito() && res.getMensaje().contains("DENEGADO")) {
                    System.out.println("[PASS] Prueba 4: Segregacion de funciones exitosa. Jefe de Produccion fue bloqueado al intentar autorizar Ajuste Fisico (indelegable Gerencia).");
                    exitos++;
                } else {
                    System.err.println("[FAIL] Prueba 4: Se permitio autorizar Ajuste Fisico a rol no facultado.");
                    fallos++;
                }
            } else {
                System.out.println("[SKIP] Prueba 4: No se encontro movimiento AJU_FISI pendiente.");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 4 con excepcion: " + e.getMessage());
            fallos++;
        }

        // PRUEBA 5: Autorizacion formal por rol facultado e impacto en Stock + Kardex + Auditoria
        try {
            List<MovimientoInventario> pendientes = ctrl.listarPendientes();
            MovimientoInventario movLote = pendientes.stream()
                .filter(m -> "ENT_PROD".equalsIgnoreCase(m.getCodigoTipoMovimiento()))
                .findFirst().orElse(null);

            if (movLote != null) {
                List<DetalleMovimiento> detalles = ctrl.obtenerDetalles(movLote.getIdMovimiento());
                DetalleMovimiento primerItem = detalles.get(0);
                Producto prodAntes = prodDAO.buscarPorId(primerItem.getIdProducto());
                int stockAntes = prodAntes.getStockActual();
                int cantIngreso = primerItem.getCantidad();

                // Jefe de Produccion autoriza Lote de Produccion
                ResultadoOperacion res = ctrl.autorizarMovimiento(movLote.getIdMovimiento(), jefeProd);
                if (res.isExito()) {
                    Producto prodDespues = prodDAO.buscarPorId(primerItem.getIdProducto());
                    int stockDespues = prodDespues.getStockActual();

                    // Verificar stock incrementado
                    boolean stockOk = (stockDespues == stockAntes + cantIngreso);

                    // Verificar asiento en Kardex
                    boolean kardexOk = false;
                    try (Connection conn = ConexionDB.getInstancia().getConexion();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM kardex WHERE id_movimiento = " + movLote.getIdMovimiento())) {
                        if (rs.next() && rs.getInt(1) > 0) kardexOk = true;
                    }

                    // Verificar auditoria
                    boolean auditOk = false;
                    try (Connection conn = ConexionDB.getInstancia().getConexion();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM auditoria WHERE accion = 'AUTORIZACION' AND id_registro_afectado = " + movLote.getIdMovimiento())) {
                        if (rs.next() && rs.getInt(1) > 0) auditOk = true;
                    }

                    if (stockOk && kardexOk && auditOk) {
                        System.out.println("[PASS] Prueba 5: Autorizacion transaccional ACID completada.");
                        System.out.println("       Stock previo: " + stockAntes + " -> Stock nuevo: " + stockDespues +
                                           " (Delta: +" + cantIngreso + "). Asiento en Kardex y Auditoria verificados.");
                        exitos++;
                    } else {
                        System.err.println("[FAIL] Prueba 5: Inconsistencia detectada. stockOk=" + stockOk + ", kardexOk=" + kardexOk + ", auditOk=" + auditOk);
                        fallos++;
                    }
                } else {
                    System.err.println("[FAIL] Prueba 5: Fallo autorizarMovimiento(): " + res.getMensaje());
                    fallos++;
                }
            } else {
                System.out.println("[SKIP] Prueba 5: No se encontro lote pendiente para autorizar.");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 5 con excepcion: " + e.getMessage());
            fallos++;
        }

        // PRUEBA 6: Rechazo formal con justificacion tecnica
        try {
            List<MovimientoInventario> pendientes = ctrl.listarPendientes();
            MovimientoInventario movParaRechazar = pendientes.stream()
                .filter(m -> "DEV_CLIE".equalsIgnoreCase(m.getCodigoTipoMovimiento()) || "SAL_VENT".equalsIgnoreCase(m.getCodigoTipoMovimiento()))
                .findFirst().orElse(null);

            if (movParaRechazar != null) {
                String causal = "Dictamen de control de calidad: Danio estetico no amparado por garantia.";
                ResultadoOperacion res = ctrl.rechazarMovimiento(movParaRechazar.getIdMovimiento(), admin, causal);
                if (res.isExito()) {
                    // Verificar auditoria de rechazo
                    boolean auditRechazoOk = false;
                    try (Connection conn = ConexionDB.getInstancia().getConexion();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM auditoria WHERE accion = 'RECHAZO' AND id_registro_afectado = " + movParaRechazar.getIdMovimiento())) {
                        if (rs.next() && rs.getInt(1) > 0) auditRechazoOk = true;
                    }

                    if (auditRechazoOk) {
                        System.out.println("[PASS] Prueba 6: Rechazo con dictamen tecnico registrado con exito en Auditoria.");
                        exitos++;
                    } else {
                        System.err.println("[FAIL] Prueba 6: No se registro el rechazo en auditoria.");
                        fallos++;
                    }
                } else {
                    System.err.println("[FAIL] Prueba 6: Fallo rechazarMovimiento(): " + res.getMensaje());
                    fallos++;
                }
            } else {
                System.out.println("[SKIP] Prueba 6: No hay movimiento pendiente para prueba de rechazo.");
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 6 con excepcion: " + e.getMessage());
            fallos++;
        }

        System.out.println("==================================================================");
        System.out.println("  RESUMEN DE PRUEBAS:");
        System.out.println("  Exitos: " + exitos + " | Fallos: " + fallos);
        System.out.println("==================================================================");

        if (fallos > 0) {
            System.exit(1);
        }
    }
}
