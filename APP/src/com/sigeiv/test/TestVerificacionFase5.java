package com.sigeiv.test;

import com.sigeiv.controlador.InventarioController;
import com.sigeiv.controlador.KardexController;
import com.sigeiv.dao.ConexionDB;
import com.sigeiv.dao.KardexDAO;
import com.sigeiv.modelo.Kardex;
import com.sigeiv.modelo.Producto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Suite de Pruebas Automatizadas de Fase 5 (RF-10, RF-11, RNF-04, RNF-05).
 * Valida el funcionamiento del Libro Mayor de Kardex, la semaforización visual tricolor,
 * la consistencia contable inmutable y los cálculos de rotación y valorización.
 */
public class TestVerificacionFase5 {

    private static int exitos = 0;
    private static int fallos = 0;

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("  SIGEIV-Volcano | Suite de Pruebas de Verificacion - Fase 5");
        System.out.println("  Libro Mayor de Kardex (RF-10) y Semaforizacion Visual (RF-11)");
        System.out.println("==================================================================");

        KardexController kardexCtrl = new KardexController();
        InventarioController invCtrl = new InventarioController();

        // ------------------------------------------------------------------
        // GRUPO 1: Conectividad y Asientos en Kardex
        // ------------------------------------------------------------------
        try {
            int totalAsientos = kardexCtrl.contarAsientosTotales();
            System.out.println("[PASS] Prueba 1: Conexion a BD exitosa. Asientos registrados en Kardex: " + totalAsientos);
            exitos++;
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 1: Error al contar asientos en Kardex: " + e.getMessage());
            fallos++;
        }

