package model;

public class PanaderoArtesanal extends Panadero {
    private static final int PANES_POR_BANDEJA = 12;
    private static final int MINUTOS_BANDEJA_TRADICIONAL = 30;
    private static final int MINUTOS_BANDEJA_INTEGRAL = 40;

    public PanaderoArtesanal(String nombre, String documento,
                             String codigoEmpleado, int minutosDisponibles) {
        super(nombre, documento, codigoEmpleado, minutosDisponibles);
    }

    @Override
    public int calcularTiempo(Pedido pedido) {
        int bandejas = dividirHaciaArriba(pedido.getCantidad(), PANES_POR_BANDEJA);
        int minutosPorBandeja = (pedido.getTipo() == TipoPan.TRADICIONAL)
                ? MINUTOS_BANDEJA_TRADICIONAL
                : MINUTOS_BANDEJA_INTEGRAL;
        return bandejas * minutosPorBandeja;
    }
}
