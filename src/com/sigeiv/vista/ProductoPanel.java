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
import java.awt.event.*;
import java.util.List;

/**
 * Panel de gestion de Productos con CRUD completo.
 * Incluye tabla, formulario, busqueda y filtrado por categoria.
 */
public class ProductoPanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar, txtNombre, txtPrecio, txtStockActual, txtStockMinimo, txtImgUrl;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<Proveedor> cmbProveedor;
    private JComboBox<String> cmbFiltroCategoria;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnLimpiar;
    private int idSeleccionado = -1;

    public ProductoPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializar();
        cargarTabla();
    }

    private void inicializar() {
        // === PANEL SUPERIOR: Titulo + Busqueda ===
        JPanel panelSuperior = new JPanel(new BorderLayout(12, 0));
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestion de Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        // Busqueda y filtro
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelFiltros.setOpaque(false);

        cmbFiltroCategoria = new JComboBox<>();
        cmbFiltroCategoria.addItem("Todas las categorias");
        for (Categoria c : categoriaDAO.listarTodos()) {
            cmbFiltroCategoria.addItem(c.getNombreCategoria());
        }
        estilizarCombo(cmbFiltroCategoria);
        cmbFiltroCategoria.setPreferredSize(new Dimension(180, 32));
        cmbFiltroCategoria.addActionListener(e -> filtrar());
        panelFiltros.add(cmbFiltroCategoria);

        txtBuscar = new JTextField(18);
        estilizarCampo(txtBuscar);
        añadirPlaceholder(txtBuscar, "Buscar...");
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { filtrar(); }
        });
        panelFiltros.add(txtBuscar);

        panelSuperior.add(panelFiltros, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // === TABLA ===
        String[] columnas = {"ID", "Nombre", "Precio", "Stock", "Minimo", "Categoria", "Proveedor"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        estilizarTabla(tabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarSeleccion();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scrollTabla.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        add(scrollTabla, BorderLayout.CENTER);

        // === PANEL DERECHO: Formulario ===
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(MainFrame.COLOR_PANEL);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));
        panelForm.setPreferredSize(new Dimension(280, 0));

        JLabel lblForm = new JLabel("Datos del Producto");
        lblForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblForm.setForeground(MainFrame.COLOR_PRIMARIO);
        lblForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelForm.add(lblForm);
        panelForm.add(Box.createVerticalStrut(16));

        txtNombre = new JTextField();
        panelForm.add(crearCampoFormulario("Nombre:", txtNombre));

        txtPrecio = new JTextField();
        panelForm.add(crearCampoFormulario("Precio:", txtPrecio));

        txtStockActual = new JTextField();
        panelForm.add(crearCampoFormulario("Stock Actual:", txtStockActual));

        txtStockMinimo = new JTextField();
        panelForm.add(crearCampoFormulario("Stock Minimo:", txtStockMinimo));

        cmbCategoria = new JComboBox<>();
        for (Categoria c : categoriaDAO.listarTodos()) cmbCategoria.addItem(c);
        estilizarCombo(cmbCategoria);
        panelForm.add(crearCampoFormulario("Categoria:", cmbCategoria));

        cmbProveedor = new JComboBox<>();
        for (Proveedor p : proveedorDAO.listarTodos()) cmbProveedor.addItem(p);
        estilizarCombo(cmbProveedor);
        panelForm.add(crearCampoFormulario("Proveedor:", cmbProveedor));

        txtImgUrl = new JTextField();
        panelForm.add(crearCampoFormulario("Imagen URL:", txtImgUrl));

        panelForm.add(Box.createVerticalStrut(12));

        // Botones CRUD
        JPanel panelBtns = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBtns.setOpaque(false);
        panelBtns.setMaximumSize(new Dimension(280, 80));
        panelBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnNuevo = crearBoton("Nuevo", MainFrame.COLOR_PRIMARIO);
        btnGuardar = crearBoton("Guardar", MainFrame.COLOR_EXITO);
        btnEliminar = crearBoton("Eliminar", MainFrame.COLOR_ERROR);
        btnLimpiar = crearBoton("Limpiar", MainFrame.COLOR_TEXTO_SEC);

        panelBtns.add(btnNuevo);
        panelBtns.add(btnGuardar);
        panelBtns.add(btnEliminar);
        panelBtns.add(btnLimpiar);
        panelForm.add(panelBtns);

        // Deshabilitar botones para consultor
        if (usuarioCtrl.esConsultor()) {
            btnNuevo.setEnabled(false);
            btnGuardar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }

        // Acciones
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());

        add(panelForm, BorderLayout.EAST);
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Producto> productos = productoDAO.listarTodos();
        List<Categoria> categorias = categoriaDAO.listarTodos();
        List<Proveedor> proveedores = proveedorDAO.listarTodos();

        for (Producto p : productos) {
            String catNombre = categorias.stream()
                .filter(c -> c.getIdCategoria() == p.getIdCategoria())
                .map(Categoria::getNombreCategoria).findFirst().orElse("-");
            String provNombre = proveedores.stream()
                .filter(pr -> pr.getIdProveedor() == p.getIdProveedor())
                .map(Proveedor::getEmpresa).findFirst().orElse("-");

            modeloTabla.addRow(new Object[]{
                p.getIdProducto(), p.getNombreProducto(),
                String.format("$%,.2f", p.getPrecio()),
                p.getStockActual(), p.getStockMinimo(),
                catNombre, provNombre
            });
        }
    }

    private void filtrar() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.equals("buscar...")) busqueda = "";
        int filtroIdx = cmbFiltroCategoria.getSelectedIndex();

        modeloTabla.setRowCount(0);
        List<Producto> productos;

        if (filtroIdx > 0) {
            List<Categoria> cats = categoriaDAO.listarTodos();
            if (filtroIdx - 1 < cats.size()) {
                productos = productoDAO.buscarPorCategoria(cats.get(filtroIdx - 1).getIdCategoria());
            } else {
                productos = productoDAO.listarTodos();
            }
        } else {
            productos = busqueda.isEmpty() ? productoDAO.listarTodos() : productoDAO.buscarPorNombre(busqueda);
        }

        List<Categoria> categorias = categoriaDAO.listarTodos();
        List<Proveedor> proveedores = proveedorDAO.listarTodos();

        for (Producto p : productos) {
            if (!busqueda.isEmpty() && !p.getNombreProducto().toLowerCase().contains(busqueda)) continue;

            String catNombre = categorias.stream()
                .filter(c -> c.getIdCategoria() == p.getIdCategoria())
                .map(Categoria::getNombreCategoria).findFirst().orElse("-");
            String provNombre = proveedores.stream()
                .filter(pr -> pr.getIdProveedor() == p.getIdProveedor())
                .map(Proveedor::getEmpresa).findFirst().orElse("-");

            modeloTabla.addRow(new Object[]{
                p.getIdProducto(), p.getNombreProducto(),
                String.format("$%,.2f", p.getPrecio()),
                p.getStockActual(), p.getStockMinimo(),
                catNombre, provNombre
            });
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        Producto p = productoDAO.buscarPorId(idSeleccionado);
        if (p == null) return;

        txtNombre.setText(p.getNombreProducto());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStockActual.setText(String.valueOf(p.getStockActual()));
        txtStockMinimo.setText(String.valueOf(p.getStockMinimo()));
        txtImgUrl.setText(p.getImgUrl() != null ? p.getImgUrl() : "");

        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getIdCategoria() == p.getIdCategoria()) {
                cmbCategoria.setSelectedIndex(i); break;
            }
        }
        for (int i = 0; i < cmbProveedor.getItemCount(); i++) {
            if (cmbProveedor.getItemAt(i).getIdProveedor() == p.getIdProveedor()) {
                cmbProveedor.setSelectedIndex(i); break;
            }
        }
    }

    private void guardar() {
        try {
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stockActual = Integer.parseInt(txtStockActual.getText().trim());
            int stockMinimo = Integer.parseInt(txtStockMinimo.getText().trim());
            Categoria cat = (Categoria) cmbCategoria.getSelectedItem();
            Proveedor prov = (Proveedor) cmbProveedor.getSelectedItem();

            if (nombre.isEmpty() || cat == null || prov == null) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos obligatorios.",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Producto p = new Producto(idSeleccionado > 0 ? idSeleccionado : 0, nombre, precio,
                stockActual, stockMinimo, cat.getIdCategoria(), prov.getIdProveedor(),
                txtImgUrl.getText().trim().isEmpty() ? null : txtImgUrl.getText().trim());

            boolean exito;
            if (idSeleccionado > 0) {
                exito = productoDAO.actualizar(p);
            } else {
                exito = productoDAO.insertar(p);
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, "Producto guardado exitosamente.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el producto.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio, stock actual y stock minimo deben ser numeros validos.",
                "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
            "Estas seguro de eliminar este producto?", "Confirmar Eliminacion",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            if (productoDAO.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado.",
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar. El producto puede estar asociado a ventas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStockActual.setText("");
        txtStockMinimo.setText("");
        txtImgUrl.setText("");
        if (cmbCategoria.getItemCount() > 0) cmbCategoria.setSelectedIndex(0);
        if (cmbProveedor.getItemCount() > 0) cmbProveedor.setSelectedIndex(0);
        tabla.clearSelection();
    }

    // === Metodos auxiliares de estilo ===

    private JPanel crearCampoFormulario(String label, JComponent campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(280, 58));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(3));

        if (campo instanceof JTextField) estilizarCampo((JTextField) campo);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(280, 30));
        panel.add(campo);
        panel.add(Box.createVerticalStrut(6));
        return panel;
    }

    static void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setForeground(MainFrame.COLOR_TEXTO);
        campo.setBackground(MainFrame.COLOR_CAMPO);
        campo.setCaretColor(MainFrame.COLOR_PRIMARIO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true),
            new EmptyBorder(5, 8, 5, 8)
        ));
        campo.setDisabledTextColor(MainFrame.COLOR_TEXTO_SEC);
        campo.setSelectionColor(MainFrame.COLOR_PRIMARIO);
        campo.setSelectedTextColor(Color.WHITE);
    }

    static void añadirPlaceholder(JTextField campo, String placeholder) {
        campo.setText(placeholder);
        campo.setForeground(MainFrame.COLOR_TEXTO_SEC);
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(MainFrame.COLOR_TEXTO);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(MainFrame.COLOR_TEXTO_SEC);
                }
            }
        });
    }

    static void estilizarArea(JTextArea area) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setForeground(MainFrame.COLOR_TEXTO);
        area.setBackground(MainFrame.COLOR_CAMPO);
        area.setCaretColor(MainFrame.COLOR_PRIMARIO);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setSelectionColor(MainFrame.COLOR_PRIMARIO);
        area.setSelectedTextColor(Color.WHITE);
    }

    @SuppressWarnings("unchecked")
    static void estilizarCombo(JComboBox<?> combo) {
        // Forzar UI basica personalizada para modernizar la flecha
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(MainFrame.COLOR_CAMPO);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        
                        // Dibujar flecha moderna (V pequeña)
                        g2.setColor(MainFrame.COLOR_TEXTO_SEC);
                        g2.setStroke(new BasicStroke(2));
                        int w = getWidth();
                        int h = getHeight();
                        int size = 8;
                        int x = (w - size) / 2;
                        int y = (h - size / 2) / 2;
                        g2.drawPolyline(new int[]{x, x + size/2, x + size}, new int[]{y, y + size/2, y}, 3);
                        g2.dispose();
                    }
                };
                btn.setBackground(MainFrame.COLOR_CAMPO);
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                return btn;
            }
        });
        
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setForeground(MainFrame.COLOR_TEXTO);
        combo.setBackground(MainFrame.COLOR_CAMPO);
        combo.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        
        // Renderer personalizado para forzar colores en el menu desplegable y el area principal
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setOpaque(true);
                if (isSelected) {
                    l.setBackground(MainFrame.COLOR_PRIMARIO);
                    l.setForeground(Color.WHITE);
                } else {
                    l.setBackground(MainFrame.COLOR_CAMPO);
                    l.setForeground(MainFrame.COLOR_TEXTO);
                }
                l.setBorder(new EmptyBorder(4, 8, 4, 8));
                return l;
            }
        });

        // Aplicar a todos los subcomponentes internos
        for (int i = 0; i < combo.getComponentCount(); i++) {
            Component c = combo.getComponent(i);
            c.setBackground(MainFrame.COLOR_CAMPO);
            c.setForeground(MainFrame.COLOR_TEXTO);
        }
    }

    static JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setForeground(MainFrame.COLOR_TEXTO);
        tabla.setBackground(MainFrame.COLOR_PANEL);
        tabla.setSelectionBackground(new Color(255, 107, 53, 80));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(MainFrame.COLOR_BORDE);
        tabla.setRowHeight(30);
        tabla.setShowGrid(true);
        tabla.setIntercellSpacing(new Dimension(1, 1));

        // Estilizar encabezado con renderer personalizado para forzar contraste
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(new Color(35, 35, 50));
                l.setForeground(MainFrame.COLOR_TEXTO);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MainFrame.COLOR_BORDE));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        });
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 35));
    }
}
