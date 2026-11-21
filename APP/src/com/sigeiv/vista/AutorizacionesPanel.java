package com.sigeiv.vista;

import com.sigeiv.controlador.MovimientoInventarioController;
import com.sigeiv.controlador.MovimientoInventarioController.ResultadoOperacion;
import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.modelo.DetalleMovimiento;
import com.sigeiv.modelo.MovimientoInventario;
import com.sigeiv.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Bandeja Centralizada de Autorizaciones Pendientes (Fase 3 - OE3, RNF-05).
 * 
 * Permite a los roles competentes (Gerente/Admin, Jefe de Produccion, Asesor Comercial)
 * revisar y autorizar digitalmente o rechazar con justificacion tecnica los documentos
 * soporte que alteran el inventario fisico y el libro mayor inmutable de Kardex.
 */
public class AutorizacionesPanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final MovimientoInventarioController movimientoCtrl = new MovimientoInventarioController();

    // Componentes de la Tabla Superior (Cabeceras)
    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;

    // Componentes de la Tabla Inferior (Detalles de asadores)
    private JTable tablaDetalles;
    private DefaultTableModel modeloDetalles;

    // Etiquetas de estado e informacion
    private JLabel lblContadorPendientes;
    private JLabel lblDocSeleccionadoInfo;
    private JLabel lblRolRequeridoBadge;

    // Botones de accion
    private JButton btnAutorizar;
    private JButton btnRechazar;
    private JButton btnRefrescar;

    // Movimiento actualmente seleccionado
    private MovimientoInventario movimientoActual;

    public AutorizacionesPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        inicializarComponentes();
        cargarPendientes();
    }

    private void inicializarComponentes() {
        // ==========================================
        // 1. ENCABEZADO SUPERIOR
        // ==========================================
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);

        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Bandeja de Autorizaciones Pendientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);

        JLabel lblSubtitulo = new JLabel("Flujo jerárquico de control y aprobación de documentos soporte (OE3, RNF-05)");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(MainFrame.COLOR_TEXTO_SEC);

        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createVerticalStrut(4));
        panelTitulos.add(lblSubtitulo);
        panelHeader.add(panelTitulos, BorderLayout.WEST);

        // Barra derecha de herramientas
        JPanel panelHerramientas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelHerramientas.setOpaque(false);

        lblContadorPendientes = new JLabel("0 pendientes");
        lblContadorPendientes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContadorPendientes.setForeground(MainFrame.COLOR_ALERTA);
        lblContadorPendientes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COLOR_ALERTA, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        btnRefrescar = ProductoPanel.crearBoton("Refrescar Bandeja", MainFrame.COLOR_CAMPO);
        btnRefrescar.setForeground(MainFrame.COLOR_TEXTO);
        btnRefrescar.setBorder(BorderFactory.createLineBorder(MainFrame.COLOR_BORDE, 1));
        btnRefrescar.addActionListener(e -> cargarPendientes());

        panelHerramientas.add(lblContadorPendientes);
        panelHerramientas.add(btnRefrescar);
        panelHeader.add(panelHerramientas, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // ==========================================
        // 2. CONTENEDOR CENTRAL DIVIDIDO (SPLIT PANE)
        // ==========================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        // --- SECCION SUPERIOR: TABLA DE DOCUMENTOS SOPORTE ---
        JPanel panelSuperior = new JPanel(new BorderLayout(0, 8));
        panelSuperior.setOpaque(false);

        JLabel lblSeccionSup = new JLabel("Documentos Soporte en Estado PENDIENTE:");
        lblSeccionSup.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSeccionSup.setForeground(MainFrame.COLOR_TEXTO);
        panelSuperior.add(lblSeccionSup, BorderLayout.NORTH);

        String[] colsSup = {"ID", "N.° Documento", "Fecha/Hora", "Tipo de Movimiento", "Usuario Creador", "Observaciones"};
        modeloMovimientos = new DefaultTableModel(colsSup, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaMovimientos = new JTable(modeloMovimientos);
        ProductoPanel.estilizarTabla(tablaMovimientos);
        configurarAnchosColumnasSuperior();

        // Listener de seleccion para cargar detalles
        tablaMovimientos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onMovimientoSeleccionado();
            }
        });

        JScrollPane scrollSup = new JScrollPane(tablaMovimientos);
        scrollSup.setBorder(new MatteBorder(1, 1, 1, 1, MainFrame.COLOR_BORDE));
        scrollSup.getViewport().setBackground(MainFrame.COLOR_FONDO);
        panelSuperior.add(scrollSup, BorderLayout.CENTER);

        splitPane.setTopComponent(panelSuperior);

        // --- SECCION INFERIOR: DETALLES DE ASADORES Y ACCIONES ---
        JPanel panelInferior = new JPanel(new BorderLayout(0, 8));
        panelInferior.setOpaque(false);

        // Cabecera de la seccion de detalle
        JPanel panelInfoDetalle = new JPanel(new BorderLayout());
        panelInfoDetalle.setOpaque(false);

        lblDocSeleccionadoInfo = new JLabel("Seleccione un documento superior para visualizar sus asadores");
        lblDocSeleccionadoInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDocSeleccionadoInfo.setForeground(MainFrame.COLOR_TEXTO);

        lblRolRequeridoBadge = new JLabel("");
        lblRolRequeridoBadge.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRolRequeridoBadge.setForeground(MainFrame.COLOR_TEXTO_SEC);

        panelInfoDetalle.add(lblDocSeleccionadoInfo, BorderLayout.WEST);
        panelInfoDetalle.add(lblRolRequeridoBadge, BorderLayout.EAST);
        panelInferior.add(panelInfoDetalle, BorderLayout.NORTH);

        // Tabla de detalles
        String[] colsInf = {"ID Prod.", "Producto", "Código SKU", "Cantidad Solicitada", "Costo Unitario", "Stock en Bodega"};
        modeloDetalles = new DefaultTableModel(colsInf, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDetalles = new JTable(modeloDetalles);
        ProductoPanel.estilizarTabla(tablaDetalles);
        configurarAnchosColumnasInferior();

        JScrollPane scrollInf = new JScrollPane(tablaDetalles);
        scrollInf.setBorder(new MatteBorder(1, 1, 1, 1, MainFrame.COLOR_BORDE));
        scrollInf.getViewport().setBackground(MainFrame.COLOR_FONDO);
        panelInferior.add(scrollInf, BorderLayout.CENTER);

        // Barra inferior de botones de accion (Autorizar / Rechazar)
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        panelAcciones.setBackground(MainFrame.COLOR_PANEL);
        panelAcciones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MainFrame.COLOR_BORDE));

        btnRechazar = ProductoPanel.crearBoton("Rechazar Documento", MainFrame.COLOR_ERROR);
        btnRechazar.setPreferredSize(new Dimension(180, 36));
        btnRechazar.setEnabled(false);
        btnRechazar.addActionListener(e -> ejecutarRechazo());

        btnAutorizar = ProductoPanel.crearBoton("Autorizar Documento", MainFrame.COLOR_EXITO);
        btnAutorizar.setPreferredSize(new Dimension(190, 36));
        btnAutorizar.setEnabled(false);
        btnAutorizar.addActionListener(e -> ejecutarAutorizacion());

        panelAcciones.add(btnRechazar);
        panelAcciones.add(btnAutorizar);
        panelInferior.add(panelAcciones, BorderLayout.SOUTH);

        splitPane.setBottomComponent(panelInferior);

        add(splitPane, BorderLayout.CENTER);
    }

    private void configurarAnchosColumnasSuperior() {
        tablaMovimientos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaMovimientos.getColumnModel().getColumn(0).setMaxWidth(60);
        tablaMovimientos.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaMovimientos.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaMovimientos.getColumnModel().getColumn(3).setPreferredWidth(210);
        tablaMovimientos.getColumnModel().getColumn(4).setPreferredWidth(160);
        tablaMovimientos.getColumnModel().getColumn(5).setPreferredWidth(320);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tablaMovimientos.getColumnModel().getColumn(0).setCellRenderer(centrado);
        tablaMovimientos.getColumnModel().getColumn(1).setCellRenderer(centrado);
        tablaMovimientos.getColumnModel().getColumn(2).setCellRenderer(centrado);
    }

    private void configurarAnchosColumnasInferior() {
        tablaDetalles.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaDetalles.getColumnModel().getColumn(0).setMaxWidth(70);
        tablaDetalles.getColumnModel().getColumn(1).setPreferredWidth(260);
        tablaDetalles.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaDetalles.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaDetalles.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaDetalles.getColumnModel().getColumn(5).setPreferredWidth(130);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tablaDetalles.getColumnModel().getColumn(0).setCellRenderer(centrado);
        tablaDetalles.getColumnModel().getColumn(2).setCellRenderer(centrado);
        tablaDetalles.getColumnModel().getColumn(3).setCellRenderer(centrado);
        tablaDetalles.getColumnModel().getColumn(4).setCellRenderer(centrado);
        tablaDetalles.getColumnModel().getColumn(5).setCellRenderer(centrado);
    }

    /**
     * Carga todos los movimientos en estado PENDIENTE en la tabla superior.
     */
    public void cargarPendientes() {
        modeloMovimientos.setRowCount(0);
        modeloDetalles.setRowCount(0);
        movimientoActual = null;
        btnAutorizar.setEnabled(false);
        btnRechazar.setEnabled(false);
        lblDocSeleccionadoInfo.setText("Seleccione un documento superior para visualizar sus asadores");
        lblRolRequeridoBadge.setText("");

        List<MovimientoInventario> pendientes = movimientoCtrl.listarPendientes();
        for (MovimientoInventario m : pendientes) {
            modeloMovimientos.addRow(new Object[]{
                m.getIdMovimiento(),
                m.getNumeroDocumentoSoporte(),
                m.getFechaHora(),
                m.getNombreTipoMovimiento(),
                m.getNombreUsuarioCreador(),
                m.getObservaciones()
            });
        }

        int total = pendientes.size();
        lblContadorPendientes.setText(total + (total == 1 ? " documento pendiente" : " documentos pendientes"));

        if (total > 0) {
            tablaMovimientos.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * Se dispara cuando el usuario selecciona un documento en la tabla superior.
     */
    private void onMovimientoSeleccionado() {
        int fila = tablaMovimientos.getSelectedRow();
        if (fila < 0) {
            movimientoActual = null;
            modeloDetalles.setRowCount(0);
            btnAutorizar.setEnabled(false);
            btnRechazar.setEnabled(false);
            lblDocSeleccionadoInfo.setText("Seleccione un documento superior para visualizar sus asadores");
            lblRolRequeridoBadge.setText("");
            return;
        }

        int idMov = (int) modeloMovimientos.getValueAt(fila, 0);
        List<MovimientoInventario> pendientes = movimientoCtrl.listarPendientes();
        movimientoActual = pendientes.stream()
            .filter(m -> m.getIdMovimiento() == idMov)
            .findFirst()
            .orElse(null);

        if (movimientoActual == null) return;

        // Cargar datos de cabecera
        lblDocSeleccionadoInfo.setText(
            "Documento Soporte: " + movimientoActual.getNumeroDocumentoSoporte() +
            "  |  Naturaleza: " + movimientoActual.getNaturalezaAfectacion() +
            "  |  Creado por: " + movimientoActual.getNombreUsuarioCreador()
        );
        lblRolRequeridoBadge.setText("Autorizador Requerido: " + movimientoActual.getRolAutorizadorRequerido());

        // Cargar los renglones de producto (DetalleMovimiento)
        modeloDetalles.setRowCount(0);
        List<DetalleMovimiento> detalles = movimientoCtrl.obtenerDetalles(idMov);
        for (DetalleMovimiento det : detalles) {
            modeloDetalles.addRow(new Object[]{
                det.getIdProducto(),
                det.getNombreProducto(),
                det.getCodigoSku(),
                det.getCantidad() + " und.",
                String.format("$%,.2f", det.getCostoUnitario()),
                det.getStockActualBodega() + " und."
            });
        }

        // Habilitar botones de accion
        btnAutorizar.setEnabled(true);
        btnRechazar.setEnabled(true);
    }

    /**
     * Accion del boton [Autorizar Documento].
     */
    private void ejecutarAutorizacion() {
        if (movimientoActual == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un documento soporte para autorizar.",
                "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioLogueado = usuarioCtrl.getUsuarioLogueado();
        if (usuarioLogueado == null) {
            JOptionPane.showMessageDialog(this,
                "No hay una sesión activa de usuario.",
                "Sesión inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmacion formal antes de alterar existencias y Kardex
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de AUTORIZAR el documento soporte " + movimientoActual.getNumeroDocumentoSoporte() + "?\n\n" +
            "Tipo: " + movimientoActual.getNombreTipoMovimiento() + "\n" +
            "Naturaleza: " + movimientoActual.getNaturalezaAfectacion() + "\n\n" +
            "Esta operación actualizará las existencias en bodega e insertará\n" +
            "los asientos contables inmutables en el libro mayor de KARDEX.",
            "Confirmación de Autorización - SIGEIV Volcano",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Ejecutar a traves del controlador con validacion de rol jerarquico
        ResultadoOperacion res = movimientoCtrl.autorizarMovimiento(movimientoActual.getIdMovimiento(), usuarioLogueado);

        if (res.isExito()) {
            JOptionPane.showMessageDialog(this,
                res.getMensaje(),
                "Documento Autorizado",
                JOptionPane.INFORMATION_MESSAGE);
            cargarPendientes();
        } else {
            JOptionPane.showMessageDialog(this,
                res.getMensaje(),
                "Control de Autorizaciones / Advertencia",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Accion del boton [Rechazar Documento].
     */
    private void ejecutarRechazo() {
        if (movimientoActual == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un documento soporte para rechazar.",
                "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioLogueado = usuarioCtrl.getUsuarioLogueado();
        if (usuarioLogueado == null) {
            JOptionPane.showMessageDialog(this,
                "No hay una sesión activa de usuario.",
                "Sesión inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Solicitar dictamen o motivo de rechazo obligatorio
        String motivo = JOptionPane.showInputDialog(this,
            "Ingrese la justificación técnica o dictamen obligatorio de rechazo\npara el documento " +
            movimientoActual.getNumeroDocumentoSoporte() + ":",
            "Dictamen de Rechazo - SIGEIV Volcano",
            JOptionPane.WARNING_MESSAGE);

        if (motivo == null) {
            return; // Cancelo el cuadro de dialogo
        }

        if (motivo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El dictamen de rechazo no puede estar vacío.\nDebe justificar la causal técnica según la norma del sistema.",
                "Causal Obligatoria",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        ResultadoOperacion res = movimientoCtrl.rechazarMovimiento(
            movimientoActual.getIdMovimiento(), usuarioLogueado, motivo.trim());

        if (res.isExito()) {
            JOptionPane.showMessageDialog(this,
                res.getMensaje(),
                "Documento Rechazado",
                JOptionPane.INFORMATION_MESSAGE);
            cargarPendientes();
        } else {
            JOptionPane.showMessageDialog(this,
                res.getMensaje(),
                "Error al procesar rechazo",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
