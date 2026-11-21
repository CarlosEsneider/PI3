package com.sigeiv.vista;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.modelo.Usuario;
import com.sigeiv.util.HashUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestion de usuarios del sistema SIGEIV-Volcano.
 *
 * Fase 2 - RF-02:
 * - Catálogo oficial de 5 roles jerarquicos.
 * - Campos email y puedeAutorizar en el formulario.
 * - Borrado logico (marca activo=false en lugar de DELETE fisico).
 */
public class UsuarioPanel extends JPanel {

    // Catalogo oficial de roles (Fase 2 - RF-02)
    private static final String[] ROLES = {
        "1 - Administrador / Gerente General",
        "2 - Jefe de Produccion y Calidad",
        "3 - Encargado de Almacen y Bodega",
        "4 - Asesor Comercial / Ventas",
        "5 - Auditor / Consultor Externo"
    };

    private final UsuarioController usuarioCtrl;

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    private JTextField txtNombre, txtUsername, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JCheckBox chkActivo, chkPuedeAutorizar;
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
        panelForm.setPreferredSize(new Dimension(340, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        JLabel lblTitulo = new JLabel("Gestion de Usuarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(MainFrame.COLOR_PRIMARIO);
        panelForm.add(lblTitulo, gbc);

        // Nombre Completo
        gbc.gridy++;
        panelForm.add(crearEtiqueta("Nombre Completo"), gbc);
        gbc.gridy++;
        txtNombre = new JTextField();
        ProductoPanel.estilizarCampo(txtNombre);
        panelForm.add(txtNombre, gbc);

        // Username
        gbc.gridy++;
        panelForm.add(crearEtiqueta("Nombre de Usuario (Login)"), gbc);
        gbc.gridy++;
        txtUsername = new JTextField();
        ProductoPanel.estilizarCampo(txtUsername);
        panelForm.add(txtUsername, gbc);

        // Email (nuevo - Fase 2)
        gbc.gridy++;
        panelForm.add(crearEtiqueta("Correo Electronico"), gbc);
        gbc.gridy++;
        txtEmail = new JTextField();
        ProductoPanel.estilizarCampo(txtEmail);
        panelForm.add(txtEmail, gbc);

        // Contrasena
        gbc.gridy++;
        panelForm.add(crearEtiqueta("Contrasena (dejar vacio para no cambiar)"), gbc);
        gbc.gridy++;
        txtPassword = new JPasswordField();
        ProductoPanel.estilizarCampo(txtPassword);
        panelForm.add(txtPassword, gbc);

        // Rol — 5 roles jerarquicos (Fase 2)
        gbc.gridy++;
        panelForm.add(crearEtiqueta("Rol del Usuario"), gbc);
        gbc.gridy++;
        cmbRol = new JComboBox<>(ROLES);
        ProductoPanel.estilizarCombo(cmbRol);
        panelForm.add(cmbRol, gbc);

        // Checkboxes
        gbc.gridy++;
        chkActivo = new JCheckBox("Usuario Activo");
        chkActivo.setBackground(MainFrame.COLOR_PANEL);
        chkActivo.setForeground(MainFrame.COLOR_TEXTO);
        chkActivo.setSelected(true);
        panelForm.add(chkActivo, gbc);

        // Puede Autorizar (nuevo - Fase 2)
        gbc.gridy++;
        chkPuedeAutorizar = new JCheckBox("Puede Autorizar Documentos Soporte");
        chkPuedeAutorizar.setBackground(MainFrame.COLOR_PANEL);
        chkPuedeAutorizar.setForeground(MainFrame.COLOR_TEXTO);
        chkPuedeAutorizar.setSelected(false);
        chkPuedeAutorizar.setToolTipText("Habilita la firma digital de autorizaciones segun el rol asignado");
        panelForm.add(chkPuedeAutorizar, gbc);

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
        btnEliminar = ProductoPanel.crearBoton("Desactivar Usuario", MainFrame.COLOR_ERROR);
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnEliminar.setEnabled(false);
        btnEliminar.setToolTipText("Realiza borrado logico (activo = false). No elimina el registro.");
        panelForm.add(btnEliminar, gbc);

        // Spacer
        gbc.gridy++; gbc.weighty = 1.0;
        panelForm.add(Box.createVerticalGlue(), gbc);

        add(panelForm, BorderLayout.WEST);

        // --- Tabla Derecha ---
        JPanel panelTabla = new JPanel(new BorderLayout(0, 12));
        panelTabla.setOpaque(false);

        String[] columnas = {"ID", "Nombre", "Username", "Email", "Rol", "Autoriza", "Estado"};
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

    /** Devuelve el nombre del rol segun los 5 roles oficiales (Fase 2). */
    private String nombreRol(int idRol) {
        switch (idRol) {
            case 1: return "Admin/Gerente";
            case 2: return "Jefe Produccion";
            case 3: return "Almacen";
            case 4: return "Asesor Comercial";
            case 5: return "Auditor";
            default: return "Desconocido";
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Usuario> usuarios = usuarioCtrl.listarTodos();
        for (Usuario u : usuarios) {
            modeloTabla.addRow(new Object[]{
                u.getIdUsuario(),
                u.getNombreUsuario(),
                u.getUsername(),
                u.getEmail(),
                nombreRol(u.getIdRol()),
                u.isPuedeAutorizar() ? "Si" : "No",
                u.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }

    private void seleccionarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        Usuario u = usuarioCtrl.buscarPorId(idSeleccionado);
        if (u != null) {
            txtNombre.setText(u.getNombreUsuario());
            txtUsername.setText(u.getUsername());
            txtEmail.setText(u.getEmail() != null ? u.getEmail() : "");
            txtPassword.setText(""); // No se muestra el hash
            int idx = u.getIdRol() - 1;
            cmbRol.setSelectedIndex(idx >= 0 && idx < ROLES.length ? idx : 0);
            chkActivo.setSelected(u.isActivo());
            chkPuedeAutorizar.setSelected(u.isPuedeAutorizar());
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
        boolean puedeAutorizar = chkPuedeAutorizar.isSelected();
        String email = txtEmail.getText().trim();

        if (idSeleccionado == -1) {
            // Nuevo usuario
            if (txtPassword.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Debe ingresar una contrasena para el nuevo usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (usuarioCtrl.registrar(txtNombre.getText().trim(), txtUsername.getText().trim(), new String(txtPassword.getPassword()), email, idRol, puedeAutorizar)) {
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear usuario (el username ya puede existir).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Editar usuario existente
            Usuario u = usuarioCtrl.buscarPorId(idSeleccionado);
            if (u != null) {
                u.setNombreUsuario(txtNombre.getText().trim());
                u.setUsername(txtUsername.getText().trim());
                u.setEmail(email);
                u.setIdRol(idRol);
                u.setActivo(activo);
                u.setPuedeAutorizar(puedeAutorizar);

                if (txtPassword.getPassword().length > 0) {
                    u.setContrasena(HashUtil.sha256(new String(txtPassword.getPassword())));
                }

                if (usuarioCtrl.actualizar(u)) {
                    JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        limpiarFormulario();
        cargarDatos();
    }

    /**
     * Borrado logico: marca el usuario como inactivo (activo = false).
     * No se realiza DELETE fisico para preservar integridad referencial.
     */
    private void eliminarUsuario() {
        if (idSeleccionado == usuarioCtrl.getUsuarioLogueado().getIdUsuario()) {
            JOptionPane.showMessageDialog(this, "No puedes desactivar tu propio usuario activo.", "Accion Denegada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opc = JOptionPane.showConfirmDialog(this,
            "Se marcara el usuario como INACTIVO (borrado logico).\n" +
            "El registro se conserva para la integridad del historial.\n\n" +
            "Deseas continuar?",
            "Confirmar Desactivacion", JOptionPane.YES_NO_OPTION);
        if (opc == JOptionPane.YES_OPTION) {
            if (usuarioCtrl.eliminar(idSeleccionado)) {
                limpiarFormulario();
                cargarDatos();
                JOptionPane.showMessageDialog(this, "Usuario desactivado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al desactivar usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtUsername.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        chkPuedeAutorizar.setSelected(false);
        btnEliminar.setEnabled(false);
        tablaUsuarios.clearSelection();
    }
}


