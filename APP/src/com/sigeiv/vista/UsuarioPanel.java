package com.sigeiv.vista;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.dao.UsuarioDAO;
import com.sigeiv.modelo.Usuario;
import com.sigeiv.util.HashUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioPanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    private JTextField txtNombre, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JCheckBox chkActivo;
    private JButton btnGuardar, btnEliminar, btnLimpiar;

    private int idSeleccionado = -1;

    public UsuarioPanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        if (!ctrl.esAdmin()) {
            add(new JLabel("Acceso Denegado. Solo administradores pueden gestionar usuarios.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // --- Formulario Izquierdo ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(MainFrame.COLOR_PANEL);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true), new EmptyBorder(16, 16, 16, 16)));
        panelForm.setPreferredSize(new Dimension(320, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        JLabel lblTitulo = new JLabel("Gestion de Usuarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(MainFrame.COLOR_PRIMARIO);
        panelForm.add(lblTitulo, gbc);

        gbc.gridy++;
        panelForm.add(crearEtiqueta("Nombre Completo"), gbc);
        gbc.gridy++;
        txtNombre = new JTextField();
        ProductoPanel.estilizarCampo(txtNombre);
        panelForm.add(txtNombre, gbc);

        gbc.gridy++;
        panelForm.add(crearEtiqueta("Nombre de Usuario (Login)"), gbc);
        gbc.gridy++;
        txtUsername = new JTextField();
        ProductoPanel.estilizarCampo(txtUsername);
        panelForm.add(txtUsername, gbc);

        gbc.gridy++;
        panelForm.add(crearEtiqueta("Contrasena (dejar vacio para no cambiar)"), gbc);
        gbc.gridy++;
        txtPassword = new JPasswordField();
        ProductoPanel.estilizarCampo(txtPassword);
        panelForm.add(txtPassword, gbc);

        gbc.gridy++;
        panelForm.add(crearEtiqueta("Rol del Usuario"), gbc);
        gbc.gridy++;
        cmbRol = new JComboBox<>(new String[]{"1 - Administrador", "2 - Vendedor", "3 - Consultor"});
        ProductoPanel.estilizarCombo(cmbRol);
        panelForm.add(cmbRol, gbc);

        gbc.gridy++;
        chkActivo = new JCheckBox("Usuario Activo");
        chkActivo.setBackground(MainFrame.COLOR_PANEL);
        chkActivo.setForeground(MainFrame.COLOR_TEXTO);
        chkActivo.setSelected(true);
        panelForm.add(chkActivo, gbc);

        // Botones
        gbc.gridy++;
        gbc.insets = new Insets(16, 0, 8, 0);
        btnGuardar = ProductoPanel.crearBoton("Guardar Usuario", MainFrame.COLOR_EXITO);
        btnGuardar.addActionListener(e -> guardarUsuario());
        panelForm.add(btnGuardar, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 8, 0);
        btnLimpiar = ProductoPanel.crearBoton("Limpiar Campos", MainFrame.COLOR_BORDE);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        panelForm.add(btnLimpiar, gbc);

        gbc.gridy++;
        btnEliminar = ProductoPanel.crearBoton("Eliminar Usuario", MainFrame.COLOR_ERROR);
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnEliminar.setEnabled(false);
        panelForm.add(btnEliminar, gbc);

        // Spacer
        gbc.gridy++; gbc.weighty = 1.0;
        panelForm.add(Box.createVerticalGlue(), gbc);

        add(panelForm, BorderLayout.WEST);

        // --- Tabla Derecha ---
        JPanel panelTabla = new JPanel(new BorderLayout(0, 12));
        panelTabla.setOpaque(false);

        String[] columnas = {"ID", "Nombre", "Username", "Rol", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaUsuarios = new JTable(modeloTabla);
        ProductoPanel.estilizarTabla(tablaUsuarios);

        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaUsuarios.getSelectedRow() != -1) {
                seleccionarUsuario();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scroll.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        panelTabla.add(scroll, BorderLayout.CENTER);

        add(panelTabla, BorderLayout.CENTER);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        for (Usuario u : usuarios) {
            String nombreRol = u.getIdRol() == 1 ? "Admin" : (u.getIdRol() == 2 ? "Vendedor" : "Consultor");
            String estado = u.isActivo() ? "Activo" : "Inactivo";
            modeloTabla.addRow(new Object[]{u.getIdUsuario(), u.getNombreUsuario(), u.getUsername(), nombreRol, estado});
        }
    }

    private void seleccionarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        
        Usuario u = usuarioDAO.listarTodos().stream().filter(x -> x.getIdUsuario() == idSeleccionado).findFirst().orElse(null);
        if (u != null) {
            txtNombre.setText(u.getNombreUsuario());
            txtUsername.setText(u.getUsername());
            txtPassword.setText(""); // No se muestra el hash
            cmbRol.setSelectedIndex(u.getIdRol() - 1);
            chkActivo.setSelected(u.isActivo());
            btnEliminar.setEnabled(true);
        }
    }

    private void guardarUsuario() {
        if (txtNombre.getText().trim().isEmpty() || txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Username son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idRol = cmbRol.getSelectedIndex() + 1;
        boolean activo = chkActivo.isSelected();

        if (idSeleccionado == -1) {
            // Nuevo usuario
            if (txtPassword.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Debe ingresar una contrasena para el nuevo usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String hash = HashUtil.sha256(new String(txtPassword.getPassword()));
            Usuario u = new Usuario(0, txtNombre.getText().trim(), txtUsername.getText().trim(), hash, idRol, activo);
            if (usuarioDAO.insertar(u)) {
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear usuario (quizas el username ya existe).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Editar usuario
            Usuario u = usuarioDAO.listarTodos().stream().filter(x -> x.getIdUsuario() == idSeleccionado).findFirst().get();
            u.setNombreUsuario(txtNombre.getText().trim());
            u.setUsername(txtUsername.getText().trim());
            u.setIdRol(idRol);
            u.setActivo(activo);
            
            if (txtPassword.getPassword().length > 0) {
                u.setContrasena(HashUtil.sha256(new String(txtPassword.getPassword())));
            }

            if (usuarioDAO.actualizar(u)) {
                JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        limpiarFormulario();
        cargarDatos();
    }

    private void eliminarUsuario() {
        if (idSeleccionado == usuarioCtrl.getUsuarioLogueado().getIdUsuario()) {
            JOptionPane.showMessageDialog(this, "No puedes eliminar tu propio usuario activo.", "Accion Denegada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opc = JOptionPane.showConfirmDialog(this, "Seguro que deseas eliminar este usuario?\nSe recomienda mejor marcarlo como Inactivo.", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opc == JOptionPane.YES_OPTION) {
            if (usuarioDAO.eliminar(idSeleccionado)) {
                limpiarFormulario();
                cargarDatos();
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario. Puede tener ventas asociadas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        btnEliminar.setEnabled(false);
        tablaUsuarios.clearSelection();
    }
}