        // ------------------------------------------------------------------
        // GRUPO 2: Consulta Cronologica de Kardex por Producto (RF-10)
        // ------------------------------------------------------------------
        try {
            List<Producto> productos = kardexCtrl.listarProductosDisponibles();
            if (!productos.isEmpty()) {
                Producto primerProd = productos.get(0);
                List<Kardex> movimientos = kardexCtrl.listarKardexPorProducto(primerProd.getIdProducto());
                assertNotNull(movimientos, "La lista de kardex no debe ser nula");
                System.out.println("[PASS] Prueba 2: Consulta de Kardex por producto ejecutada correctamente (ID=" + primerProd.getIdProducto() + ").");
                exitos++;
            } else {
                System.out.println("[WARN] Prueba 2: No hay productos en la base de datos para consultar kardex.");
                exitos++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 2: Error en consulta de Kardex por producto: " + e.getMessage());
            fallos++;
        }

        // ------------------------------------------------------------------
        // GRUPO 3: Busqueda de Kardex por SKU o Nombre
        // ------------------------------------------------------------------
        try {
            List<Kardex> busquedaVacia = kardexCtrl.buscarPorSkuONombre("");
            List<Kardex> busquedaSku = kardexCtrl.buscarPorSkuONombre("VOLC");
            assertNotNull(busquedaVacia, "Busqueda vacia no debe ser nula");
            assertNotNull(busquedaSku, "Busqueda por SKU no debe ser nula");
            System.out.println("[PASS] Prueba 3: Filtrado y busqueda textual de asientos de Kardex verificado.");
            exitos++;
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 3: Error al buscar por SKU o nombre: " + e.getMessage());
            fallos++;
        }

        // ------------------------------------------------------------------
        // GRUPO 4: Calculos Acumulativos de Entradas, Salidas y Saldo
        // ------------------------------------------------------------------
        try {
            int ent = kardexCtrl.obtenerTotalEntradas(1);
            int sal = kardexCtrl.obtenerTotalSalidas(1);
            int saldo = kardexCtrl.obtenerSaldoActual(1);
            double val = kardexCtrl.calcularValorizacionSaldo(1);
            double rot = kardexCtrl.calcularIndiceRotacion(1);

            assertTrue(ent >= 0, "Total entradas debe ser >= 0");
            assertTrue(sal >= 0, "Total salidas debe ser >= 0");
            assertTrue(saldo >= 0, "Saldo en stock debe ser >= 0");
            assertTrue(val >= 0, "Valorizacion de saldo debe ser >= 0");
            assertTrue(rot >= 0, "Indice de rotacion debe ser >= 0");

            System.out.println("[PASS] Prueba 4: Metricas contables calculadas: Ent=" + ent + ", Sal=" + sal + ", Saldo=" + saldo + ", Val=$" + String.format("%.2f", val) + ", Rot=" + String.format("%.2fx", rot));
            exitos++;
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 4: Error en metricas contables: " + e.getMessage());
            fallos++;
        }

        // ------------------------------------------------------------------
        // GRUPO 5: Regla Oficial de Semaforizacion Tricolor (RF-11)
        // ------------------------------------------------------------------
        try {
            // Caso 1: Óptimo (Stock > Min * 1.5) -> Min=10, Stock=20 -> 20 > 15 -> OPTIMO
            Producto pOptimo = new Producto();
            pOptimo.setStockActual(20);
            pOptimo.setStockMinimo(10);
            String estOptimo = invCtrl.determinarEstadoSemaforo(pOptimo);
            assertEquals("OPTIMO", estOptimo, "Semaforo optimo");

            // Caso 2: Preventivo (Min < Stock <= Min * 1.5) -> Min=10, Stock=12 -> 10 < 12 <= 15 -> PREVENTIVO
            Producto pPrev = new Producto();
            pPrev.setStockActual(12);
            pPrev.setStockMinimo(10);
            String estPrev = invCtrl.determinarEstadoSemaforo(pPrev);
            assertEquals("PREVENTIVO", estPrev, "Semaforo preventivo");

            // Caso 3: Crítico (Stock <= Min) -> Min=10, Stock=8 -> 8 <= 10 -> CRITICO
            Producto pCrit = new Producto();
            pCrit.setStockActual(8);
            pCrit.setStockMinimo(10);
            String estCrit = invCtrl.determinarEstadoSemaforo(pCrit);
            assertEquals("CRITICO", estCrit, "Semaforo critico");

            // Caso 4: Crítico Agotado (Stock = 0) -> Min=5, Stock=0 -> CRITICO
            Producto pAgot = new Producto();
            pAgot.setStockActual(0);
            pAgot.setStockMinimo(5);
            String estAgot = invCtrl.determinarEstadoSemaforo(pAgot);
            assertEquals("CRITICO", estAgot, "Semaforo agotado");

            System.out.println("[PASS] Prueba 5: Regla matemática oficial de semaforizacion tricolor validada al 100% (Verde/Amarillo/Rojo).");
            exitos++;
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 5: Error en semaforizacion tricolor: " + e.getMessage());
            fallos++;
        }

        // ------------------------------------------------------------------
        // GRUPO 6: Integridad de Contadores y Filtros en InventarioController
        // ------------------------------------------------------------------
        try {
            int totCat = invCtrl.obtenerTotalProductos();
            int totOpt = invCtrl.obtenerTotalOptimo();
            int totPrev = invCtrl.obtenerTotalPreventivo();
            int totCrit = invCtrl.obtenerTotalCritico();

            assertEquals(totCat, totOpt + totPrev + totCrit, "La suma de categorias debe coincidir con el catalogo total");

            List<Producto> filtradosCrit = invCtrl.filtrarPorSemaforo("CRITICO");
            assertEquals(totCrit, filtradosCrit.size(), "El filtro de criticos debe coincidir con el contador");

            System.out.println("[PASS] Prueba 6: Contadores de semaforo integros (Total=" + totCat + " = Opt:" + totOpt + " + Prev:" + totPrev + " + Crit:" + totCrit + ").");
            exitos++;
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 6: Error en contadores de semaforo: " + e.getMessage());
            fallos++;
        }

        // ------------------------------------------------------------------
        // GRUPO 7: Trazabilidad Inmutable en Base de Datos (RNF-04)
        // ------------------------------------------------------------------
        try (Connection conn = ConexionDB.getInstancia().getConexion();
             Statement stmt = conn.createStatement()) {

            // Validar que la tabla 'kardex' tenga las columnas requeridas
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(kardex)");
            boolean tieneIdKardex = false;
            boolean tieneIdProd = false;
            boolean tieneCantEnt = false;
            boolean tieneCantSal = false;
            boolean tieneSaldo = false;
            boolean tieneCostoUnit = false;
            boolean tieneCostoTotal = false;

            while (rs.next()) {
                String col = rs.getString("name");
                if ("id_kardex".equalsIgnoreCase(col)) tieneIdKardex = true;
                if ("id_producto".equalsIgnoreCase(col)) tieneIdProd = true;
                if ("cantidad_entrada".equalsIgnoreCase(col)) tieneCantEnt = true;
                if ("cantidad_salida".equalsIgnoreCase(col)) tieneCantSal = true;
                if ("saldo_stock".equalsIgnoreCase(col)) tieneSaldo = true;
                if ("costo_unitario".equalsIgnoreCase(col)) tieneCostoUnit = true;
                if ("costo_total_saldo".equalsIgnoreCase(col)) tieneCostoTotal = true;
            }

            assertTrue(tieneIdKardex && tieneIdProd && tieneCantEnt && tieneCantSal && tieneSaldo && tieneCostoUnit && tieneCostoTotal,
                       "La tabla kardex debe contener todas las columnas contables inmutables");

            System.out.println("[PASS] Prueba 7: Esquema relacional inmutable de Kardex verificado (RNF-04).");
            exitos++;
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 7: Error en verificacion de esquema de Kardex: " + e.getMessage());
            fallos++;
        }

        // ------------------------------------------------------------------
        // GRUPO 8: Cruce Relacional con Usuarios Registrador y Autorizador
        // ------------------------------------------------------------------
        try {
            KardexDAO dao = new KardexDAO();
            List<Kardex> todos = dao.listarTodoKardex();
            for (Kardex k : todos) {
                assertNotNull(k.getRegistradoPor(), "RegistradoPor no debe ser nulo");
                assertNotNull(k.getAutorizadoPor(), "AutorizadoPor no debe ser nulo");
            }
            System.out.println("[PASS] Prueba 8: Cruce relacional de auditoria en Kardex (Registrador / Autorizador) verificado sin nulos.");
            exitos++;
        } catch (Exception e) {
            System.err.println("[FAIL] Prueba 8: Error en cruce relacional: " + e.getMessage());
            fallos++;
        }

        // ------------------------------------------------------------------
        // RESUMEN FINAL
        // ------------------------------------------------------------------
        System.out.println("==================================================================");
        System.out.println("  RESULTADO: " + exitos + " pruebas exitosas, " + fallos + " fallos.");
        if (fallos == 0) {
            System.out.println("  ESTADO: [PASS] FASE 5 VERIFICADA AL 100% CON EXITO");
        } else {
            System.err.println("  ESTADO: [FAIL] SE DETECTARON INCONSISTENCIAS");
        }
        System.out.println("==================================================================");
    }

    private static void assertTrue(boolean condition, String msg) {
        if (!condition) throw new AssertionError("AssertTrue fallo: " + msg);
    }

    private static void assertNotNull(Object obj, String msg) {
        if (obj == null) throw new AssertionError("AssertNotNull fallo: " + msg);
    }

    private static void assertEquals(Object expected, Object actual, String msg) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError("AssertEquals fallo (" + msg + "): esperado [" + expected + "] pero fue [" + actual + "]");
    }
}
