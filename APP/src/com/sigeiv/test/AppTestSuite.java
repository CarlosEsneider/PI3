package com.sigeiv.test;

import com.sigeiv.controlador.*;
import com.sigeiv.dao.ConexionDB;
import com.sigeiv.modelo.*;
import com.sigeiv.util.HashUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Suite de Pruebas Automatizadas para el sistema SIGEIV-Volcano.
 * Permite validar la integridad arquitectonica MVC, conexion a BD y reglas de negocio.
 */
public class AppTestSuite {

    private static int pruebasPasadas = 0;
    private static int pruebasTotales = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   SIGEIV-Volcano: Ejecucion de Suite de Pruebas");
        System.out.println("=================================================");

        testHashUtil();
        testConexionDB();
        testUsuarioController();
        testProductoController();
        testInventarioController();
        testVentaControllerTransaccional();

        System.out.println("=================================================");
        System.out.println(String.format(" Resultado Final: %d/%d pruebas superadas (%.1f%%)",
                pruebasPasadas, pruebasTotales, (pruebasPasadas * 100.0 / pruebasTotales)));
        System.out.println("=================================================");
    }

    private static void afirmar(String nombrePrueba, boolean condicion) {
        pruebasTotales++;
        if (condicion) {
            pruebasPasadas++;
            System.out.println("  [OK] " + nombrePrueba);
        } else {
            System.err.println("  [FAIL] " + nombrePrueba);
        }
    }

    private static void testHashUtil() {
        System.out.println("\n--- 1. Pruebas de Criptografia (HashUtil) ---");
        String texto = "admin123";
        String hashEsperado = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";
        String hashGenerado = HashUtil.sha256(texto);
        afirmar("Generacion SHA-256 coincide con el vector de prueba", hashEsperado.equalsIgnoreCase(hashGenerado));
        afirmar("Hash de texto vacio genera 64 caracteres hex", HashUtil.sha256("").length() == 64);
    }

    private static void testConexionDB() {
        System.out.println("\n--- 2. Pruebas de Conexion a Base de Datos ---");
        try {
            ConexionDB conInstancia = ConexionDB.getInstancia();
            afirmar("Obtencion de instancia Singleton ConexionDB", conInstancia != null);
            Connection conn = conInstancia.getConexion();
            afirmar("Conexion JDBC activa y no cerrada", conn != null && !conn.isClosed());
        } catch (Exception e) {
            afirmar("Excepcion en ConexionDB: " + e.getMessage(), false);
        }
    }

    private static void testUsuarioController() {
        System.out.println("\n--- 3. Pruebas de Controlador de Usuarios ---");
        UsuarioController ctrl = new UsuarioController();

        Usuario admin = ctrl.autenticar("admin", "admin123");
        afirmar("Autenticacion exitosa con credenciales validas de admin", admin != null && admin.getIdRol() == 1);
        afirmar("Verificacion de rol esAdmin()", ctrl.esAdmin());

        Usuario fallo = ctrl.autenticar("admin", "clave_invalida_xyz");
        afirmar("Rechazo de credenciales incorrectas", fallo == null);

        Usuario vacio = ctrl.autenticar("", "");
        afirmar("Rechazo de credenciales vacias", vacio == null);
    }

    private static void testProductoController() {
        System.out.println("\n--- 4. Pruebas de Controlador de Productos ---");
        ProductoController prodCtrl = new ProductoController();

        List<Producto> lista = prodCtrl.listarTodos();
        afirmar("Listado de productos no es nulo ni vacio", lista != null && !lista.isEmpty());

        Producto p = prodCtrl.buscarPorId(1);
        afirmar("Busqueda de producto ID #1 existente", p != null && p.getIdProducto() == 1);

        List<Producto> busqueda = prodCtrl.buscarPorNombre("Barril");
        afirmar("Filtro por nombre 'Barril' retorna coincidencias", busqueda != null && !busqueda.isEmpty());

        // Validacion defensiva
        Producto pInvalido = new Producto(0, "", -100, -5, -1, 0, 0, null);
        afirmar("Rechazo preventivo de producto con datos negativos o vacios", !prodCtrl.guardar(pInvalido));
    }

    private static void testInventarioController() {
        System.out.println("\n--- 5. Pruebas de Controlador de Inventario ---");
        InventarioController invCtrl = new InventarioController();

        int total = invCtrl.obtenerTotalProductos();
        afirmar("Conteo total de productos mayor a 0", total > 0);

        double valorTotal = invCtrl.calcularValorTotalInventario();
        afirmar("Valorizacion de inventario es positiva", valorTotal > 0);

        List<Producto> bajo = invCtrl.listarInventario(true);
        afirmar("Consulta de lista de stock bajo no nula", bajo != null);
    }

    private static void testVentaControllerTransaccional() {
        System.out.println("\n--- 6. Pruebas de Venta Transaccional y Validacion de Stock ---");
        VentaController ventaCtrl = new VentaController();
        ProductoController prodCtrl = new ProductoController();

        List<Producto> productos = prodCtrl.listarTodos();
        if (!productos.isEmpty()) {
            Producto primerProd = productos.get(0);
            
            // Intento de venta con cantidad que EXCEDE el stock disponible
            Venta ventaInvalida = new Venta(0, new Date(), 1, 1, 999999999.0);
            List<DetalleVenta> detallesInvalidos = new ArrayList<>();
            detallesInvalidos.add(new DetalleVenta(0, 0, primerProd.getIdProducto(), primerProd.getStockActual() + 99999, primerProd.getPrecio(), 999999999.0));

            boolean resultadoFallo = ventaCtrl.procesarVenta(ventaInvalida, detallesInvalidos);
            afirmar("Venta rechazada preventivamente por stock insuficiente", !resultadoFallo);
        }
    }
}
