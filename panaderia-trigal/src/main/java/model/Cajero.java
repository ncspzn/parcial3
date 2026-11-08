package model;

public class Cajero extends Empleado {
    private String numCaja;

    public Cajero(String nombre, String documento,
                  String codigoEmpleado, int minutosDisponibles,
                  String numCaja) {
        super(nombre, documento, codigoEmpleado, minutosDisponibles);
        this.numCaja = numCaja;
    }

    public String getNumCaja() { return numCaja; }
    public void setNumCaja(String numCaja) { this.numCaja = numCaja; }
}
