package com.sigeiv.dao;

import com.sigeiv.modelo.DetalleMovimiento;
import com.sigeiv.modelo.Kardex;
import com.sigeiv.modelo.MovimientoInventario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestion transaccional de Movimientos de Inventario,
 * Documentos Soporte y Asentamiento en Kardex (Fase 2 / Fase 3 - OE2, OE3, RNF-04, RNF-06).
 */
public class MovimientoInventarioDAO {

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    /**
     * Lista todos los movimientos de inventario que estan en estado 'PENDIENTE'.
     * Incluye datos del tipo de movimiento y del usuario creador.
     */
    public List<MovimientoInventario> listarPendientes() {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = "SELECT m.id_movimiento, m.numero_documento_soporte, m.fecha_hora, m.id_tipo_movimiento, " +
                     "       t.codigo_movimiento, t.descripcion AS desc_tipo, t.naturaleza_afectacion, t.rol_autorizador_requerido, " +
                     "       m.id_usuario_creador, u.nombre_usuario AS creador_nombre, " +
                     "       m.id_usuario_autorizador, m.estado_autorizacion, m.fecha_autorizacion, m.observaciones " +
                     "FROM movimiento_inventario m " +
                     "JOIN tipo_movimiento t ON m.id_tipo_movimiento = t.id_tipo_movimiento " +
                     "JOIN usuario u ON m.id_usuario_creador = u.id_usuario " +
                     "WHERE m.estado_autorizacion = 'PENDIENTE' " +
                     "ORDER BY m.fecha_hora DESC";

        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MovimientoInventario m = new MovimientoInventario();
                m.setIdMovimiento(rs.getInt("id_movimiento"));
                m.setNumeroDocumentoSoporte(rs.getString("numero_documento_soporte"));
                m.setFechaHora(rs.getString("fecha_hora"));
                m.setIdTipoMovimiento(rs.getInt("id_tipo_movimiento"));
                m.setCodigoTipoMovimiento(rs.getString("codigo_movimiento"));
                m.setNombreTipoMovimiento(rs.getString("desc_tipo"));
                m.setNaturalezaAfectacion(rs.getString("naturaleza_afectacion"));
                m.setRolAutorizadorRequerido(rs.getString("rol_autorizador_requerido"));
                m.setIdUsuarioCreador(rs.getInt("id_usuario_creador"));
                m.setNombreUsuarioCreador(rs.getString("creador_nombre"));
                m.setIdUsuarioAutorizador((Integer) rs.getObject("id_usuario_autorizador"));
                m.setEstadoAutorizacion(rs.getString("estado_autorizacion"));
                m.setFechaAutorizacion(rs.getString("fecha_autorizacion"));
                m.setObservaciones(rs.getString("observaciones"));
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Lista los renglones (detalles) de productos de un movimiento especifico.
     * Vincula con el producto para traer SKU y existencias actuales de bodega.
     */
    public List<DetalleMovimiento> listarDetalles(int idMovimiento) {
        List<DetalleMovimiento> lista = new ArrayList<>();
        String sql = "SELECT d.id_detalle_movimiento, d.id_movimiento, d.id_producto, " +
                     "       p.nombre_producto, p.codigo_sku, p.stock_actual, " +
                     "       d.cantidad, d.costo_unitario, d.saldo_resultante " +
                     "FROM detalle_movimiento d " +
                     "JOIN producto p ON d.id_producto = p.id_producto " +
                     "WHERE d.id_movimiento = ? " +
                     "ORDER BY d.id_detalle_movimiento ASC";

        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleMovimiento det = new DetalleMovimiento();
                    det.setIdDetalleMovimiento(rs.getInt("id_detalle_movimiento"));
                    det.setIdMovimiento(rs.getInt("id_movimiento"));
                    det.setIdProducto(rs.getInt("id_producto"));
                    det.setNombreProducto(rs.getString("nombre_producto"));
                    det.setCodigoSku(rs.getString("codigo_sku"));
                    det.setStockActualBodega(rs.getInt("stock_actual"));
                    det.setCantidad(rs.getInt("cantidad"));
                    det.setCostoUnitario(rs.getDouble("costo_unitario"));
                    det.setSaldoResultante(rs.getInt("saldo_resultante"));
                    lista.add(det);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca un movimiento por su ID con sus datos de cabecera completos.
     */
    public MovimientoInventario buscarPorId(int idMovimiento) {
        String sql = "SELECT m.id_movimiento, m.numero_documento_soporte, m.fecha_hora, m.id_tipo_movimiento, " +
                     "       t.codigo_movimiento, t.descripcion AS desc_tipo, t.naturaleza_afectacion, t.rol_autorizador_requerido, " +
                     "       m.id_usuario_creador, u.nombre_usuario AS creador_nombre, " +
                     "       m.id_usuario_autorizador, m.estado_autorizacion, m.fecha_autorizacion, m.observaciones " +
                     "FROM movimiento_inventario m " +
                     "JOIN tipo_movimiento t ON m.id_tipo_movimiento = t.id_tipo_movimiento " +
                     "JOIN usuario u ON m.id_usuario_creador = u.id_usuario " +
                     "WHERE m.id_movimiento = ?";

        try (Connection conn = conexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMovimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MovimientoInventario m = new MovimientoInventario();
                    m.setIdMovimiento(rs.getInt("id_movimiento"));
                    m.setNumeroDocumentoSoporte(rs.getString("numero_documento_soporte"));
                    m.setFechaHora(rs.getString("fecha_hora"));
                    m.setIdTipoMovimiento(rs.getInt("id_tipo_movimiento"));
                    m.setCodigoTipoMovimiento(rs.getString("codigo_movimiento"));
                    m.setNombreTipoMovimiento(rs.getString("desc_tipo"));
                    m.setNaturalezaAfectacion(rs.getString("naturaleza_afectacion"));
                    m.setRolAutorizadorRequerido(rs.getString("rol_autorizador_requerido"));
                    m.setIdUsuarioCreador(rs.getInt("id_usuario_creador"));
                    m.setNombreUsuarioCreador(rs.getString("creador_nombre"));
                    m.setIdUsuarioAutorizador((Integer) rs.getObject("id_usuario_autorizador"));
                    m.setEstadoAutorizacion(rs.getString("estado_autorizacion"));
                    m.setFechaAutorizacion(rs.getString("fecha_autorizacion"));
                    m.setObservaciones(rs.getString("observaciones"));
                    m.setDetalles(listarDetalles(idMovimiento));
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ejecuta la transaccion ACID de autorizacion de un documento soporte (OE3, RNF-04, RNF-06):
     * 1. Verifica estado 'PENDIENTE'.
     * 2. Si es una salida, valida que existan existencias suficientes en bodega.
     * 3. Actualiza stock_actual en la tabla producto.
     * 4. Asienta el movimiento en el libro mayor inmutable de KARDEX.
     * 5. Actualiza saldo_resultante en detalle_movimiento.
     * 6. Cambia estado a 'APROBADO' y sella usuario y fecha de autorizacion.
     * 7. Registra evento en la bitacora de AUDITORIA forense.
     */
    public boolean autorizarMovimiento(int idMovimiento, int idUsuarioAutorizador) throws Exception {
        Connection conn = conexionDB.getConexion();
        boolean autoCommitOriginal = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            // 1. Obtener datos del movimiento y tipo
            String sqlCab = "SELECT m.estado_autorizacion, m.numero_documento_soporte, t.codigo_movimiento, t.naturaleza_afectacion " +
                            "FROM movimiento_inventario m " +
                            "JOIN tipo_movimiento t ON m.id_tipo_movimiento = t.id_tipo_movimiento " +
                            "WHERE m.id_movimiento = ?";

            String estado;
            String numeroDoc;
            String codTipo;
            String naturaleza;

            try (PreparedStatement psCab = conn.prepareStatement(sqlCab)) {
                psCab.setInt(1, idMovimiento);
                try (ResultSet rsCab = psCab.executeQuery()) {
                    if (!rsCab.next()) {
                        throw new Exception("El movimiento de inventario N.° " + idMovimiento + " no existe.");
                    }
                    estado = rsCab.getString("estado_autorizacion");
                    numeroDoc = rsCab.getString("numero_documento_soporte");
                    codTipo = rsCab.getString("codigo_movimiento");
                    naturaleza = rsCab.getString("naturaleza_afectacion");
                }
            }

            if (!"PENDIENTE".equalsIgnoreCase(estado)) {
                throw new Exception("El documento " + numeroDoc + " ya fue procesado anteriormente (Estado: " + estado + ").");
            }

            // 2. Obtener renglones de detalle
            String sqlDet = "SELECT d.id_detalle_movimiento, d.id_producto, d.cantidad, d.costo_unitario, " +
                            "       p.stock_actual, p.nombre_producto, p.codigo_sku " +
                            "FROM detalle_movimiento d " +
                            "JOIN producto p ON d.id_producto = p.id_producto " +
                            "WHERE d.id_movimiento = ?";

            List<DetalleMovimiento> detalles = new ArrayList<>();
            try (PreparedStatement psDet = conn.prepareStatement(sqlDet)) {
                psDet.setInt(1, idMovimiento);
                try (ResultSet rsDet = psDet.executeQuery()) {
                    while (rsDet.next()) {
                        DetalleMovimiento det = new DetalleMovimiento();
                        det.setIdDetalleMovimiento(rsDet.getInt("id_detalle_movimiento"));
                        det.setIdProducto(rsDet.getInt("id_producto"));
                        det.setNombreProducto(rsDet.getString("nombre_producto"));
                        det.setCodigoSku(rsDet.getString("codigo_sku"));
                        det.setStockActualBodega(rsDet.getInt("stock_actual"));
                        det.setCantidad(rsDet.getInt("cantidad"));
                        det.setCostoUnitario(rsDet.getDouble("costo_unitario"));
                        detalles.add(det);
                    }
                }
            }

            if (detalles.isEmpty()) {
                throw new Exception("El documento soporte " + numeroDoc + " no contiene ningun producto en su detalle.");
            }

            // 3. Procesar cada producto: validar stock, actualizar producto, asentar kardex
            String sqlUpdProd = "UPDATE producto SET stock_actual = ? WHERE id_producto = ?";
            String sqlUpdDet  = "UPDATE detalle_movimiento SET saldo_resultante = ? WHERE id_detalle_movimiento = ?";
            String sqlInsKard = "INSERT INTO kardex (id_producto, fecha_hora, id_movimiento, tipo_evento, " +
                                "                   numero_soporte, cantidad_entrada, cantidad_salida, " +
                                "                   saldo_stock, costo_unitario, costo_total_saldo) " +
                                "VALUES (?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement psUpdProd = conn.prepareStatement(sqlUpdProd);
                 PreparedStatement psUpdDet  = conn.prepareStatement(sqlUpdDet);
                 PreparedStatement psInsKard = conn.prepareStatement(sqlInsKard)) {

                for (DetalleMovimiento det : detalles) {
                    int stockActual = det.getStockActualBodega();
                    int cantidad = det.getCantidad();
                    int nuevoStock;
                    int cantEntrada = 0;
                    int cantSalida = 0;

                    if ("SALIDA".equalsIgnoreCase(naturaleza) || "SAL_VENT".equalsIgnoreCase(codTipo) || "BAJ_MERM".equalsIgnoreCase(codTipo)) {
                        if (stockActual < cantidad) {
                            throw new Exception("Stock insuficiente para el asador [" + det.getCodigoSku() + "] " +
                                                det.getNombreProducto() + ". Disponible en bodega: " + stockActual +
                                                ", Solicitado para despacho/baja: " + cantidad + ".");
                        }
                        nuevoStock = stockActual - cantidad;
                        cantSalida = cantidad;
                    } else if ("ENTRADA".equalsIgnoreCase(naturaleza) || "ENT_PROD".equalsIgnoreCase(codTipo) || "DEV_CLIE".equalsIgnoreCase(codTipo)) {
                        nuevoStock = stockActual + cantidad;
                        cantEntrada = cantidad;
                    } else { // AJUSTE
                        // En conteo fisico, si el conteo difiere, la cantidad representa el saldo conciliado
                        nuevoStock = cantidad;
                        if (nuevoStock >= stockActual) {
                            cantEntrada = nuevoStock - stockActual;
                        } else {
                            cantSalida = stockActual - nuevoStock;
                        }
                    }

                    // Actualizar stock en tabla producto
                    psUpdProd.setInt(1, nuevoStock);
                    psUpdProd.setInt(2, det.getIdProducto());
                    psUpdProd.executeUpdate();

                    // Actualizar saldo_resultante en detalle_movimiento
                    psUpdDet.setInt(1, nuevoStock);
                    psUpdDet.setInt(2, det.getIdDetalleMovimiento());
                    psUpdDet.executeUpdate();

                    // Asentar en Kardex
                    psInsKard.setInt(1, det.getIdProducto());
                    psInsKard.setInt(2, idMovimiento);
                    psInsKard.setString(3, codTipo);
                    psInsKard.setString(4, numeroDoc);
                    psInsKard.setInt(5, cantEntrada);
                    psInsKard.setInt(6, cantSalida);
                    psInsKard.setInt(7, nuevoStock);
                    psInsKard.setDouble(8, det.getCostoUnitario());
                    psInsKard.setDouble(9, nuevoStock * det.getCostoUnitario());
                    psInsKard.executeUpdate();
                }
            }

            // 4. Actualizar cabecera del movimiento a 'APROBADO'
            String sqlUpdMov = "UPDATE movimiento_inventario " +
                               "SET estado_autorizacion = 'APROBADO', " +
                               "    id_usuario_autorizador = ?, " +
                               "    fecha_autorizacion = CURRENT_TIMESTAMP " +
                               "WHERE id_movimiento = ?";
            try (PreparedStatement psUpdMov = conn.prepareStatement(sqlUpdMov)) {
                psUpdMov.setInt(1, idUsuarioAutorizador);
                psUpdMov.setInt(2, idMovimiento);
                psUpdMov.executeUpdate();
            }

            // 5. Registrar en auditoria forense
            String sqlAud = "INSERT INTO auditoria (fecha_hora, id_usuario, accion, tabla_afectada, id_registro_afectado, detalle) " +
                            "VALUES (CURRENT_TIMESTAMP, ?, 'AUTORIZACION', 'movimiento_inventario', ?, ?)";
            try (PreparedStatement psAud = conn.prepareStatement(sqlAud)) {
                psAud.setInt(1, idUsuarioAutorizador);
                psAud.setInt(2, idMovimiento);
                psAud.setString(3, "Autorizacion formal del documento soporte " + numeroDoc + " (" + codTipo + "). Stock impactado y Kardex asentado.");
                psAud.executeUpdate();
            }

            // Confirmar transaccion
            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommitOriginal);
        }
    }

    /**
     * Rechaza un documento soporte con justificacion tecnica obligatoria.
     * No altera existencias de producto ni asienta en Kardex.
     */
    public boolean rechazarMovimiento(int idMovimiento, int idUsuarioAutorizador, String motivoRechazo) throws Exception {
        Connection conn = conexionDB.getConexion();
        boolean autoCommitOriginal = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            // Verificar estado pendiente
            String sqlCheck = "SELECT estado_autorizacion, numero_documento_soporte FROM movimiento_inventario WHERE id_movimiento = ?";
            String numDoc;
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, idMovimiento);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next()) throw new Exception("El movimiento N.° " + idMovimiento + " no existe.");
                    String est = rs.getString("estado_autorizacion");
                    numDoc = rs.getString("numero_documento_soporte");
                    if (!"PENDIENTE".equalsIgnoreCase(est)) {
                        throw new Exception("El documento " + numDoc + " ya no se encuentra PENDIENTE.");
                    }
                }
            }

            // Actualizar a RECHAZADO con motivo
            String sqlUpd = "UPDATE movimiento_inventario " +
                            "SET estado_autorizacion = 'RECHAZADO', " +
                            "    id_usuario_autorizador = ?, " +
                            "    fecha_autorizacion = CURRENT_TIMESTAMP, " +
                            "    observaciones = coalesce(observaciones, '') || ' [DICTAMEN RECHAZO: ' || ? || ']' " +
                            "WHERE id_movimiento = ?";
            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpd)) {
                psUpd.setInt(1, idUsuarioAutorizador);
                psUpd.setString(2, motivoRechazo.trim());
                psUpd.setInt(3, idMovimiento);
                psUpd.executeUpdate();
            }

            // Registrar en auditoria
            String sqlAud = "INSERT INTO auditoria (fecha_hora, id_usuario, accion, tabla_afectada, id_registro_afectado, detalle) " +
                            "VALUES (CURRENT_TIMESTAMP, ?, 'RECHAZO', 'movimiento_inventario', ?, ?)";
            try (PreparedStatement psAud = conn.prepareStatement(sqlAud)) {
                psAud.setInt(1, idUsuarioAutorizador);
                psAud.setInt(2, idMovimiento);
                psAud.setString(3, "Rechazo del documento soporte " + numDoc + ". Causal tecnica: " + motivoRechazo.trim());
                psAud.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommitOriginal);
        }
    }
}
