package com.sigeiv.vista;

import java.util.List;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.dao.*;
import com.sigeiv.modelo.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

/**
 * Panel de registro de Compras (abastecimiento de inventario).
 * Permite seleccionar productos, agregar al carrito de compra y procesar la compra.
 * Actualiza el inventario automaticamente incrementando el stock.
 * Acceso restringido: solo Administrador.
 */
public class CompraPanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final CompraDAO compraDAO = new CompraDAO();
    private final DetalleCompraDAO detalleCompraDAO = new DetalleCompraDAO();

    private JComboBox<ProveedorItem> cmbProveedor;
    private JComboBox<ProductoItem> cmbProducto;
    private JTextField txtCantidad, txtPrecioUnitario;
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JLabel lblTotal;
    private JButton btnAgregar, btnQuitar, btnProcesar;

    // Lista temporal del carrito de compra
    private final java.util.List<Object[]> carrito = new ArrayList<>();

    public CompraPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializar();
    }

    private void inicializar() {
        // Titulo
        JLabel lblTitulo = new JLabel("Registro de Compras");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);
        panelTop.add(lblTitulo, BorderLayout.WEST);
        add(panelTop, BorderLayout.NORTH);

        // Panel izquierdo: Formulario de compra
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(MainFrame.COLOR_PANEL);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true), new EmptyBorder(16, 16, 16, 16)));
        panelIzq.setPreferredSize(new Dimension(300, 0));

        JLabel lblNuevaCompra = new JLabel("Nueva Compra");
        lblNuevaCompra.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNuevaCompra.setForeground(MainFrame.COLOR_PRIMARIO);
        lblNuevaCompra.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblNuevaCompra);
        panelIzq.add(Box.createVerticalStrut(16));

        // Proveedor
        JLabel lblProv = new JLabel("Proveedor:");
        lblProv.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblProv.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblProv.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblProv);
        panelIzq.add(Box.createVerticalStrut(3));

        cmbProveedor = new JComboBox<>();
        for (Proveedor p : proveedorDAO.listarTodos()) {
            cmbProveedor.addItem(new ProveedorItem(p));
        }
        ProductoPanel.estilizarCombo(cmbProveedor);
        cmbProveedor.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbProveedor.setMaximumSize(new Dimension(300, 30));
        // Al cambiar de proveedor, se recargan los productos
        cmbProveedor.addActionListener(e -> cargarProductos());
        panelIzq.add(cmbProveedor);
        panelIzq.add(Box.createVerticalStrut(12));

        // Producto
        JLabel lblProd = new JLabel("Producto:");
        lblProd.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblProd.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblProd.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblProd);
        panelIzq.add(Box.createVerticalStrut(3));

        cmbProducto = new JComboBox<>();
        cargarProductos();
        ProductoPanel.estilizarCombo(cmbProducto);
        cmbProducto.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbProducto.setMaximumSize(new Dimension(300, 30));
        panelIzq.add(cmbProducto);
        panelIzq.add(Box.createVerticalStrut(12));

        // Cantidad
        JLabel lblCant = new JLabel("Cantidad:");
        lblCant.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCant.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblCant.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblCant);
        panelIzq.add(Box.createVerticalStrut(3));

        txtCantidad = new JTextField("1");
        ProductoPanel.estilizarCampo(txtCantidad);
        txtCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtCantidad.setMaximumSize(new Dimension(300, 30));
        panelIzq.add(txtCantidad);
        panelIzq.add(Box.createVerticalStrut(12));

        // Precio Unitario
        JLabel lblPrecio = new JLabel("Precio Unitario:");
        lblPrecio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPrecio.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblPrecio);
        panelIzq.add(Box.createVerticalStrut(3));

        txtPrecioUnitario = new JTextField();
        ProductoPanel.estilizarCampo(txtPrecioUnitario);
        txtPrecioUnitario.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPrecioUnitario.setMaximumSize(new Dimension(300, 30));
        // Pre-cargar precio del producto seleccionado
        cmbProducto.addActionListener(e -> {
            ProductoItem item = (ProductoItem) cmbProducto.getSelectedItem();
            if (item != null) {
                txtPrecioUnitario.setText(String.valueOf(item.producto.getPrecio()));
            }
        });
        panelIzq.add(txtPrecioUnitario);
        panelIzq.add(Box.createVerticalStrut(16));

        // Botones
        btnAgregar = ProductoPanel.crearBoton("Agregar al Carrito", MainFrame.COLOR_PRIMARIO);
        btnAgregar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAgregar.setMaximumSize(new Dimension(300, 36));
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        panelIzq.add(btnAgregar);
        panelIzq.add(Box.createVerticalStrut(8));

        btnQuitar = ProductoPanel.crearBoton("Quitar Seleccionado", MainFrame.COLOR_ERROR);
        btnQuitar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnQuitar.setMaximumSize(new Dimension(300, 36));
        btnQuitar.addActionListener(e -> quitarDelCarrito());
        panelIzq.add(btnQuitar);

        panelIzq.add(Box.createVerticalGlue());

        // Total
        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(MainFrame.COLOR_EXITO);
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblTotal);
        panelIzq.add(Box.createVerticalStrut(12));

        btnProcesar = ProductoPanel.crearBoton("Procesar Compra", MainFrame.COLOR_EXITO);
        btnProcesar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProcesar.setMaximumSize(new Dimension(300, 40));
        btnProcesar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProcesar.addActionListener(e -> procesarCompra());
        panelIzq.add(btnProcesar);

        // Solo admin puede registrar compras
        if (!usuarioCtrl.esAdmin()) {
            btnAgregar.setEnabled(false); btnQuitar.setEnabled(false); btnProcesar.setEnabled(false);
        }

        add(panelIzq, BorderLayout.WEST);

        // Tabla carrito
        String[] cols = {"Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloCarrito = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        ProductoPanel.estilizarTabla(tablaCarrito);

        JScrollPane scroll = new JScrollPane(tablaCarrito);
        scroll.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scroll.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        add(scroll, BorderLayout.CENTER);

        // Historial al lado derecho
        JPanel panelHist = new JPanel(new BorderLayout());
        panelHist.setBackground(MainFrame.COLOR_PANEL);
        panelHist.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true), new EmptyBorder(12, 12, 12, 12)));
        panelHist.setPreferredSize(new Dimension(260, 0));

        JLabel lblHist = new JLabel("Ultimas Compras");
        lblHist.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHist.setForeground(MainFrame.COLOR_PRIMARIO);
        panelHist.add(lblHist, BorderLayout.NORTH);

        DefaultTableModel modeloHist = new DefaultTableModel(new String[]{"ID", "Fecha", "Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaHist = new JTable(modeloHist);
        ProductoPanel.estilizarTabla(tablaHist);
        for (Compra c : compraDAO.listarTodos()) {
            if (modeloHist.getRowCount() >= 15) break;
            modeloHist.addRow(new Object[]{
                c.getIdCompra(),
                new java.text.SimpleDateFormat("dd/MM/yy HH:mm").format(c.getFecha()),
                String.format("$%,.2f", c.getTotal())
            });
        }
        JScrollPane scrollHist = new JScrollPane(tablaHist);
        scrollHist.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scrollHist.setBorder(BorderFactory.createEmptyBorder());
        panelHist.add(scrollHist, BorderLayout.CENTER);
        add(panelHist, BorderLayout.EAST);
    }

    private void cargarProductos() {
        cmbProducto.removeAllItems();
        ProveedorItem prov = (ProveedorItem) cmbProveedor.getSelectedItem();
        if (prov == null) return;
        
        java.util.List<Producto> productos = productoDAO.buscarPorProveedor(prov.proveedor.getIdProveedor());
        for (Producto p : productos) {
            cmbProducto.addItem(new ProductoItem(p));
        }
    }

    private void agregarAlCarrito() {
        ProductoItem item = (ProductoItem) cmbProducto.getSelectedItem();
        if (item == null) { JOptionPane.showMessageDialog(this, "Selecciona un producto."); return; }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double precioUnit = Double.parseDouble(txtPrecioUnitario.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
            if (precioUnit < 0) {
                JOptionPane.showMessageDialog(this, "El precio unitario debe ser positivo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double subtotal = precioUnit * cantidad;
            carrito.add(new Object[]{ item.producto, cantidad, precioUnit, subtotal });
            modeloCarrito.addRow(new Object[]{
                item.producto.getNombreProducto(),
                String.format("$%,.2f", precioUnit),
                cantidad,
                String.format("$%,.2f", subtotal)
            });
            actualizarTotal();
            txtCantidad.setText("1");
            
            // Bloquear cambio de proveedor si hay items en el carrito
            cmbProveedor.setEnabled(false);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad y precio deben ser numeros validos positivos.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            carrito.remove(fila);
            modeloCarrito.removeRow(fila);
            actualizarTotal();
            
            // Si el carrito queda vacio, permitir cambiar de proveedor
            if (carrito.isEmpty()) {
                cmbProveedor.setEnabled(true);
            }
        }
    }

    private void actualizarTotal() {
        double total = 0;
        for (Object[] item : carrito) total += (double) item[3];
        lblTotal.setText("Total: $" + String.format("%,.2f", total));
    }

    private void procesarCompra() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega productos al carrito primero.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ProveedorItem provItem = (ProveedorItem) cmbProveedor.getSelectedItem();
        if (provItem == null) { JOptionPane.showMessageDialog(this, "Selecciona un proveedor."); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirmar la compra?",
            "Procesar Compra", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        double total = 0;
        for (Object[] item : carrito) total += (double) item[3];

        Compra compra = new Compra(0, new Date(), total,
            provItem.proveedor.getIdProveedor(),
            usuarioCtrl.getUsuarioLogueado().getIdUsuario());

        if (compraDAO.insertar(compra)) {
            boolean todoOk = true;
            for (Object[] item : carrito) {
                Producto prod = (Producto) item[0];
                int cantidad = (int) item[1];
                double precioUnit = (double) item[2];

                DetalleCompra detalle = new DetalleCompra(0, compra.getIdCompra(),
                    prod.getIdProducto(), cantidad, precioUnit);
                detalleCompraDAO.insertar(detalle);

                // Actualizar inventario: INCREMENTAR stock
                if (!productoDAO.actualizarStockCompra(prod.getIdProducto(), cantidad)) {
                    todoOk = false;
                }
            }

            if (todoOk) {
                JOptionPane.showMessageDialog(this,
                    "Compra #" + compra.getIdCompra() + " procesada exitosamente.\nTotal: $"
                    + String.format("%,.2f", total) + "\nStock actualizado.",
                    "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Compra registrada pero hubo problemas al actualizar el stock.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

            // Limpiar carrito
            carrito.clear();
            modeloCarrito.setRowCount(0);
            actualizarTotal();
            cmbProveedor.setEnabled(true); // Permitir elegir nuevo proveedor para otra compra
            cargarProductos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la compra.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Wrappers para ComboBox
    private static class ProveedorItem {
        Proveedor proveedor;
        ProveedorItem(Proveedor p) { this.proveedor = p; }
        @Override public String toString() {
            return proveedor.getEmpresa() + " (" + proveedor.getContacto() + ")";
        }
    }

    private static class ProductoItem {
        Producto producto;
        ProductoItem(Producto p) { this.producto = p; }
        @Override public String toString() {
            return producto.getNombreProducto() + " - $" + String.format("%,.2f", producto.getPrecio())
                + " [Stock: " + producto.getStockActual() + "]";
        }
    }
}






