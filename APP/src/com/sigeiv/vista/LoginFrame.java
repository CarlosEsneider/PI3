package com.sigeiv.vista;

import java.util.List;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana de inicio de sesion del sistema SIGEIV-Volcano.
 * Diseno profesional con validacion de credenciales.
 * 
 * Entrada: Username y contrasena del usuario
 * Proceso: Valida credenciales via UsuarioController
 * Salida: Abre MainFrame si las credenciales son correctas
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;
    private final UsuarioController usuarioController = new UsuarioController();

    // Colores del tema
    private static final Color COLOR_FONDO = new Color(18, 18, 24);
    private static final Color COLOR_PANEL = new Color(28, 28, 38);
    private static final Color COLOR_PRIMARIO = new Color(255, 107, 53);   // Naranja volcanico
    private static final Color COLOR_PRIMARIO_HOVER = new Color(255, 133, 89);
    private static final Color COLOR_TEXTO = new Color(230, 230, 240);
    private static final Color COLOR_TEXTO_SEC = new Color(160, 160, 180);
    private static final Color COLOR_CAMPO = new Color(38, 38, 52);
    private static final Color COLOR_BORDE = new Color(58, 58, 78);
    private static final Color COLOR_ERROR = new Color(255, 82, 82);

    public LoginFrame() {
        configurarVentana();
        inicializarComponentes();
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("SIGEIV-Volcano - Iniciar Sesion");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
    }

    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Panel principal con bordes redondeados simulados
        JPanel panelLogin = new JPanel();
        panelLogin.setLayout(new BoxLayout(panelLogin, BoxLayout.Y_AXIS));
        panelLogin.setBackground(COLOR_PANEL);
        panelLogin.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDE, 1, true),
            new EmptyBorder(40, 40, 40, 40)
        ));
        panelLogin.setPreferredSize(new Dimension(380, 440));

        panelLogin.add(Box.createVerticalStrut(20)); // Espacio inicial superior tras quitar el icono

        // Titulo
        JLabel lblTitulo = new JLabel("SIGEIV-Volcano", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLogin.add(lblTitulo);

        // Subtitulo
        JLabel lblSubtitulo = new JLabel("Sistema de Gestion de Inventario", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(COLOR_TEXTO_SEC);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLogin.add(lblSubtitulo);
        panelLogin.add(Box.createVerticalStrut(30));

        // Label Usuario
        JLabel lblUser = new JLabel("Usuario", SwingConstants.CENTER);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(COLOR_TEXTO);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLogin.add(lblUser);
        panelLogin.add(Box.createVerticalStrut(6));

        // Campo Username
        txtUsername = new JTextField();
        estilizarCampo(txtUsername);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelLogin.add(txtUsername);
        panelLogin.add(Box.createVerticalStrut(16));

        // Label Contrasena
        JLabel lblPass = new JLabel("Contrasena", SwingConstants.CENTER);
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(COLOR_TEXTO);
        lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLogin.add(lblPass);
        panelLogin.add(Box.createVerticalStrut(6));

        // Campo Contrasena
        txtPassword = new JPasswordField();
        estilizarCampo(txtPassword);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelLogin.add(txtPassword);
        panelLogin.add(Box.createVerticalStrut(8));

        // Label de error
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblError.setForeground(COLOR_ERROR);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLogin.add(lblError);
        panelLogin.add(Box.createVerticalStrut(12));

        // Boton Login
        btnLogin = new JButton("Iniciar Sesion");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(COLOR_PRIMARIO);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(COLOR_PRIMARIO_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(COLOR_PRIMARIO);
            }
        });

        btnLogin.addActionListener(e -> iniciarSesion());
        panelLogin.add(btnLogin);
        panelLogin.add(Box.createVerticalStrut(16));



        // Enter para login
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) iniciarSesion();
            }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocus();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(panelLogin, gbc);
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setForeground(COLOR_TEXTO);
        campo.setBackground(COLOR_CAMPO);
        campo.setCaretColor(COLOR_PRIMARIO);
        campo.setHorizontalAlignment(JTextField.CENTER);
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDE, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void iniciarSesion() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Ingresa usuario y contrasena.");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Verificando...");

        // Usar SwingWorker para no bloquear la UI
        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() {
                return usuarioController.autenticar(username, password);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        dispose();
                        new MainFrame(usuarioController);
                    } else {
                        lblError.setText("Usuario o contrasena incorrectos.");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception ex) {
                    lblError.setText("Error de conexion a la base de datos.");
                    ex.printStackTrace();
                }
                btnLogin.setEnabled(true);
                btnLogin.setText("Iniciar Sesion");
            }
        };
        worker.execute();
    }

    /** Punto de entrada principal de la aplicacion */
    public static void main(String[] args) {
        MainFrame.aplicarTema();
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}






