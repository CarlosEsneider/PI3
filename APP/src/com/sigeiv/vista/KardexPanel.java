package com.sigeiv.vista;

import com.sigeiv.controlador.KardexController;
import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.modelo.Kardex;
import com.sigeiv.modelo.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel del Libro Mayor de Kardex y Trazabilidad Inmutable (Fase 5 - RF-10, RNF-04).
 * Permite visualizar el historial cronológico oficial de asientos por producto terminado
 * o de forma global, con desglose de Entradas (+), Salidas (-), Saldos y Valorizaciones.
 * Arquitectura MVC estricta: consume KardexController.
 */
public class KardexPanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final KardexController kardexCtrl = new KardexController();

    private JComboBox<ProductoComboItem> cboProductos;
    private JTextField txtBuscarSku;
    private JTable tablaKardex;
    private DefaultTableModel modeloTabla;

    private JLabel lblTotalEntradas;
    private JLabel lblTotalSalidas;
    private JLabel lblSaldoActual;
    private JLabel lblValorizacionTotal;
    private JLabel lblIndiceRotacion;

    public KardexPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializarUI();
        cargarComboProductos();
        cargarDatosKardex();
    }

    private void inicializarUI() {
        // 1. PANEL SUPERIOR: Título y Controles de Filtro
        JPanel panelNorte = new JPanel(new BorderLayout(12, 10));
        panelNorte.setOpaque(false);

        JLabel lblTitulo = new JLabel("Libro Mayor de Kardex y Trazabilidad (RF-10)");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);
        panelNorte.add(lblTitulo, BorderLayout.NORTH);

        // Barra de selección y búsqueda
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        panelFiltros.setBackground(MainFrame.COLOR_PANEL);
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblProd = new JLabel("Producto:");
        lblProd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblProd.setForeground(MainFrame.COLOR_TEXTO);
        panelFiltros.add(lblProd);

        cboProductos = new JComboBox<>();
        cboProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboProductos.setPreferredSize(new Dimension(300, 28));
        cboProductos.setBackground(MainFrame.COLOR_FONDO);
        cboProductos.setForeground(MainFrame.COLOR_TEXTO);
        cboProductos.addActionListener(e -> cargarDatosKardex());
        panelFiltros.add(cboProductos);

        panelFiltros.add(Box.createHorizontalStrut(10));

        JLabel lblBuscar = new JLabel("Buscar SKU / Nombre:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBuscar.setForeground(MainFrame.COLOR_TEXTO_SEC);
        panelFiltros.add(lblBuscar);

        txtBuscarSku = new JTextField(15);
        txtBuscarSku.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscarSku.setBackground(MainFrame.COLOR_FONDO);
        txtBuscarSku.setForeground(MainFrame.COLOR_TEXTO);
        txtBuscarSku.setCaretColor(MainFrame.COLOR_TEXTO);
        txtBuscarSku.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1),
            new EmptyBorder(4, 6, 4, 6)
        ));
        txtBuscarSku.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarPorTexto(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarPorTexto(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarPorTexto(); }
        });
        panelFiltros.add(txtBuscarSku);

        JButton btnRefrescar = ProductoPanel.crearBoton("Actualizar", MainFrame.COLOR_PRIMARIO);
        btnRefrescar.setPreferredSize(new Dimension(100, 28));
        btnRefrescar.addActionListener(e -> {
            cargarComboProductos();
            cargarDatosKardex();
        });
        panelFiltros.add(btnRefrescar);

        panelNorte.add(panelFiltros, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        // 2. PANEL CENTRAL: Tarjetas de Métricas + Tabla de Asientos
        JPanel panelCentro = new JPanel(new BorderLayout(0, 12));
        panelCentro.setOpaque(false);

        // Tarjetas superiores
        JPanel panelCards = new JPanel(new GridLayout(1, 5, 10, 0));
        panelCards.setOpaque(false);
        panelCards.setPreferredSize(new Dimension(0, 75));

        lblTotalEntradas = crearTarjeta(panelCards, "Total Entradas (+)", "0", new Color(46, 204, 113));
        lblTotalSalidas = crearTarjeta(panelCards, "Total Salidas (-)", "0", new Color(231, 76, 60));
        lblSaldoActual = crearTarjeta(panelCards, "Saldo en Stock", "0", new Color(52, 152, 219));
        lblValorizacionTotal = crearTarjeta(panelCards, "Valorización Saldo", "$0", MainFrame.COLOR_PRIMARIO);
        lblIndiceRotacion = crearTarjeta(panelCards, "Índice Rotación", "0.00x", new Color(241, 196, 15));

        panelCentro.add(panelCards, BorderLayout.NORTH);

        // Tabla oficial de Kardex
        String[] columnas = {
            "ID", "Fecha / Hora", "Tipo Evento", "N.° Soporte",
            "Registrado Por", "Autorizado Por",
            "Entrada (+)", "Salida (-)", "Saldo Stock",
            "Costo Unitario", "Saldo Valorizado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaKardex = new JTable(modeloTabla);
        ProductoPanel.estilizarTabla(tablaKardex);

        // Renderers especializados para columnas numéricas y de tipo
        configurarRenderersTabla();

        JScrollPane scrollTabla = new JScrollPane(tablaKardex);
        scrollTabla.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scrollTabla.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        panelCentro.add(scrollTabla, BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        // 3. PANEL SUR: Leyenda y nota de inmutabilidad (RNF-04)
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setOpaque(false);

        JLabel lblLeyenda = new JLabel("🔒 Libro Mayor Inmutable (RNF-04): Los asientos de Kardex se asientan exclusivamente tras autorización de documentos soporte y no permiten borrado ni edición.");
        lblLeyenda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblLeyenda.setForeground(MainFrame.COLOR_TEXTO_SEC);
        panelSur.add(lblLeyenda, BorderLayout.WEST);

        add(panelSur, BorderLayout.SOUTH);
    }

    private void configurarRenderersTabla() {
        // Anchos de columna optimizados
        tablaKardex.getColumnModel().getColumn(0).setPreferredWidth(45);   // ID
        tablaKardex.getColumnModel().getColumn(1).setPreferredWidth(125);  // Fecha
        tablaKardex.getColumnModel().getColumn(2).setPreferredWidth(95);   // Tipo Evento
        tablaKardex.getColumnModel().getColumn(3).setPreferredWidth(100);  // N Soporte
        tablaKardex.getColumnModel().getColumn(4).setPreferredWidth(110);  // Registrado
        tablaKardex.getColumnModel().getColumn(5).setPreferredWidth(120);  // Autorizado
        tablaKardex.getColumnModel().getColumn(6).setPreferredWidth(85);   // Entrada
        tablaKardex.getColumnModel().getColumn(7).setPreferredWidth(85);   // Salida
        tablaKardex.getColumnModel().getColumn(8).setPreferredWidth(85);   // Saldo
        tablaKardex.getColumnModel().getColumn(9).setPreferredWidth(95);   // Costo Unit
        tablaKardex.getColumnModel().getColumn(10).setPreferredWidth(110); // Saldo Val

        // Columna Entrada (+): verde
        tablaKardex.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, val, sel, foc, r, c);
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                int cant = val != null ? (Integer) val : 0;
                if (cant > 0) {
                    l.setForeground(new Color(46, 204, 113));
                    l.setText("+" + cant);
                } else {
                    l.setForeground(MainFrame.COLOR_TEXTO_SEC);
                    l.setText("-");
                }
                return l;
            }
        });

        // Columna Salida (-): rojo
        tablaKardex.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, val, sel, foc, r, c);
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                int cant = val != null ? (Integer) val : 0;
                if (cant > 0) {
                    l.setForeground(new Color(231, 76, 60));
                    l.setText("-" + cant);
                } else {
                    l.setForeground(MainFrame.COLOR_TEXTO_SEC);
                    l.setText("-");
                }
                return l;
            }
        });

        // Columna Saldo Stock: azul / blanco
        tablaKardex.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, val, sel, foc, r, c);
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setForeground(new Color(52, 152, 219));
                return l;
            }
        });

        // Alineación a la derecha para costos
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaKardex.getColumnModel().getColumn(9).setCellRenderer(rightRenderer);
        tablaKardex.getColumnModel().getColumn(10).setCellRenderer(rightRenderer);
    }

    private void cargarComboProductos() {
        cboProductos.removeAllItems();
        cboProductos.addItem(new ProductoComboItem(0, "TODOS LOS PRODUCTOS (Historial Consolidado)", ""));
        List<Producto> productos = kardexCtrl.listarProductosDisponibles();
        for (Producto p : productos) {
            String sku = (p.getCodigoSku() != null && !p.getCodigoSku().isEmpty()) ? p.getCodigoSku() : "PROD-" + p.getIdProducto();
            cboProductos.addItem(new ProductoComboItem(p.getIdProducto(), p.getNombreProducto(), sku));
        }
    }

    private void cargarDatosKardex() {
        ProductoComboItem item = (ProductoComboItem) cboProductos.getSelectedItem();
        if (item == null) return;

        modeloTabla.setRowCount(0);
        List<Kardex> asientos;

        if (item.idProducto == 0) {
            asientos = kardexCtrl.listarTodoKardex();
            // Métricas globales
            int totEnt = 0;
            int totSal = 0;
            double totVal = 0.0;
            for (Kardex k : asientos) {
                totEnt += k.getCantidadEntrada();
                totSal += k.getCantidadSalida();
            }
            lblTotalEntradas.setText(String.valueOf(totEnt));
            lblTotalSalidas.setText(String.valueOf(totSal));
            lblSaldoActual.setText(String.valueOf(totEnt - totSal));
            lblValorizacionTotal.setText("N/A (Multi-item)");
            lblIndiceRotacion.setText(totEnt > 0 ? String.format("%.2fx", (double) totSal / Math.max(totEnt - totSal, 1)) : "0.00x");
        } else {
            asientos = kardexCtrl.listarKardexPorProducto(item.idProducto);
            int totEnt = kardexCtrl.obtenerTotalEntradas(item.idProducto);
            int totSal = kardexCtrl.obtenerTotalSalidas(item.idProducto);
            int saldo = kardexCtrl.obtenerSaldoActual(item.idProducto);
            double val = kardexCtrl.calcularValorizacionSaldo(item.idProducto);
            double rot = kardexCtrl.calcularIndiceRotacion(item.idProducto);

            lblTotalEntradas.setText(String.valueOf(totEnt));
            lblTotalSalidas.setText(String.valueOf(totSal));
            lblSaldoActual.setText(String.valueOf(saldo));
            lblValorizacionTotal.setText(String.format("$%,.2f", val));
            lblIndiceRotacion.setText(String.format("%.2fx", rot));
        }

        for (Kardex k : asientos) {
            modeloTabla.addRow(new Object[]{
                k.getIdKardex(),
                k.getFechaHora(),
                k.getTipoEvento(),
                k.getNumeroSoporte(),
                k.getRegistradoPor() != null ? k.getRegistradoPor() : "Sistema",
                k.getAutorizadoPor() != null ? k.getAutorizadoPor() : "N/A",
                k.getCantidadEntrada(),
                k.getCantidadSalida(),
                k.getSaldoStock(),
                String.format("$%,.2f", k.getCostoUnitario()),
                String.format("$%,.2f", k.getCostoTotalSaldo())
            });
        }
    }

    private void filtrarPorTexto() {
        String texto = txtBuscarSku.getText().trim();
        if (texto.isEmpty()) {
            cargarDatosKardex();
            return;
        }

        modeloTabla.setRowCount(0);
        List<Kardex> filtrados = kardexCtrl.buscarPorSkuONombre(texto);
        int totEnt = 0;
        int totSal = 0;

        for (Kardex k : filtrados) {
            totEnt += k.getCantidadEntrada();
            totSal += k.getCantidadSalida();
            modeloTabla.addRow(new Object[]{
                k.getIdKardex(),
                k.getFechaHora(),
                k.getTipoEvento(),
                k.getNumeroSoporte(),
                k.getRegistradoPor() != null ? k.getRegistradoPor() : "Sistema",
                k.getAutorizadoPor() != null ? k.getAutorizadoPor() : "N/A",
                k.getCantidadEntrada(),
                k.getCantidadSalida(),
                k.getSaldoStock(),
                String.format("$%,.2f", k.getCostoUnitario()),
                String.format("$%,.2f", k.getCostoTotalSaldo())
            });
        }

        lblTotalEntradas.setText(String.valueOf(totEnt));
        lblTotalSalidas.setText(String.valueOf(totSal));
        lblSaldoActual.setText(String.valueOf(totEnt - totSal));
        lblValorizacionTotal.setText("Filtrado");
    }

    private JLabel crearTarjeta(JPanel contenedor, String titulo, String valor, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.COLOR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTit);

        card.add(Box.createVerticalStrut(4));

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblVal.setForeground(color);
        lblVal.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblVal);

        contenedor.add(card);
        return lblVal;
    }

    /**
     * Clase auxiliar para elementos del JComboBox de productos.
     */
    private static class ProductoComboItem {
        final int idProducto;
        final String nombre;
        final String sku;

        ProductoComboItem(int idProducto, String nombre, String sku) {
            this.idProducto = idProducto;
            this.nombre = nombre;
            this.sku = sku;
        }

        @Override
        public String toString() {
            if (idProducto == 0) return nombre;
            return "[" + sku + "] " + nombre;
        }
    }
}
