package com.sigeiv.vista;

import java.util.List;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Ventana principal del sistema SIGEIV-Volcano.
 * Contiene navegacion lateral y paneles para cada modulo.
 * Controla el acceso segun el rol del usuario.
 * 
 * Entrada: UsuarioController con sesion activa
 * Proceso: Muestra modulos segun permisos del rol
 * Salida: Interfaz principal con todos los modulos del sistema
 */
public class MainFrame extends JFrame {

    private final UsuarioController usuarioCtrl;
    private JPanel panelContenido;
    private CardLayout cardLayout;

    public static void aplicarTema() {
        try {
            // Overrides globales para asegurar contraste (SET BEFORE L&F)
            UIManager.put("Panel.background", COLOR_FONDO);
            UIManager.put("Label.foreground", COLOR_TEXTO);
            UIManager.put("ComboBox.background", COLOR_CAMPO);
            UIManager.put("ComboBox.foreground", COLOR_TEXTO);
            UIManager.put("ComboBox.selectionBackground", COLOR_PRIMARIO);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            UIManager.put("ComboBox.buttonBackground", COLOR_CAMPO);
            UIManager.put("ComboBox.buttonShadow", COLOR_BORDE);
            
            UIManager.put("TextField.background", COLOR_CAMPO);
            UIManager.put("TextField.foreground", COLOR_TEXTO);
            UIManager.put("PasswordField.background", COLOR_CAMPO);
            UIManager.put("PasswordField.foreground", COLOR_TEXTO);
            
            UIManager.put("OptionPane.background", COLOR_PANEL);
            UIManager.put("OptionPane.messageForeground", COLOR_TEXTO);
            UIManager.put("OptionPane.foreground", COLOR_TEXTO);
            
            UIManager.put("Table.background", COLOR_PANEL);
            UIManager.put("Table.foreground", COLOR_TEXTO);
            UIManager.put("Table.gridColor", COLOR_BORDE);
            
            UIManager.put("TableHeader.background", new Color(35, 35, 50));
            UIManager.put("TableHeader.foreground", COLOR_TEXTO);
            
            UIManager.put("ScrollPane.background", COLOR_FONDO);
            UIManager.put("Viewport.background", COLOR_FONDO);

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Colores del tema
    static final Color COLOR_FONDO = new Color(18, 18, 24);
    static final Color COLOR_SIDEBAR = new Color(22, 22, 32);
    static final Color COLOR_PANEL = new Color(28, 28, 38);
    static final Color COLOR_PRIMARIO = new Color(255, 107, 53);
    static final Color COLOR_PRIMARIO_HOVER = new Color(255, 133, 89);
    static final Color COLOR_TEXTO = new Color(230, 230, 240);
    static final Color COLOR_TEXTO_SEC = new Color(160, 160, 180);
    static final Color COLOR_CAMPO = new Color(38, 38, 52);
    static final Color COLOR_BORDE = new Color(58, 58, 78);
    static final Color COLOR_EXITO = new Color(76, 175, 80);
    static final Color COLOR_ALERTA = new Color(255, 193, 7);
    static final Color COLOR_ERROR = new Color(255, 82, 82);
    static final Color COLOR_BTN_SIDEBAR = new Color(32, 32, 46);
    static final Color COLOR_BTN_ACTIVE = new Color(45, 30, 30); // Color solido para evitar superposicion visual

    private JButton btnActivo = null;

    public MainFrame(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        configurarVentana();
        inicializarComponentes();
        setVisible(true);
    }

    private void configurarVentana() {
        Usuario u = usuarioCtrl.getUsuarioLogueado();
        setTitle("SIGEIV-Volcano - " + u.getNombreUsuario() + " [" + obtenerNombreRol(u.getIdRol()) + "]");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // === SIDEBAR ===
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, COLOR_BORDE));

        // Logo en sidebar
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        logoPanel.setBackground(COLOR_SIDEBAR);
        logoPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblLogo = new JLabel("SIGEIV-Volcano");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(COLOR_PRIMARIO);
        logoPanel.add(lblLogo);
        logoPanel.setMaximumSize(new Dimension(190, 60));
        sidebar.add(logoPanel);

        // Separador
        sidebar.add(crearSeparador());

        // Informacion del usuario
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(COLOR_SIDEBAR);
        userPanel.setBorder(new EmptyBorder(8, 15, 8, 15));
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.setMaximumSize(new Dimension(190, 50));

        Usuario u = usuarioCtrl.getUsuarioLogueado();
        JLabel lblUsuario = new JLabel(u.getNombreUsuario());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuario.setForeground(COLOR_TEXTO);
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.add(lblUsuario);

        JLabel lblRol = new JLabel(obtenerNombreRol(u.getIdRol()));
        lblRol.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblRol.setForeground(COLOR_TEXTO_SEC);
        lblRol.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.add(lblRol);
        sidebar.add(userPanel);

        sidebar.add(crearSeparador());
        sidebar.add(Box.createVerticalStrut(8));

        // === PANEL DE CONTENIDO ===
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(COLOR_FONDO);

        // Agregar paneles de modulos
        panelContenido.add(new ProductoPanel(usuarioCtrl), "productos");
        panelContenido.add(new ClientePanel(usuarioCtrl), "clientes");
        panelContenido.add(new ProveedorPanel(usuarioCtrl), "proveedores");
        panelContenido.add(new CategoriaPanel(usuarioCtrl), "categorias");
        panelContenido.add(new UsuarioPanel(usuarioCtrl), "usuarios");
        panelContenido.add(new VentaPanel(usuarioCtrl), "ventas");
        panelContenido.add(new CompraPanel(usuarioCtrl), "compras");
        panelContenido.add(new InventarioPanel(usuarioCtrl), "inventario");
        panelContenido.add(new ReportePanel(usuarioCtrl), "reportes");

        // Botones del sidebar
        JButton btnProductos = crearBotonSidebar("Productos", "productos");
        JButton btnClientes = crearBotonSidebar("Clientes", "clientes");
        JButton btnProveedores = crearBotonSidebar("Proveedores", "proveedores");
        JButton btnCategorias = crearBotonSidebar("Categorias", "categorias");
        JButton btnUsuarios = crearBotonSidebar("Usuarios", "usuarios");
        JButton btnVentas = crearBotonSidebar("Ventas", "ventas");
        JButton btnCompras = crearBotonSidebar("Compras", "compras");
        JButton btnInventario = crearBotonSidebar("Inventario", "inventario");
        JButton btnReportes = crearBotonSidebar("Reportes", "reportes");

        // Control de visibilidad segun ROL
        boolean esAdmin = usuarioCtrl.esAdmin();
        boolean esVendedor = usuarioCtrl.esVendedor();
        
        btnProveedores.setVisible(esAdmin);
        btnCategorias.setVisible(esAdmin);
        btnUsuarios.setVisible(esAdmin);
        
        btnClientes.setVisible(esAdmin || esVendedor);
        btnVentas.setVisible(esAdmin || esVendedor);
        btnCompras.setVisible(esAdmin);

        sidebar.add(btnProductos);
        sidebar.add(btnClientes);
        sidebar.add(btnProveedores);
        sidebar.add(btnCategorias);
        sidebar.add(btnUsuarios);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(crearSeparador());
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnVentas);
        sidebar.add(btnCompras);
        sidebar.add(btnInventario);
        sidebar.add(btnReportes);

