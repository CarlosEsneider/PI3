package com.sigeiv.vista;

import com.sigeiv.controlador.UsuarioController;
import com.sigeiv.dao.*;
import com.sigeiv.modelo.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Panel de Reportes del sistema SIGEIV-Volcano.
 * Genera reportes de ventas por periodos y muestra productos mas vendidos.
 */
public class ReportePanel extends JPanel {

    private final UsuarioController usuarioCtrl;
    private final VentaDAO ventaDAO = new VentaDAO();
    private final DetalleVentaDAO detalleDAO = new DetalleVentaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private JTable tablaVentas, tablaTopProductos;
    private DefaultTableModel modeloVentas, modeloTop;
    private JLabel lblTotalVentas, lblCantidadVentas, lblPromedioVenta;
    private JComboBox<String> cmbPeriodo;
    private JTextField txtFechaInicio, txtFechaFin;

    public ReportePanel(UsuarioController ctrl) {
        this.usuarioCtrl = ctrl;
        setBackground(MainFrame.COLOR_FONDO);
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        inicializar();
        generarReporte();
    }

    private void inicializar() {
        // Titulo
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setOpaque(false);
        JLabel lblTitulo = new JLabel("Reportes de Ventas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(MainFrame.COLOR_TEXTO);
        panelTop.add(lblTitulo, BorderLayout.WEST);

        // Filtros de periodo
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelFiltros.setOpaque(false);

        cmbPeriodo = new JComboBox<>(new String[]{"Hoy", "Ultima Semana", "Ultimo Mes", "Ultimo Trimestre", "Personalizado"});
        ProductoPanel.estilizarCombo(cmbPeriodo);
        cmbPeriodo.setPreferredSize(new Dimension(160, 30));
        cmbPeriodo.addActionListener(e -> {
            boolean custom = cmbPeriodo.getSelectedIndex() == 4;
            txtFechaInicio.setEnabled(custom);
            txtFechaFin.setEnabled(custom);
            if (!custom) generarReporte();
        });

        JLabel lblDesde = new JLabel("Desde:");
        lblDesde.setForeground(MainFrame.COLOR_TEXTO_SEC);
        txtFechaInicio = new JTextField("2026-03-01", 10);
        ProductoPanel.estilizarCampo(txtFechaInicio);
        txtFechaInicio.setEnabled(false);

        JLabel lblHasta = new JLabel("Hasta:");
        lblHasta.setForeground(MainFrame.COLOR_TEXTO_SEC);
        txtFechaFin = new JTextField("2026-03-31", 10);
        ProductoPanel.estilizarCampo(txtFechaFin);
        txtFechaFin.setEnabled(false);

        JButton btnGenerar = ProductoPanel.crearBoton("Generar", MainFrame.COLOR_PRIMARIO);
        btnGenerar.addActionListener(e -> generarReporte());

        panelFiltros.add(cmbPeriodo);
        panelFiltros.add(lblDesde); panelFiltros.add(txtFechaInicio);
        panelFiltros.add(lblHasta); panelFiltros.add(txtFechaFin);
        panelFiltros.add(btnGenerar);
        panelTop.add(panelFiltros, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        // Tarjetas de resumen
        JPanel panelCards = new JPanel(new GridLayout(1, 3, 12, 0));
        panelCards.setOpaque(false);
        panelCards.setPreferredSize(new Dimension(0, 80));

        lblCantidadVentas = crearTarjeta(panelCards, "Ventas Realizadas", "0", MainFrame.COLOR_PRIMARIO);
        lblTotalVentas = crearTarjeta(panelCards, "Total Ingresos", "$0", MainFrame.COLOR_EXITO);
        lblPromedioVenta = crearTarjeta(panelCards, "Promedio por Venta", "$0", MainFrame.COLOR_ALERTA);

        // Panel central dividido
        JPanel panelCentral = new JPanel(new BorderLayout(0, 12));
        panelCentral.setOpaque(false);
        panelCentral.add(panelCards, BorderLayout.NORTH);

        // Tabla de ventas
        String[] colsVentas = {"ID", "Fecha", "Cliente", "Vendedor", "Total"};
        modeloVentas = new DefaultTableModel(colsVentas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaVentas = new JTable(modeloVentas);
        ProductoPanel.estilizarTabla(tablaVentas);

        JScrollPane scrollVentas = new JScrollPane(tablaVentas);
        scrollVentas.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scrollVentas.setBorder(new LineBorder(MainFrame.COLOR_BORDE, 1, true));
        panelCentral.add(scrollVentas, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // Panel derecho: Top productos
        JPanel panelDer = new JPanel(new BorderLayout());
        panelDer.setBackground(MainFrame.COLOR_PANEL);
        panelDer.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true), new EmptyBorder(12, 12, 12, 12)));
        panelDer.setPreferredSize(new Dimension(280, 0));

        JLabel lblTop = new JLabel("Productos Mas Vendidos");
        lblTop.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTop.setForeground(MainFrame.COLOR_PRIMARIO);
        panelDer.add(lblTop, BorderLayout.NORTH);

        String[] colsTop = {"Producto", "Uds. Vendidas", "Ingresos"};
        modeloTop = new DefaultTableModel(colsTop, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaTopProductos = new JTable(modeloTop);
        ProductoPanel.estilizarTabla(tablaTopProductos);

        JScrollPane scrollTop = new JScrollPane(tablaTopProductos);
        scrollTop.getViewport().setBackground(MainFrame.COLOR_PANEL);
        scrollTop.setBorder(BorderFactory.createEmptyBorder());
        panelDer.add(scrollTop, BorderLayout.CENTER);
        add(panelDer, BorderLayout.EAST);
    }

    private void generarReporte() {
        Date[] rango = calcularRango();
        Date inicio = rango[0], fin = rango[1];

        // Ventas
        List<Venta> ventas = ventaDAO.buscarPorFechas(inicio, fin);
        modeloVentas.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Cargar listas de lookup
        List<Cliente> clientes = clienteDAO.listarTodos();
        List<Usuario> usuarios = new com.sigeiv.dao.UsuarioDAO().listarTodos();

        double totalIngresos = 0;
        for (Venta v : ventas) {
            String clienteNombre = clientes.stream()
                .filter(c -> c.getIdCliente() == v.getIdCliente())
                .map(Cliente::getNombreCliente).findFirst().orElse("-");
            String vendedorNombre = usuarios.stream()
                .filter(u -> u.getIdUsuario() == v.getIdUsuario())
                .map(Usuario::getNombreUsuario).findFirst().orElse("-");

            modeloVentas.addRow(new Object[]{
                v.getIdVenta(),
                sdf.format(v.getFecha()),
                clienteNombre,
                vendedorNombre,
                String.format("$%,.2f", v.getTotal())
            });
            totalIngresos += v.getTotal();
        }

        // Actualizar tarjetas
        lblCantidadVentas.setText(String.valueOf(ventas.size()));
        lblTotalVentas.setText("$" + String.format("%,.0f", totalIngresos));
        lblPromedioVenta.setText("$" + String.format("%,.0f", ventas.isEmpty() ? 0 : totalIngresos / ventas.size()));

        // Top productos
        modeloTop.setRowCount(0);
        List<Object[]> topProductos = detalleDAO.productosMasVendidos(10);
        for (Object[] row : topProductos) {
            modeloTop.addRow(new Object[]{
                row[0],
                row[1],
                String.format("$%,.2f", (double) row[2])
            });
        }
    }

    private Date[] calcularRango() {
        Calendar cal = Calendar.getInstance();

        // 'fin' siempre es el ultimo instante del dia de hoy (23:59:59.999)
        Calendar calFin = Calendar.getInstance();
        calFin.set(Calendar.HOUR_OF_DAY, 23);
        calFin.set(Calendar.MINUTE, 59);
        calFin.set(Calendar.SECOND, 59);
        calFin.set(Calendar.MILLISECOND, 999);
        Date fin = calFin.getTime();
        Date inicio;

        switch (cmbPeriodo.getSelectedIndex()) {
            case 0: // Hoy
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                inicio = cal.getTime();
                break;
            case 1: // Ultima semana
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.add(Calendar.DAY_OF_MONTH, -7);
                inicio = cal.getTime();
                break;
            case 2: // Ultimo mes
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.add(Calendar.MONTH, -1);
                inicio = cal.getTime();
                break;
            case 3: // Ultimo trimestre
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.add(Calendar.MONTH, -3);
                inicio = cal.getTime();
                break;
            case 4: // Personalizado
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    inicio = sdf.parse(txtFechaInicio.getText().trim());
                    // Fin: ultimo instante del dia seleccionado
                    Calendar calFinCustom = Calendar.getInstance();
                    calFinCustom.setTime(sdf.parse(txtFechaFin.getText().trim()));
                    calFinCustom.set(Calendar.HOUR_OF_DAY, 23);
                    calFinCustom.set(Calendar.MINUTE, 59);
                    calFinCustom.set(Calendar.SECOND, 59);
                    calFinCustom.set(Calendar.MILLISECOND, 999);
                    fin = calFinCustom.getTime();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Formato de fecha invalido. Usa: YYYY-MM-DD",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    inicio = new Date(0);
                }
                break;
            default:
                inicio = new Date(0);
        }
        return new Date[]{ inicio, fin };
    }

    private JLabel crearTarjeta(JPanel contenedor, String titulo, String valor, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.COLOR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MainFrame.COLOR_BORDE, 1, true), new EmptyBorder(12, 16, 12, 16)));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(MainFrame.COLOR_TEXTO_SEC);
        lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTit);

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblVal.setForeground(color);
        lblVal.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblVal);

        contenedor.add(card);
        return lblVal;
    }
}






