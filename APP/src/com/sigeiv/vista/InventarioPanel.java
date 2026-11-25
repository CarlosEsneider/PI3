package com.sigeiv.vista;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.controlador.InventarioController;
import com.sigeiv.controlador.CategoriaController;
import com.sigeiv.controlador.ProveedorController;
import com.sigeiv.modelo.Producto;
import com.sigeiv.modelo.Categoria;
import com.sigeiv.modelo.Proveedor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de Inventario con Semaforización Visual Oficial (Fase 5 - RF-11).
 * Implementa la escala tricolor obligatoria de Volcano Barriles:
 *  - VERDE (ÓPTIMO): Stock > Stock Mínimo * 1.5
 *  - AMARILLO (PREVENTIVO): Stock Mínimo < Stock <= Stock Mínimo * 1.5
 *  - ROJO (CRÍTICO / AGOTADO): Stock <= Stock Mínimo o Stock = 0
 * Consume InventarioController bajo el estricto patrón MVC.
 */
public class InventarioPanel extends JPanel {

    public static final Color COLOR_SEMAFORO_VERDE = new Color(46, 204, 113);
    public static final Color COLOR_SEMAFORO_AMARILLO = new Color(243, 156, 18);
    public static final Color COLOR_SEMAFORO_ROJO = new Color(231, 76, 60);

    private final UsuarioController usuarioCtrl;
    private final InventarioController inventarioCtrl = new InventarioController();
    private final CategoriaController categoriaCtrl = new CategoriaController();
    private final ProveedorController proveedorCtrl = new ProveedorController();

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalProductos, lblOptimo, lblPreventivo, lblCritico, lblValorInventario;
    private JComboBox<String> cboFiltroSemaforo;

