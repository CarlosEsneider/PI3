package com.sigeiv.vista;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.dao.ProductoDAO;
import com.sigeiv.dao.CategoriaDAO;
import com.sigeiv.dao.ProveedorDAO;
import com.sigeiv.modelo.Producto;
import com.sigeiv.modelo.Categoria;
import com.sigeiv.modelo.Proveedor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de Inventario.
 * Muestra el estado del stock, alertas de stock bajo y permite filtrar.
 */
public class InventarioPanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalProductos, lblStockBajo, lblValorInventario;
    private JCheckBox chkSoloStockBajo;

    public InventarioPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializar();
        cargarDatos();
    }

    private void inicializar() {
        // Titulo
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);
        JLabel lblTitulo = new JLabel("Control de Inventario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);
        panelTop.add(lblTitulo, BorderLayout.WEST);

        chkSoloStockBajo = new JCheckBox("Solo productos con stock bajo");
        chkSoloStockBajo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkSoloStockBajo.setForeground(MainFrame.COLOR_ALERTA);
        chkSoloStockBajo.setOpaque(false);
        chkSoloStockBajo.addActionListener(e -> cargarDatos());
        panelTop.add(chkSoloStockBajo, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        // Tarjetas de resumen
        JPanel panelCards = new JPanel(new GridLayout(1, 3, 12, 0));
        panelCards.setOpaque(false);
        panelCards.setPreferredSize(new Dimension(0, 80));

        lblTotalProductos = crearTarjeta(panelCards, "Total Productos", "0", MainFrame.COLOR_PRIMARIO);
        lblStockBajo = crearTarjeta(panelCards, "Stock Bajo", "0", MainFrame.COLOR_ALERTA);
        lblValorInventario = crearTarjeta(panelCards, "Valor Inventario", "$0", MainFrame.COLOR_EXITO);

        JPanel panelMedio = new JPanel(new BorderLayout(0, 12));
        panelMedio.setOpaque(false);
        panelMedio.add(panelCards, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"ID", "Producto", "Categoria", "Proveedor", "Precio", "Stock Actual", "Stock Minimo", "Estado"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        ProductoPanel.estilizarTabla(tabla);

        // Renderer para colorear el estado
        tabla.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if ("BAJO".equals(value)) {
                    label.setForeground(MainFrame.COLOR_ERROR);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    label.setForeground(MainFrame.COLOR_EXITO);
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    // Mantener el color si es BAJO pero quizás más brillante o blanco para contraste
                    if (!"BAJO".equals(value)) label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(MainFrame.COLOR_PANEL);
                }
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scroll.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        panelMedio.add(scroll, BorderLayout.CENTER);

        add(panelMedio, BorderLayout.CENTER);

        // Boton refrescar
        JButton btnRefrescar = ProductoPanel.crearBoton("Actualizar", MainFrame.COLOR_PRIMARIO);
        btnRefrescar.addActionListener(e -> cargarDatos());
        JPanel panelBtm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtm.setOpaque(false);
        panelBtm.add(btnRefrescar);
        add(panelBtm, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Producto> productos;

        if (chkSoloStockBajo.isSelected()) {
            productos = productoDAO.productosStockBajo();
        } else {
            productos = productoDAO.listarTodos();
        }

        List<Categoria> categorias = categoriaDAO.listarTodos();
        List<Proveedor> proveedores = proveedorDAO.listarTodos();

        int totalProductos = productos.size();
        int stockBajoCount = 0;
        double valorTotal = 0;

        for (Producto p : productos) {
            String catNombre = categorias.stream()
                .filter(c -> c.getIdCategoria() == p.getIdCategoria())
                .map(Categoria::getNombreCategoria).findFirst().orElse("-");
            String provNombre = proveedores.stream()
                .filter(pr -> pr.getIdProveedor() == p.getIdProveedor())
                .map(Proveedor::getEmpresa).findFirst().orElse("-");

            boolean bajo = p.isStockBajo();
            if (bajo) stockBajoCount++;
            valorTotal += p.getPrecio() * p.getStockActual();

            modeloTabla.addRow(new Object[]{
                p.getIdProducto(), p.getNombreProducto(), catNombre, provNombre,
                String.format("$%,.2f", p.getPrecio()),
                p.getStockActual(), p.getStockMinimo(),
                bajo ? "BAJO" : "OK"
            });
        }

        // Actualizar tarjetas (para el total real, leer todos si filtramos)
        if (chkSoloStockBajo.isSelected()) {
            List<Producto> todos = productoDAO.listarTodos();
            totalProductos = todos.size();
            valorTotal = 0;
            stockBajoCount = 0;
            for (Producto p : todos) {
                valorTotal += p.getPrecio() * p.getStockActual();
                if (p.isStockBajo()) stockBajoCount++;
            }
        }

        lblTotalProductos.setText(String.valueOf(totalProductos));
        lblStockBajo.setText(String.valueOf(stockBajoCount));
        lblValorInventario.setText("$" + String.format("%,.0f", valorTotal));
    }

    private JLabel crearTarjeta(JPanel contenedor, String titulo, String valor, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.COLOR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true),
            new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTit);

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblVal.setForeground(color);
        lblVal.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblVal);

        contenedor.add(card);
        return lblVal;
    }
}






