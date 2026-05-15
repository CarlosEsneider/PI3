package com.sigeiv.vista;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.dao.CategoriaDAO;
import com.sigeiv.modelo.Categoria;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestion de Categorias con CRUD completo.
 */
public class CategoriaPanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnLimpiar;
    private int idSeleccionado = -1;

    public CategoriaPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializar();
        cargarTabla();
    }

    private void inicializar() {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);
        JLabel lblTitulo = new JLabel("Gestion de Categorias");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        add(panelSuperior, BorderLayout.NORTH);

        String[] cols = {"ID", "Nombre", "Descripcion"};
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
        panelForm.setPreferredSize(new Dimension(280, 0));

        JLabel lblForm = new JLabel("Datos de Categoria");
        lblForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblForm.setForeground(MainFrame.COLOR_PRIMARIO);
        lblForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelForm.add(lblForm); panelForm.add(Box.createVerticalStrut(16));

        txtNombre = new JTextField();
        ProductoPanel.estilizarCampo(txtNombre);
        panelForm.add(crearCampo("Nombre:", txtNombre));

        // Descripcion como TextArea
        JLabel lblDesc = new JLabel("Descripcion:");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelForm.add(lblDesc); panelForm.add(Box.createVerticalStrut(3));

        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcion.setForeground(MainFrame.COLOR_TEXTO);
        txtDescripcion.setBackground(MainFrame.COLOR_CAMPO);
        txtDescripcion.setCaretColor(MainFrame.COLOR_PRIMARIO);
        txtDescripcion.setBorder(new EmptyBorder(5, 8, 5, 8));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollDesc.setMaximumSize(new Dimension(280, 100));
        panelForm.add(scrollDesc);
        panelForm.add(Box.createVerticalStrut(16));

        JPanel panelBtns = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBtns.setOpaque(false); panelBtns.setMaximumSize(new Dimension(280, 80));
        panelBtns.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnNuevo = ProductoPanel.crearBoton("Nuevo", MainFrame.COLOR_PRIMARIO);
        btnGuardar = ProductoPanel.crearBoton("Guardar", MainFrame.COLOR_EXITO);
        btnEliminar = ProductoPanel.crearBoton("Eliminar", MainFrame.COLOR_ERROR);
        btnLimpiar = ProductoPanel.crearBoton("Limpiar", MainFrame.COLOR_TEXTO_SEC);
        panelBtns.add(btnNuevo); panelBtns.add(btnGuardar);
        panelBtns.add(btnEliminar); panelBtns.add(btnLimpiar);
        panelForm.add(panelBtns);

        if (usuarioCtrl.esConsultor() || usuarioCtrl.esVendedor()) {
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
        for (Categoria c : categoriaDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{ c.getIdCategoria(), c.getNombreCategoria(), c.getDescripcion() });
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        Categoria c = categoriaDAO.buscarPorId(idSeleccionado);
        if (c != null) {
            txtNombre.setText(c.getNombreCategoria());
            txtDescripcion.setText(c.getDescripcion() != null ? c.getDescripcion() : "");
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return; }
        Categoria c = new Categoria(idSeleccionado > 0 ? idSeleccionado : 0, nombre, txtDescripcion.getText().trim());
        boolean ok = idSeleccionado > 0 ? categoriaDAO.actualizar(c) : categoriaDAO.insertar(c);
        if (ok) { JOptionPane.showMessageDialog(this, "Categoria guardada."); limpiar(); cargarTabla(); }
        else JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void eliminar() {
        if (idSeleccionado <= 0) { JOptionPane.showMessageDialog(this, "Selecciona una categoria."); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar esta categoria?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (categoriaDAO.eliminar(idSeleccionado)) { limpiar(); cargarTabla(); }
            else JOptionPane.showMessageDialog(this, "No se pudo eliminar. Tiene productos asociados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        idSeleccionado = -1; txtNombre.setText(""); txtDescripcion.setText(""); tabla.clearSelection();
    }

    private JPanel crearCampo(String label, JTextField campo) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false); p.setAlignmentX(LEFT_ALIGNMENT); p.setMaximumSize(new Dimension(280, 58));
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(MainFrame.COLOR_TEXTO_SEC); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(3));
        campo.setAlignmentX(LEFT_ALIGNMENT); campo.setMaximumSize(new Dimension(280, 30));
        p.add(campo); p.add(Box.createVerticalStrut(6));
        return p;
    }
}






