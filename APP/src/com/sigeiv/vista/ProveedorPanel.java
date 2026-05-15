package com.sigeiv.vista;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.dao.ProveedorDAO;
import com.sigeiv.modelo.Proveedor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Panel de gestion de Proveedores con CRUD completo.
 */
public class ProveedorPanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar, txtEmpresa, txtContacto, txtTelefono;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnLimpiar;
    private int idSeleccionado = -1;

    public ProveedorPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializar();
        cargarTabla();
    }

    private void inicializar() {
        JPanel panelSuperior = new JPanel(new BorderLayout(12, 0));
        panelSuperior.setOpaque(false);
        JLabel lblTitulo = new JLabel("Gestion de Proveedores");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        txtBuscar = new JTextField(20);
        ProductoPanel.estilizarCampo(txtBuscar);
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { filtrar(); }
        });
        JPanel pb = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pb.setOpaque(false);
        pb.add(txtBuscar);
        panelSuperior.add(pb, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        String[] cols = {"ID", "Empresa", "Contacto", "Telefono"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(MainFrame.COLOR_PANEL);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true), new EmptyBorder(16, 16, 16, 16)));
        panelForm.setPreferredSize(new Dimension(260, 0));

        JLabel lblForm = new JLabel("Datos del Proveedor");
        lblForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblForm.setForeground(MainFrame.COLOR_PRIMARIO);
        lblForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelForm.add(lblForm); panelForm.add(Box.createVerticalStrut(16));

        txtEmpresa = new JTextField(); panelForm.add(crearCampo("Empresa:", txtEmpresa));
        txtContacto = new JTextField(); panelForm.add(crearCampo("Contacto:", txtContacto));
        txtTelefono = new JTextField(); panelForm.add(crearCampo("Telefono:", txtTelefono));
        panelForm.add(Box.createVerticalStrut(16));

        JPanel panelBtns = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBtns.setOpaque(false); panelBtns.setMaximumSize(new Dimension(260, 80));
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
        if (usuarioCtrl.esVendedor()) {
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
        for (Proveedor p : proveedorDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{ p.getIdProveedor(), p.getEmpresa(), p.getContacto(), p.getTelefono() });
        }
    }

    private void filtrar() {
        String b = txtBuscar.getText().trim();
        modeloTabla.setRowCount(0);
        List<Proveedor> lista = b.isEmpty() ? proveedorDAO.listarTodos() : proveedorDAO.buscarPorNombre(b);
        for (Proveedor p : lista) {
            modeloTabla.addRow(new Object[]{ p.getIdProveedor(), p.getEmpresa(), p.getContacto(), p.getTelefono() });
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        Proveedor p = proveedorDAO.buscarPorId(idSeleccionado);
        if (p != null) {
            txtEmpresa.setText(p.getEmpresa()); txtContacto.setText(p.getContacto());
            txtTelefono.setText(p.getTelefono());
        }
    }

    private void guardar() {
        String empresa = txtEmpresa.getText().trim();
        if (empresa.isEmpty()) { JOptionPane.showMessageDialog(this, "La empresa es obligatoria."); return; }
        Proveedor p = new Proveedor(idSeleccionado > 0 ? idSeleccionado : 0, empresa,
            txtContacto.getText().trim(), txtTelefono.getText().trim());
        boolean ok = idSeleccionado > 0 ? proveedorDAO.actualizar(p) : proveedorDAO.insertar(p);
        if (ok) { JOptionPane.showMessageDialog(this, "Proveedor guardado."); limpiar(); cargarTabla(); }
        else JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void eliminar() {
        if (idSeleccionado <= 0) { JOptionPane.showMessageDialog(this, "Selecciona un proveedor."); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (proveedorDAO.eliminar(idSeleccionado)) { limpiar(); cargarTabla(); }
            else JOptionPane.showMessageDialog(this, "No se pudo eliminar. Puede tener productos asociados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        idSeleccionado = -1; txtEmpresa.setText(""); txtContacto.setText(""); txtTelefono.setText("");
        tabla.clearSelection();
    }

    private JPanel crearCampo(String label, JTextField campo) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false); p.setAlignmentX(LEFT_ALIGNMENT); p.setMaximumSize(new Dimension(260, 58));
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(MainFrame.COLOR_TEXTO_SEC); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(3));
        ProductoPanel.estilizarCampo(campo); campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(260, 30)); p.add(campo); p.add(Box.createVerticalStrut(6));
        return p;
    }
}






