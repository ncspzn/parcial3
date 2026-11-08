package model;

public abstract class Panadero extends Empleado {

    public Panadero(String nombre, String documento,
                    String codigoEmpleado, int minutosDisponibles) {
        super(nombre, documento, codigoEmpleado, minutosDisponibles);
    }

    /**
     * Calcula cuántos minutos tardaría este panadero en producir el pedido completo.
     * Es solo una consulta: no modifica los minutos disponibles.
     */
    public abstract int calcularTiempo(Pedido pedido);

    /** División entera hacia arriba: un lote o bandeja incompleto cuenta como uno. */
    protected int dividirHaciaArriba(int cantidad, int capacidad) {
        return (cantidad + capacidad - 1) / capacidad;
    }
}
