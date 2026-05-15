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
 * Panel de registro de Ventas.
 * Permite seleccionar productos, agregar al carrito y procesar la venta.
 * Actualiza el inventario automaticamente.
 */
public class VentaPanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final DetalleVentaDAO detalleDAO = new DetalleVentaDAO();

    private JComboBox<ClienteItem> cmbCliente;
    private JComboBox<ProductoItem> cmbProducto;
    private JTextField txtCantidad;
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JLabel lblTotal;
    private JButton btnAgregar, btnQuitar, btnProcesar;

    // Lista temporal del carrito
    private final java.util.List<Object[]> carrito = new ArrayList<>();

    public VentaPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializar();
    }

    private void inicializar() {
        // Titulo
        JLabel lblTitulo = new JLabel("Registro de Ventas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);
        panelTop.add(lblTitulo, BorderLayout.WEST);
        add(panelTop, BorderLayout.NORTH);

        // Panel izquierdo: Seleccion de producto
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(MainFrame.COLOR_PANEL);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true), new EmptyBorder(16, 16, 16, 16)));
        panelIzq.setPreferredSize(new Dimension(300, 0));

        JLabel lblNuevaVenta = new JLabel("Nueva Venta");
        lblNuevaVenta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNuevaVenta.setForeground(MainFrame.COLOR_PRIMARIO);
        lblNuevaVenta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblNuevaVenta);
        panelIzq.add(Box.createVerticalStrut(16));

        // Cliente
        JLabel lblCli = new JLabel("Cliente:");
        lblCli.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCli.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblCli.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(lblCli);
        panelIzq.add(Box.createVerticalStrut(3));

        cmbCliente = new JComboBox<>();
        for (Cliente c : clienteDAO.listarTodos()) {
            cmbCliente.addItem(new ClienteItem(c));
        }
        ProductoPanel.estilizarCombo(cmbCliente);
        cmbCliente.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbCliente.setMaximumSize(new Dimension(300, 30));
        panelIzq.add(cmbCliente);
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

        btnProcesar = ProductoPanel.crearBoton("Procesar Venta", MainFrame.COLOR_EXITO);
        btnProcesar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProcesar.setMaximumSize(new Dimension(300, 40));
        btnProcesar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProcesar.addActionListener(e -> procesarVenta());
        panelIzq.add(btnProcesar);

        if (usuarioCtrl.esConsultor()) {
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

        JLabel lblHist = new JLabel("Ultimas Ventas");
        lblHist.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHist.setForeground(MainFrame.COLOR_PRIMARIO);
        panelHist.add(lblHist, BorderLayout.NORTH);

        DefaultTableModel modeloHist = new DefaultTableModel(new String[]{"ID", "Fecha", "Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaHist = new JTable(modeloHist);
        ProductoPanel.estilizarTabla(tablaHist);
        for (Venta v : ventaDAO.listarTodos()) {
            if (modeloHist.getRowCount() >= 15) break;
            modeloHist.addRow(new Object[]{
                v.getIdVenta(),
                new java.text.SimpleDateFormat("dd/MM/yy HH:mm").format(v.getFecha()),
                String.format("$%,.2f", v.getTotal())
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
        for (Producto p : productoDAO.listarTodos()) {
            if (p.getStockActual() > 0) {
                cmbProducto.addItem(new ProductoItem(p));
            }
        }
    }

    private void agregarAlCarrito() {
        ProductoItem item = (ProductoItem) cmbProducto.getSelectedItem();
        if (item == null) { JOptionPane.showMessageDialog(this, "Selecciona un producto."); return; }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
            if (cantidad > item.producto.getStockActual()) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente. Disponible: " + item.producto.getStockActual(),
                    "Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double subtotal = item.producto.getPrecio() * cantidad;
            carrito.add(new Object[]{ item.producto, cantidad, subtotal });
            modeloCarrito.addRow(new Object[]{
                item.producto.getNombreProducto(),
                String.format("$%,.2f", item.producto.getPrecio()),
                cantidad,
                String.format("$%,.2f", subtotal)
            });
            actualizarTotal();
            txtCantidad.setText("1");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad debe ser un numero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            carrito.remove(fila);
            modeloCarrito.removeRow(fila);
            actualizarTotal();
        }
    }

    private void actualizarTotal() {
        double total = 0;
        for (Object[] item : carrito) total += (double) item[2];
        lblTotal.setText("Total: $" + String.format("%,.2f", total));
    }

    private void procesarVenta() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega productos al carrito primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ClienteItem clienteItem = (ClienteItem) cmbCliente.getSelectedItem();
        if (clienteItem == null) { JOptionPane.showMessageDialog(this, "Selecciona un cliente."); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirmar la venta?", "Procesar Venta", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        double total = 0;
        for (Object[] item : carrito) total += (double) item[2];

        Venta venta = new Venta(0, new Date(), clienteItem.cliente.getIdCliente(),
            usuarioCtrl.getUsuarioLogueado().getIdUsuario(), total);

        if (ventaDAO.insertar(venta)) {
            boolean todoOk = true;
            for (Object[] item : carrito) {
                Producto prod = (Producto) item[0];
                int cantidad = (int) item[1];
                double subtotal = (double) item[2];

                DetalleVenta detalle = new DetalleVenta(0, venta.getIdVenta(),
                    prod.getIdProducto(), cantidad, prod.getPrecio(), subtotal);
                detalleDAO.insertar(detalle);

                // Actualizar inventario
                if (!productoDAO.actualizarStock(prod.getIdProducto(), cantidad)) {
                    todoOk = false;
                }
            }

            if (todoOk) {
                JOptionPane.showMessageDialog(this,
                    "Venta #" + venta.getIdVenta() + " procesada exitosamente.\nTotal: $" + String.format("%,.2f", total),
                    "Venta Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Venta registrada pero hubo problemas al actualizar el stock.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

            // Limpiar carrito
            carrito.clear();
            modeloCarrito.setRowCount(0);
            actualizarTotal();
            cargarProductos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Wrappers para ComboBox
    private static class ClienteItem {
        Cliente cliente;
        ClienteItem(Cliente c) { this.cliente = c; }
        @Override public String toString() { return cliente.getNombreCliente() + " (" + cliente.getDni() + ")"; }
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






