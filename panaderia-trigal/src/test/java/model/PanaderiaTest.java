package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PanaderiaTest {

    private Panaderia panaderia;

    @BeforeEach
    void preparar() {
        // Cada prueba recibe una panadería nueva y vacía, y arma sus propios datos.
        panaderia = new Panaderia("El Trigal");
    }

    // ---------------------------------------------------------------
    // REGISTRAR
    // ---------------------------------------------------------------

    @Test
    void registrar_pedidoValido_quedaPendienteYNoReservaMinutos() {
        Panadero ana = new PanaderoArtesanal("Ana", "1", "E01", 100);
        panaderia.agregarEmpleado(ana);

        Pedido p = panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 25);

        assertEquals(EstadoPedido.PENDIENTE, p.getEstado());
        assertEquals(25, p.getCantidad());
        assertEquals(1, panaderia.getListaPedidos().size());
        assertSame(p, panaderia.buscarPedido("P01"));
        assertNull(p.getCodigoEmpleadoAsig());
        assertEquals(100, ana.getMinutosDisponibles());
    }

    @Test
    void registrar_codigoDuplicadoDeUnPedidoCancelado_lanzaIllegalArgumentException() {
        panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 25);
        panaderia.cancelarPedido("P01");

        assertThrows(IllegalArgumentException.class,
                () -> panaderia.registrarPedido("P01", "Otro Cliente", TipoPan.INTEGRAL, 10));

        assertEquals(1, panaderia.getListaPedidos().size());
        Pedido original = panaderia.getListaPedidos().get(0);
        assertEquals("P01", original.getCodigoPedido());
        assertEquals("Cafe Central", original.getNombreCliente());
        assertEquals(EstadoPedido.CANCELADO, original.getEstado());
    }

    @Test
    void registrar_cantidadCero_lanzaIllegalArgumentExceptionYHistorialSigueVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 0));

        assertTrue(panaderia.getListaPedidos().isEmpty());
    }

    // ---------------------------------------------------------------
    // ASIGNAR
    // ---------------------------------------------------------------

    @Test
    void asignar_seleccionaAlPanaderoQueMenosTarda() {
        Panadero ana = new PanaderoArtesanal("Ana", "1", "E01", 100);
        Panadero carlos = new PanaderoIndustrial("Carlos", "2", "E02", 45);
        panaderia.agregarEmpleado(ana);
        panaderia.agregarEmpleado(carlos);
        Pedido p = panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 25);

        // Las estimaciones no consumen tiempo.
        assertEquals(90, ana.calcularTiempo(p));
        assertEquals(45, carlos.calcularTiempo(p));

        panaderia.asignarPedido("P01");

        assertEquals(EstadoPedido.ASIGNADO, p.getEstado());
        assertEquals("E02", p.getCodigoEmpleadoAsig());
        assertEquals(45, p.getMinutosReserva());
        assertEquals(0, carlos.getMinutosDisponibles());
        assertEquals(100, ana.getMinutosDisponibles());
    }

    @Test
    void asignar_empateEntreArtesanales_seleccionaAlPrimeroRegistrado() {
        Panadero ana = new PanaderoArtesanal("Ana", "1", "E01", 80);
        Panadero diana = new PanaderoArtesanal("Diana", "2", "E02", 80);
        Panadero carlos = new PanaderoIndustrial("Carlos", "3", "E03", 54);
        panaderia.agregarEmpleado(ana);
        panaderia.agregarEmpleado(diana);
        panaderia.agregarEmpleado(carlos);
        Pedido p = panaderia.registrarPedido("P01", "Cafe Central", TipoPan.INTEGRAL, 13);

        assertEquals(55, carlos.calcularTiempo(p)); // 55 > 54: queda fuera
        assertEquals(80, ana.calcularTiempo(p));
        assertEquals(80, diana.calcularTiempo(p));

        panaderia.asignarPedido("P01");

        assertEquals(EstadoPedido.ASIGNADO, p.getEstado());
        assertEquals("E01", p.getCodigoEmpleadoAsig());
        assertEquals(80, p.getMinutosReserva());
        assertEquals(0, ana.getMinutosDisponibles());
        assertEquals(80, diana.getMinutosDisponibles());
        assertEquals(54, carlos.getMinutosDisponibles());
    }

    @Test
    void asignar_sinPanaderoConTiempoSuficiente_lanzaIllegalStateExceptionYConservaTodo() {
        Panadero carlos = new PanaderoIndustrial("Carlos", "1", "E01", 44);
        Cajero lucia = new Cajero("Lucía", "2", "E02", 120, "C1");
        panaderia.agregarEmpleado(carlos);
        panaderia.agregarEmpleado(lucia);
        Pedido p = panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 25);

        assertThrows(IllegalStateException.class, () -> panaderia.asignarPedido("P01"));

        assertEquals(EstadoPedido.PENDIENTE, p.getEstado());
        assertNull(p.getCodigoEmpleadoAsig());
        assertEquals(0, p.getMinutosReserva());
        assertEquals(44, carlos.getMinutosDisponibles());
        assertEquals(120, lucia.getMinutosDisponibles());
    }

    // ---------------------------------------------------------------
    // CANCELAR
    // ---------------------------------------------------------------

    @Test
    void cancelar_pedidoAsignado_devuelveSoloLosMinutosDeEsePedido() {
        Panadero carlos = new PanaderoIndustrial("Carlos", "1", "E01", 120);
        panaderia.agregarEmpleado(carlos);
        Pedido p1 = panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 25);
        Pedido p2 = panaderia.registrarPedido("P02", "Restaurante Sol", TipoPan.TRADICIONAL, 25);
        panaderia.asignarPedido("P01");
        panaderia.asignarPedido("P02");

        assertEquals(45, p1.getMinutosReserva());
        assertEquals(45, p2.getMinutosReserva());
        assertEquals(30, carlos.getMinutosDisponibles());

        panaderia.cancelarPedido("P01");

        assertEquals(75, carlos.getMinutosDisponibles());
        assertEquals(EstadoPedido.CANCELADO, p1.getEstado());
        assertEquals(EstadoPedido.ASIGNADO, p2.getEstado());
    }

    @Test
    void cancelar_pedidoPendiente_conservaLosMinutosDelPanadero() {
        Panadero ana = new PanaderoArtesanal("Ana", "1", "E01", 100);
        panaderia.agregarEmpleado(ana);
        Pedido p = panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 25);

        panaderia.cancelarPedido("P01");

        assertEquals(EstadoPedido.CANCELADO, p.getEstado());
        assertEquals(1, panaderia.getListaPedidos().size());
        assertSame(p, panaderia.buscarPedido("P01"));
        assertEquals(100, ana.getMinutosDisponibles());
    }

    @Test
    void cancelar_pedidoYaCancelado_lanzaIllegalStateExceptionYDevuelveMinutosUnaSolaVez() {
        Panadero carlos = new PanaderoIndustrial("Carlos", "1", "E01", 100);
        panaderia.agregarEmpleado(carlos);
        Pedido p = panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 25);
        panaderia.asignarPedido("P01");
        assertEquals(55, carlos.getMinutosDisponibles());

        panaderia.cancelarPedido("P01");
        assertEquals(100, carlos.getMinutosDisponibles());

        assertThrows(IllegalStateException.class, () -> panaderia.cancelarPedido("P01"));

        assertEquals(100, carlos.getMinutosDisponibles());
        assertEquals(EstadoPedido.CANCELADO, p.getEstado());
    }
}
