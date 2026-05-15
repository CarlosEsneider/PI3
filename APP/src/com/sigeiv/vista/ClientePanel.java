package com.sigeiv.vista;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.dao.ClienteDAO;
import com.sigeiv.modelo.Cliente;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Panel de gestion de Clientes con CRUD completo.
 */
public class ClientePanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar, txtNombre, txtDni, txtTelefono;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnLimpiar;
    private int idSeleccionado = -1;

    public ClientePanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializar();
        cargarTabla();
    }

    private void inicializar() {
        // Titulo + Busqueda
        JPanel panelSuperior = new JPanel(new BorderLayout(12, 0));
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestion de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        txtBuscar = new JTextField(20);
        ProductoPanel.estilizarCampo(txtBuscar);
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { filtrar(); }
        });
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBuscar.setOpaque(false);
        panelBuscar.add(new JLabel("Buscar:") {{ setForeground(MainFrame.COLOR_TEXTO_SEC); }});
        panelBuscar.add(txtBuscar);
        panelSuperior.add(panelBuscar, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "DNI", "Telefono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        ProductoPanel.estilizarTabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) cargarSeleccion();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scroll.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        add(scroll, BorderLayout.CENTER);

        // Formulario
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(MainFrame.COLOR_PANEL);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));
        panelForm.setPreferredSize(new Dimension(260, 0));

        JLabel lblForm = new JLabel("Datos del Cliente");
        lblForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblForm.setForeground(MainFrame.COLOR_PRIMARIO);
        lblForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelForm.add(lblForm);
        panelForm.add(Box.createVerticalStrut(16));

        txtNombre = new JTextField();
        panelForm.add(crearCampo("Nombre:", txtNombre));
        txtDni = new JTextField();
        panelForm.add(crearCampo("DNI:", txtDni));
        txtTelefono = new JTextField();
        panelForm.add(crearCampo("Telefono:", txtTelefono));

        panelForm.add(Box.createVerticalStrut(16));

        JPanel panelBtns = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBtns.setOpaque(false);
        panelBtns.setMaximumSize(new Dimension(260, 80));
        panelBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnNuevo = ProductoPanel.crearBoton("Nuevo", MainFrame.COLOR_PRIMARIO);
        btnGuardar = ProductoPanel.crearBoton("Guardar", MainFrame.COLOR_EXITO);
        btnEliminar = ProductoPanel.crearBoton("Eliminar", MainFrame.COLOR_ERROR);
        btnLimpiar = ProductoPanel.crearBoton("Limpiar", MainFrame.COLOR_TEXTO_SEC);

        panelBtns.add(btnNuevo); panelBtns.add(btnGuardar);
        panelBtns.add(btnEliminar); panelBtns.add(btnLimpiar);
        panelForm.add(panelBtns);

        if (usuarioCtrl.esConsultor()) {
            btnNuevo.setEnabled(false); btnGuardar.setEnabled(false); btnEliminar.setEnabled(false);
        }

        btnNuevo.addActionListener(e -> limpiar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());

        add(panelForm, BorderLayout.EAST);
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Cliente c : clienteDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{ c.getIdCliente(), c.getNombreCliente(), c.getDni(), c.getTelefono() });
        }
    }

    private void filtrar() {
        String busqueda = txtBuscar.getText().trim();
        modeloTabla.setRowCount(0);
        List<Cliente> lista = busqueda.isEmpty() ? clienteDAO.listarTodos() : clienteDAO.buscarPorNombre(busqueda);
        for (Cliente c : lista) {
            modeloTabla.addRow(new Object[]{ c.getIdCliente(), c.getNombreCliente(), c.getDni(), c.getTelefono() });
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        Cliente c = clienteDAO.buscarPorId(idSeleccionado);
        if (c != null) {
            txtNombre.setText(c.getNombreCliente());
            txtDni.setText(c.getDni());
            txtTelefono.setText(c.getTelefono());
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente c = new Cliente(idSeleccionado > 0 ? idSeleccionado : 0, nombre,
            txtDni.getText().trim(), txtTelefono.getText().trim());
        boolean ok = idSeleccionado > 0 ? clienteDAO.actualizar(c) : clienteDAO.insertar(c);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Cliente guardado.", "Exito", JOptionPane.INFORMATION_MESSAGE);
            limpiar(); cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar. Posible DNI duplicado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado <= 0) { JOptionPane.showMessageDialog(this, "Selecciona un cliente."); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(idSeleccionado)) { limpiar(); cargarTabla(); }
            else JOptionPane.showMessageDialog(this, "No se pudo eliminar. Puede tener ventas asociadas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        txtNombre.setText(""); txtDni.setText(""); txtTelefono.setText("");
        tabla.clearSelection();
    }

    private JPanel crearCampo(String label, JTextField campo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(260, 58));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(MainFrame.COLOR_TEXTO_SEC);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(3));
        ProductoPanel.estilizarCampo(campo);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(260, 30));
        p.add(campo); p.add(Box.createVerticalStrut(6));
        return p;
    }
}






