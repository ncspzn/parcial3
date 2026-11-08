package model;

public class PanaderoIndustrial extends Panadero {
    private static final int MINUTOS_PREPARACION = 20;
    private static final int PANES_POR_LOTE = 30;
    private static final int MINUTOS_LOTE_TRADICIONAL = 25;
    private static final int MINUTOS_LOTE_INTEGRAL = 35;

    public PanaderoIndustrial(String nombre, String documento,
                              String codigoEmpleado, int minutosDisponibles) {
        super(nombre, documento, codigoEmpleado, minutosDisponibles);
    }

    @Override
    public int calcularTiempo(Pedido pedido) {
        int lotes = dividirHaciaArriba(pedido.getCantidad(), PANES_POR_LOTE);
        int minutosPorLote = (pedido.getTipo() == TipoPan.TRADICIONAL)
                ? MINUTOS_LOTE_TRADICIONAL
                : MINUTOS_LOTE_INTEGRAL;
        return MINUTOS_PREPARACION + lotes * minutosPorLote;
    }
}