    public InventarioPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializar();
        cargarDatos();
    }

    private void inicializar() {
        // Encabezado superior
        JPanel panelTop = new JPanel(new BorderLayout(12, 0));
        panelTop.setOpaque(false);

        JLabel lblTitulo = new JLabel("Control y Semaforización de Inventario (RF-11)");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);
        panelTop.add(lblTitulo, BorderLayout.WEST);

        // Selector de filtro por estado semafórico
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelFiltro.setOpaque(false);

        JLabel lblFiltro = new JLabel("Filtrar Semáforo:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFiltro.setForeground(MainFrame.COLOR_TEXTO_SEC);
        panelFiltro.add(lblFiltro);

        cboFiltroSemaforo = new JComboBox<>(new String[]{
            "Todos los Estados",
            "🟢 Solo Óptimo (>1.5× Min)",
            "🟡 Solo Preventivo (≤1.5× Min)",
            "🔴 Solo Críticos / Agotados (≤ Min)"
        });
        cboFiltroSemaforo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboFiltroSemaforo.setBackground(MainFrame.COLOR_PANEL);
        cboFiltroSemaforo.setForeground(MainFrame.COLOR_TEXTO);
        cboFiltroSemaforo.addActionListener(e -> cargarDatos());
        panelFiltro.add(cboFiltroSemaforo);

        panelTop.add(panelFiltro, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        // Panel de tarjetas resumen de semaforización
        JPanel panelCards = new JPanel(new GridLayout(1, 5, 10, 0));
        panelCards.setOpaque(false);
        panelCards.setPreferredSize(new Dimension(0, 80));

        lblTotalProductos = crearTarjeta(panelCards, "Catálogo Total", "0", MainFrame.COLOR_TEXTO);
        lblOptimo = crearTarjeta(panelCards, "🟢 Óptimo", "0", COLOR_SEMAFORO_VERDE);
        lblPreventivo = crearTarjeta(panelCards, "🟡 Preventivo", "0", COLOR_SEMAFORO_AMARILLO);
        lblCritico = crearTarjeta(panelCards, "🔴 Crítico / Taller", "0", COLOR_SEMAFORO_ROJO);
        lblValorInventario = crearTarjeta(panelCards, "Valorización Total", "$0", MainFrame.COLOR_PRIMARIO);

        JPanel panelMedio = new JPanel(new BorderLayout(0, 12));
        panelMedio.setOpaque(false);
        panelMedio.add(panelCards, BorderLayout.NORTH);

        // Tabla con semaforización
        String[] cols = {"ID", "SKU", "Producto", "Categoría", "Proveedor", "Precio", "Stock Actual", "Stock Mínimo", "Semáforo (RF-11)"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        ProductoPanel.estilizarTabla(tabla);

        // Renderer exclusivo para la columna de semaforización (columna 8)
        tabla.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";

                if (val.contains("CRÍTICO")) {
                    label.setForeground(COLOR_SEMAFORO_ROJO);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if (val.contains("PREVENTIVO")) {
                    label.setForeground(COLOR_SEMAFORO_AMARILLO);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    label.setForeground(COLOR_SEMAFORO_VERDE);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }

                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                } else {
                    label.setBackground(MainFrame.COLOR_PANEL);
                }
                return label;
            }
        });

        // Configurar anchos de columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(210);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(95);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(8).setPreferredWidth(130);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scroll.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        panelMedio.add(scroll, BorderLayout.CENTER);

        add(panelMedio, BorderLayout.CENTER);

        // Barra inferior de acciones
        JPanel panelBtm = new JPanel(new BorderLayout());
        panelBtm.setOpaque(false);

        JLabel lblLeyenda = new JLabel("🟢 Óptimo: Stock > 1.5× Min   |   🟡 Preventivo: Min < Stock ≤ 1.5× Min   |   🔴 Crítico: Stock ≤ Min (Alerta Taller)");
        lblLeyenda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblLeyenda.setForeground(MainFrame.COLOR_TEXTO_SEC);
        panelBtm.add(lblLeyenda, BorderLayout.WEST);

        JButton btnRefrescar = ProductoPanel.crearBoton("Actualizar Inventario", MainFrame.COLOR_PRIMARIO);
        btnRefrescar.addActionListener(e -> cargarDatos());
        panelBtm.add(btnRefrescar, BorderLayout.EAST);

        add(panelBtm, BorderLayout.SOUTH);
    }

    public void cargarDatos() {
        modeloTabla.setRowCount(0);

        int idx = cboFiltroSemaforo != null ? cboFiltroSemaforo.getSelectedIndex() : 0;
        String filtro = "TODOS";
        if (idx == 1) filtro = "OPTIMO";
        else if (idx == 2) filtro = "PREVENTIVO";
        else if (idx == 3) filtro = "CRITICO";

        List<Producto> productos = inventarioCtrl.filtrarPorSemaforo(filtro);
        List<Categoria> categorias = categoriaCtrl.listarTodos();
        List<Proveedor> proveedores = proveedorCtrl.listarTodos();

        for (Producto p : productos) {
            String catNombre = categorias.stream()
                .filter(c -> c.getIdCategoria() == p.getIdCategoria())
                .map(Categoria::getNombreCategoria).findFirst().orElse("-");
            String provNombre = proveedores.stream()
                .filter(pr -> pr.getIdProveedor() == p.getIdProveedor())
                .map(Proveedor::getEmpresa).findFirst().orElse("-");

            String semaforo = inventarioCtrl.determinarEstadoSemaforo(p);
            String etiquetaSemaforo;
            if ("CRITICO".equals(semaforo)) {
                etiquetaSemaforo = "🔴 CRÍTICO";
            } else if ("PREVENTIVO".equals(semaforo)) {
                etiquetaSemaforo = "🟡 PREVENTIVO";
            } else {
                etiquetaSemaforo = "🟢 ÓPTIMO";
            }

            modeloTabla.addRow(new Object[]{
                p.getIdProducto(),
                (p.getCodigoSku() != null && !p.getCodigoSku().isEmpty()) ? p.getCodigoSku() : "PROD-" + p.getIdProducto(),
                p.getNombreProducto(),
                catNombre,
                provNombre,
                String.format("$%,.2f", p.getPrecio()),
                p.getStockActual(),
                p.getStockMinimo(),
                etiquetaSemaforo
            });
        }

        // Actualizar tarjetas de conteo
        lblTotalProductos.setText(String.valueOf(inventarioCtrl.obtenerTotalProductos()));
        lblOptimo.setText(String.valueOf(inventarioCtrl.obtenerTotalOptimo()));
        lblPreventivo.setText(String.valueOf(inventarioCtrl.obtenerTotalPreventivo()));
        lblCritico.setText(String.valueOf(inventarioCtrl.obtenerTotalCritico()));
        lblValorInventario.setText("$" + String.format("%,.0f", inventarioCtrl.calcularValorTotalInventario()));
    }

    private JLabel crearTarjeta(JPanel contenedor, String titulo, String valor, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.COLOR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTit);

        card.add(Box.createVerticalStrut(4));

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(color);
        lblVal.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblVal);

        contenedor.add(card);
        return lblVal;
    }
}