        sidebar.add(Box.createVerticalGlue());

        // Boton cerrar sesion
        sidebar.add(crearSeparador());
        JButton btnCerrar = crearBotonSidebar("Cerrar Sesion", null);
        btnCerrar.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this,
                "Deseas cerrar la sesion actual?", "Cerrar Sesion",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opcion == JOptionPane.YES_OPTION) {
                usuarioCtrl.cerrarSesion();
                dispose();
                new LoginFrame();
            }
        });
        sidebar.add(btnCerrar);
        sidebar.add(Box.createVerticalStrut(12));

        // Activar primer panel
        activarBoton(btnProductos);
        cardLayout.show(panelContenido, "productos");

        add(sidebar, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);
    }

    private JButton crearBotonSidebar(String texto, String panelName) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(190, 42));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != btnActivo) btn.setBackground(COLOR_BTN_SIDEBAR);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != btnActivo) btn.setBackground(COLOR_SIDEBAR);
            }
        });

        if (panelName != null) {
            btn.addActionListener(e -> {
                cardLayout.show(panelContenido, panelName);
                activarBoton(btn);
            });
        }

        return btn;
    }

    private void activarBoton(JButton btn) {
        if (btnActivo != null) {
            btnActivo.setBackground(COLOR_SIDEBAR);
            btnActivo.setForeground(COLOR_TEXTO);
        }
        btnActivo = btn;
        btnActivo.setBackground(COLOR_BTN_ACTIVE);
        btnActivo.setForeground(COLOR_PRIMARIO);
    }

    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_BORDE);
        sep.setBackground(COLOR_SIDEBAR);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(190, 1));
        return sep;
    }

    private String obtenerNombreRol(int idRol) {
        switch (idRol) {
            case 1: return "Administrador";
            case 2: return "Vendedor";
            case 3: return "Consultor";
            default: return "Desconocido";
        }
    }
}






