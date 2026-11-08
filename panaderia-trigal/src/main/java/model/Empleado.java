package model;

public abstract class Empleado extends Persona {
    private String codigoEmpleado;
    private int minutosDisponibles;

    public Empleado(String nombre, String documento,
                    String codigoEmpleado, int minutosDisponibles) {
        super(nombre, documento);
        this.codigoEmpleado = codigoEmpleado;
        this.minutosDisponibles = minutosDisponibles;
    }

    public String getCodigoEmpleado() { return codigoEmpleado; }
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }

    public int getMinutosDisponibles() { return minutosDisponibles; }
    public void setMinutosDisponibles(int minutosDisponibles) { this.minutosDisponibles = minutosDisponibles; }
}
